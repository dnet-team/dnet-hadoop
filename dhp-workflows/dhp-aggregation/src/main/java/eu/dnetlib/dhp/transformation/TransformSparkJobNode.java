package eu.dnetlib.dhp.transformation;

import eu.dnetlib.dhp.model.mdstore.MetadataRecord;
import eu.dnetlib.dhp.utils.DHPUtils;
import eu.dnetlib.message.Message;
import eu.dnetlib.message.MessageManager;
import eu.dnetlib.message.MessageType;
import org.apache.commons.cli.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.LongAccumulator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class TransformSparkJobNode {



    public static void main(String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        Options options = new Options();

        options.addOption(Option.builder("mt")
                .longOpt("master")
                .required(true)
                .desc("should be local or yarn")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("d")
                .longOpt("dateOfCollection")
                .required(true)
                .desc("the date of collection")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("i")
                .longOpt("input")
                .required(true)
                .desc("input path of the sequence file")
                .hasArg() // This option has an argument.
                .build());
        options.addOption(Option.builder("o")
                .longOpt("output")
                .required(true)
                .desc("output path of the mdstore")
                .hasArg()
                .build());
        options.addOption(Option.builder("w")
                .longOpt("workflowId")
                .required(true)
                .desc("the identifier of the dnet Workflow")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("tr")
                .longOpt("transformationRule")
                .required(true)
                .desc("the transformation Rule to apply to the input MDStore")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("ru")
                .longOpt("rabbitUser")
                .required(false)
                .desc("the user to connect with RabbitMq for messaging")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("rp")
                .longOpt("rabbitPassWord")
                .required(false)
                .desc("the password to connect with RabbitMq for messaging")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("rh")
                .longOpt("rabbitHost")
                .required(false)
                .desc("the host of the RabbitMq server")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("ro")
                .longOpt("rabbitOngoingQueue")
                .required(false)
                .desc("the name of the ongoing queue")
                .hasArg() // This option has an argument.
                .build());

        options.addOption(Option.builder("rr")
                .longOpt("rabbitReportQueue")
                .required(false)
                .desc("the name of the report queue")
                .hasArg() // This option has an argument.
                .build());


        final CommandLineParser parser      = new DefaultParser();
        final CommandLine cmd               = parser.parse( options, args);

        final String inputPath              = cmd.getOptionValue("i");
        final String outputPath             = cmd.getOptionValue("o");
        final String workflowId             = cmd.getOptionValue("w");
        final String trasformationRule      = extractXSLTFromTR(DHPUtils.decompressString(cmd.getOptionValue("tr")));
        final String master                 = cmd.getOptionValue("mt");
        final String rabbitUser 			= cmd.getOptionValue("ru");
        final String rabbitPassword		    = cmd.getOptionValue("rp");
        final String rabbitHost 			= cmd.getOptionValue("rh");
        final String rabbitReportQueue  	= cmd.getOptionValue("rr");
        final long dateOfCollection         = new Long(cmd.getOptionValue("d"));

        final SparkSession spark = SparkSession
                .builder()
                .appName("TransformStoreSparkJob")
                .master(master)
                .getOrCreate();

        final Encoder<MetadataRecord> encoder = Encoders.bean(MetadataRecord.class);
        final Dataset<MetadataRecord> mdstoreInput = spark.read().format("parquet").load(inputPath).as(encoder);

        final LongAccumulator totalItems = spark.sparkContext().longAccumulator("TotalItems");
        final LongAccumulator errorItems = spark.sparkContext().longAccumulator("errorItems");
        final LongAccumulator transformedItems = spark.sparkContext().longAccumulator("transformedItems");

        final TransformFunction transformFunction = new TransformFunction(totalItems, errorItems, transformedItems, trasformationRule, dateOfCollection) ;
        mdstoreInput.map(transformFunction, encoder).write().format("parquet").save(outputPath);


        if (rabbitHost != null) {

            System.out.println("SEND FINAL REPORT");

            final Map<String, String> reportMap = new HashMap<>();
            reportMap.put("inputItem" , ""+ totalItems.value());
            reportMap.put("invalidRecords", "" + errorItems.value());
            reportMap.put("mdStoreSize", "" + transformedItems.value());
            final MessageManager manager = new MessageManager(rabbitHost, rabbitUser, rabbitPassword, false, false, null);


            System.out.println(new Message(workflowId, "Transform", MessageType.REPORT, reportMap));
            manager.sendMessage(new Message(workflowId, "Transform", MessageType.REPORT, reportMap), rabbitReportQueue, true, false);
            manager.close();
        }

    }


    private static String extractXSLTFromTR(final String tr) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new ByteArrayInputStream(tr.getBytes()));
        Node node = document.selectSingleNode("//CODE/*[local-name()='stylesheet']");
        return node.asXML();
    }
}