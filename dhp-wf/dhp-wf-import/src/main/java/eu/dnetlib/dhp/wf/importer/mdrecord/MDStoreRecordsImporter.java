package eu.dnetlib.dhp.wf.importer.mdrecord;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.common.base.Preconditions;
import eu.dnetlib.dhp.common.WorkflowRuntimeParameters;
import eu.dnetlib.dhp.common.counter.NamedCounters;
import eu.dnetlib.dhp.common.counter.NamedCountersFileWriter;
import eu.dnetlib.dhp.common.java.PortBindings;
import eu.dnetlib.dhp.common.java.Process;
import eu.dnetlib.dhp.common.java.io.DataStore;
import eu.dnetlib.dhp.common.java.io.FileSystemPath;
import eu.dnetlib.dhp.common.java.porttype.AvroPortType;
import eu.dnetlib.dhp.common.java.porttype.PortType;
import eu.dnetlib.dhp.importer.schemas.ImportedRecord;
import eu.dnetlib.dhp.importer.schemas.RecordFormat;
import eu.dnetlib.dhp.wf.importer.facade.MDStoreFacade;
import eu.dnetlib.dhp.wf.importer.facade.ServiceFacadeUtils;
import org.apache.avro.file.DataFileWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import static eu.dnetlib.dhp.common.WorkflowRuntimeParameters.OOZIE_ACTION_OUTPUT_FILENAME;
import static eu.dnetlib.dhp.wf.importer.ImportWorkflowRuntimeParameters.IMPORT_MDSTORE_IDS_CSV;
import static eu.dnetlib.dhp.wf.importer.ImportWorkflowRuntimeParameters.IMPORT_MDSTORE_RECORD_MAXLENGTH;

/**
 * {@link MDStoreFacade} based metadata records importer.
 * @author mhorst
 *
 */
public class MDStoreRecordsImporter implements Process {

    protected static final String COUNTER_NAME_TOTAL = "TOTAL";
    
    protected static final String COUNTER_NAME_SIZE_EXCEEDED = "SIZE_EXCEEDED";
    
    protected static final String PORT_OUT_MDRECORDS = "mdrecords";
    
    private static final Logger log = Logger.getLogger(MDStoreRecordsImporter.class);
    
    private final static int progressLogInterval = 100000;
    
    private final NamedCountersFileWriter countersWriter = new NamedCountersFileWriter();
    
    private final Map<String, PortType> outputPorts = new HashMap<String, PortType>();

    
    //------------------------ CONSTRUCTORS -------------------
    
    public MDStoreRecordsImporter() {
        outputPorts.put(PORT_OUT_MDRECORDS, new AvroPortType(ImportedRecord.SCHEMA$));
    }
    
    //------------------------ LOGIC --------------------------
    
    @Override
    public Map<String, PortType> getInputPorts() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, PortType> getOutputPorts() {
        return outputPorts;
    }

    @Override
    public void run(PortBindings portBindings, Configuration conf,
            Map<String, String> parameters) throws Exception {
        
        Preconditions.checkArgument(parameters.containsKey(IMPORT_MDSTORE_IDS_CSV), 
                "unknown mdstore identifier, required parameter '%s' is missing!", IMPORT_MDSTORE_IDS_CSV);
        String mdStoreIdsCSV = parameters.get(IMPORT_MDSTORE_IDS_CSV);
        int recordMaxLength = parameters.containsKey(IMPORT_MDSTORE_RECORD_MAXLENGTH)?
                Integer.parseInt(parameters.get(IMPORT_MDSTORE_RECORD_MAXLENGTH)):Integer.MAX_VALUE;
        
        NamedCounters counters = new NamedCounters(new String[] { COUNTER_NAME_TOTAL, COUNTER_NAME_SIZE_EXCEEDED });
        
        if (StringUtils.isNotBlank(mdStoreIdsCSV) && !WorkflowRuntimeParameters.UNDEFINED_NONEMPTY_VALUE.equals(mdStoreIdsCSV)) {
            
            String[] mdStoreIds = StringUtils.split(mdStoreIdsCSV, WorkflowRuntimeParameters.DEFAULT_CSV_DELIMITER);

            try (DataFileWriter<ImportedRecord> recordWriter = getWriter(FileSystem.get(conf), portBindings)) {
                
                MDStoreFacade mdStoreFacade = ServiceFacadeUtils.instantiate(parameters);
                
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                parserFactory.setNamespaceAware(true);
                SAXParser saxParser = parserFactory.newSAXParser();
                MDRecordHandler mdRecordHandler = new MDRecordHandler();

                long startTime = System.currentTimeMillis();
                int currentCount = 0;
                
                for (String mdStoreId : mdStoreIds) {
                    for (String mdRecord : mdStoreFacade.deliverMDRecords(mdStoreId)) {
                        if (!StringUtils.isEmpty(mdRecord)) {
                            if (mdRecord.length() <= recordMaxLength) {
                                saxParser.parse(new InputSource(new StringReader(mdRecord)), mdRecordHandler);
                                String recordId = mdRecordHandler.getRecordId();
                                if (StringUtils.isNotBlank(recordId)) {
                                    recordWriter.append(
                                            ImportedRecord.newBuilder()
                                                .setId(recordId)
                                                .setBody(mdRecord)
                                                .setFormat(RecordFormat.XML)
                                                .build());
                                    counters.increment(COUNTER_NAME_TOTAL);
                                } else {
                                    log.error("skipping, unable to extract identifier from record: " + mdRecord);
                                }    
                            } else {
                                counters.increment(COUNTER_NAME_SIZE_EXCEEDED);
                                log.error("mdstore record maximum length (" + recordMaxLength + "): was exceeded: "
                                        + mdRecord.length() + ", record content:\n" + mdRecord);
                            }
                            
                        } else {
                            log.error("got empty metadata record from mdstore: " + mdStoreId);
                        }
                        currentCount++;
                        if (currentCount % progressLogInterval == 0) {
                            log.info("current progress: " + currentCount + ", last package of " + progressLogInterval
                                    + " processed in " + ((System.currentTimeMillis() - startTime) / 1000) + " secs");
                            startTime = System.currentTimeMillis();
                        }
                    } 
                }
                log.info("total number of processed records: " + currentCount);
            }
        }
        
        if (counters.currentValue(COUNTER_NAME_TOTAL)==0) {
            log.warn("parsed 0 metadata records from mdstores: " + mdStoreIdsCSV);
        }
        countersWriter.writeCounters(counters, System.getProperty(OOZIE_ACTION_OUTPUT_FILENAME));

    }
    
    /**
     * Provides {@link ImportedRecord} writer consuming records.
     */
    protected DataFileWriter<ImportedRecord> getWriter(FileSystem fs, PortBindings portBindings) throws IOException {
        return DataStore.create(
                new FileSystemPath(fs, portBindings.getOutput().get(PORT_OUT_MDRECORDS)), ImportedRecord.SCHEMA$);
    }

}
