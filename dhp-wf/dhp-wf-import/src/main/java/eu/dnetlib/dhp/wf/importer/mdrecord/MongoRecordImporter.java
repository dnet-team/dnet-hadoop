package eu.dnetlib.dhp.wf.importer.mdrecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.dnetlib.dhp.common.java.PortBindings;
import eu.dnetlib.dhp.common.java.Process;
import eu.dnetlib.dhp.common.java.porttype.PortType;
import org.apache.hadoop.conf.Configuration;

public class MongoRecordImporter implements Process {

	private final Map<String, PortType> outputPorts = new HashMap<String, PortType>();

	@Override
	public Map<String, PortType> getInputPorts() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, PortType> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public void run(final PortBindings portBindings, final Configuration conf, final Map<String, String> parameters) throws Exception {

		/*
		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.input.uri", "mongodb://127.0.0.1/test.myCollection")
				.config("spark.mongodb.output.uri", "mongodb://127.0.0.1/test.myCollection")
				.getOrCreate();

		// Create a JavaSparkContext using the SparkSession's SparkContext object
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

		// More application logic would go here...

		jsc.close();
		*/

	}


}
