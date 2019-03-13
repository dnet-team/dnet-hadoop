package eu.dnetlib.collector.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dnetlib.collector.worker.model.ApiDescriptor;
import eu.dnetlib.collector.worker.plugins.CollectorPlugin;
import eu.dnetlib.collector.worker.utils.CollectorPluginEnumerator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 * DnetCollectortWorkerApplication is the main class responsible to start
 * the Dnet Collection into HDFS.
 * This module will be executed on the hadoop cluster and taking in input some parameters
 * that tells it which is the right collector plugin to use  and where store the data into HDFS path
 *
 *
 * @author Sandro La Bruzzo
 */
@SpringBootApplication
public class DnetCollectorWorkerApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DnetCollectorWorkerApplication.class);

	@Autowired
	private CollectorPluginEnumerator collectorPluginEnumerator;

	/**
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		SpringApplication.run(DnetCollectorWorkerApplication.class, args);
	}

	/**
	 * This module expect two arguments:
	 * 	path hdfs where store the sequential file.
	 * 	Json serialization of {@link ApiDescriptor}
	 */
	@Override
	public void run(final String... args) throws Exception {
		if (args.length == 0) { return; }
		if (args.length != 2) { throw new DnetCollectorException("Invalid number of parameters, expected: hdfs_path and json_api_description"); }

		final String hdfsPath = args[0];

		log.info("hdfsPath ="+hdfsPath);

		final String json = args[1];

		log.info("json = "+json);
		final ObjectMapper jsonMapper = new ObjectMapper();
		final ApiDescriptor api = jsonMapper.readValue(json, ApiDescriptor.class);

		final CollectorPlugin plugin = collectorPluginEnumerator.getPluginByProtocol(api.getProtocol());

		final String hdfsuri ="hdfs://hadoop-rm1.garr-pa1.d4science.org:8020";

		// ====== Init HDFS File System Object
		Configuration conf = new Configuration();
		// Set FileSystem URI
		conf.set("fs.defaultFS", hdfsuri);
		// Because of Maven
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		System.setProperty("hadoop.home.dir", "/");
		//Get the filesystem - HDFS
		FileSystem fs = FileSystem.get(URI.create(hdfsuri), conf);
		Path hdfswritepath = new Path(hdfsPath);

		log.info("Created path "+hdfswritepath.toString());

		try(SequenceFile.Writer writer = SequenceFile.createWriter(conf,
				SequenceFile.Writer.file(hdfswritepath), SequenceFile.Writer.keyClass(IntWritable.class),
				SequenceFile.Writer.valueClass(Text.class))) {

			final AtomicInteger counter = new AtomicInteger(0);
			final IntWritable key = new IntWritable(counter.get());
			final Text value = new Text();

			plugin.collect(api).forEach(content -> {

					key.set(counter.getAndIncrement());
					value.set(content);
				try {
					writer.append(key, value);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			});
		}
	}

}
