package eu.dnetlib.dhp.collection;

import eu.dnetlib.dhp.model.mdstore.MetadataRecord;
import eu.dnetlib.dhp.transformation.TransformFunction;
import org.apache.commons.cli.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.LongAccumulator;

public class TransformSparkJobNode {

    public static void main(String[] args) throws ParseException {
        Options options = new Options();

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

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse( options, args);

        final String inputPath = cmd.getOptionValue("i");
        final String outputPath = cmd.getOptionValue("o");

        final SparkSession spark = SparkSession
                .builder()
                .appName("GenerateNativeStoreSparkJob")
                .master("local")
                .getOrCreate();

        final Encoder<MetadataRecord> encoder = Encoders.bean(MetadataRecord.class);
        final Dataset<MetadataRecord> mdstoreInput = spark.read().format("parquet").load(inputPath).as(encoder);
        final LongAccumulator totalItems = spark.sparkContext().longAccumulator("TotalItems");

        final TransformFunction mfunc = new TransformFunction(totalItems);
        mdstoreInput.map(mfunc, encoder).write().format("parquet").save(outputPath);
        System.out.println("totalItems = " + totalItems.value());

    }
}
