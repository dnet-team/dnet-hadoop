package eu.dnetlib.dhp.collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dnetlib.dhp.model.mdstore.MetadataRecord;
import eu.dnetlib.dhp.model.mdstore.Provenance;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.LongAccumulator;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class GenerateNativeStoreSparkJob {


    public static MetadataRecord parseRecord (final String input, final String xpath, final String encoding, final Provenance provenance, final Long dateOfCollection, final LongAccumulator totalItems, final LongAccumulator invalidRecords)  {

        if(totalItems != null)
            totalItems.add(1);
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new ByteArrayInputStream(input.getBytes("UTF-8")));
            Node node = document.selectSingleNode(xpath);
            final String originalIdentifier = node.getText();
            if (StringUtils.isBlank(originalIdentifier)) {
                if (invalidRecords!= null)
                    invalidRecords.add(1);
                return null;
            }
            return new MetadataRecord(originalIdentifier, encoding, provenance, input, dateOfCollection);
        } catch (Throwable e) {
            if (invalidRecords!= null)
                invalidRecords.add(1);
            e.printStackTrace();
            return null;

        }
    }


    public static void main(String[] args) throws Exception {

        if (args == null || args.length != 6)
            //TODO Create a DHPWFException
            throw new Exception("unexpected number of parameters ");

        final String encoding = args[0];
        final long dateOfCollection = Long.valueOf(args[1]);
        final String jsonProvenance = args[2];
        final ObjectMapper jsonMapper = new ObjectMapper();
        final Provenance provenance = jsonMapper.readValue(jsonProvenance, Provenance.class);
        final String xpath = args[3];
        final String inputPath = args[4];
        final String outputPath = args[5];



        final SparkSession spark = SparkSession
                .builder()
                .appName("GenerateNativeStoreSparkJob")
                .master("yarn")
                .getOrCreate();

        final JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        final JavaPairRDD<IntWritable, Text> inputRDD = sc.sequenceFile(inputPath, IntWritable.class, Text.class);

        final LongAccumulator totalItems = sc.sc().longAccumulator("TotalItems");

        final LongAccumulator invalidRecords = sc.sc().longAccumulator("InvalidRecords");

        final JavaRDD<MetadataRecord> mappeRDD = inputRDD.map(item -> parseRecord(item._2().toString(), xpath, encoding, provenance, dateOfCollection, totalItems, invalidRecords))
                .filter(Objects::nonNull).distinct();

        final Encoder<MetadataRecord> encoder = Encoders.bean(MetadataRecord.class);
        final Dataset<MetadataRecord> mdstore = spark.createDataset(mappeRDD.rdd(), encoder);


        final LongAccumulator mdStoreRecords = sc.sc().longAccumulator("MDStoreRecords");
        mdStoreRecords.add(mdstore.count());
        System.out.println("totalItems.value() = " + totalItems.value());
        System.out.println("invalidRecords = " + invalidRecords.value());
        System.out.println("mdstoreRecords.value() = " + mdStoreRecords.value());

        mdstore.write().format("parquet").save(outputPath);







    }
}
