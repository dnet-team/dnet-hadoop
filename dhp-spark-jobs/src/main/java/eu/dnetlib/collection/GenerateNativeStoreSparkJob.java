package eu.dnetlib.collection;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.apache.spark.sql.functions.array_contains;

public class GenerateNativeStoreSparkJob {


    public static void main(String[] args) {

        final SparkSession spark = SparkSession
                .builder()
                .appName("GenerateNativeStoreSparkJob")
                .master("local[*]")
                .getOrCreate();

        final JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        JavaPairRDD<IntWritable, Text> f = sc.sequenceFile("/home/sandro/Downloads/mdstore_oai", IntWritable.class, Text.class);

        String first = f.map(a -> a._2().toString()).first();


        final List<StructField> fields = new ArrayList<>();

        fields.add(DataTypes.createStructField("id", DataTypes.StringType, false));
        fields.add(DataTypes.createStructField("format", DataTypes.StringType, false));
        fields.add(DataTypes.createStructField("formatName", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("body", DataTypes.StringType, true));

        JavaRDD<Row> mdRdd = f.map((Function<Tuple2<IntWritable, Text>, Row>) item -> RowFactory.create("" + item._1().get(), "xml", null, item._2().toString()));

        final StructType schema = DataTypes.createStructType(fields);
        Dataset<Row> ds = spark.createDataFrame(mdRdd, schema);

//        ds.write().save("/home/sandro/Downloads/test.parquet");

        Publication p2 = new Publication();
        p2.setDates(Collections.singletonList("2018-09-09"));
        p2.setTitles(Collections.singletonList("Titolo 2"));
        p2.setIdentifiers(Collections.singletonList(new PID("pmID", "1234567")));

        Publication p1 = new Publication();
        p1.setDates(Collections.singletonList("2018-09-09"));
        p1.setTitles(Collections.singletonList("Titolo 1"));
        p1.setIdentifiers(Collections.singletonList(new PID("doi", "1234567")));




        Encoder<Publication> encoder = Encoders.bean(Publication.class);

        Dataset<Publication> dp = spark.createDataset(Arrays.asList(p1,p2), encoder);


        long count = dp.where(array_contains(new Column("identifiers.schema"), "doi")).count();

        System.out.println("count = " + count);

        System.out.println(ds.count());


    }
}
