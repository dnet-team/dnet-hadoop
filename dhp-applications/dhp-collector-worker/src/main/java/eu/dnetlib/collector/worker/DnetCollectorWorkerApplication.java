package eu.dnetlib.collector.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dnetlib.collector.worker.model.ApiDescriptor;
import eu.dnetlib.collector.worker.plugins.CollectorPlugin;
import eu.dnetlib.collector.worker.utils.CollectorPluginEnumerator;
import eu.dnetlib.message.Message;
import eu.dnetlib.message.MessageManager;
import eu.dnetlib.message.MessageType;
import org.apache.commons.cli.*;
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
import java.util.HashMap;
import java.util.Map;
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
		Options options = new Options();
		options.addOption(Option.builder("p")
				.longOpt("hdfsPath")
				.required(true)
				.desc("the path where storing the sequential file")
				.hasArg() // This option has an argument.
				.build());
		options.addOption(Option.builder("a")
				.longOpt("apidescriptor")
				.required(true)
				.desc("the Json enconding of the API Descriptor")
				.hasArg() // This option has an argument.
				.build());

		options.addOption(Option.builder("n")
				.longOpt("namenode")
				.required(true)
				.desc("the Name Node URI")
				.hasArg() // This option has an argument.
				.build());

		options.addOption(Option.builder("u")
				.longOpt("userHDFS")
				.required(true)
				.desc("the user wich create the hdfs seq file")
				.hasArg() // This option has an argument.
				.build());

		options.addOption(Option.builder("ru")
				.longOpt("rabbitUser")
				.required(true)
				.desc("the user to connect with RabbitMq for messaging")
				.hasArg() // This option has an argument.
				.build());

		options.addOption(Option.builder("rp")
				.longOpt("rabbitPassWord")
				.required(true)
				.desc("the password to connect with RabbitMq for messaging")
				.hasArg() // This option has an argument.
				.build());

		options.addOption(Option.builder("rh")
				.longOpt("rabbitHost")
				.required(true)
				.desc("the host of the RabbitMq server")
				.hasArg() // This option has an argument.
				.build());

		options.addOption(Option.builder("ro")
				.longOpt("rabbitOngoingQueue")
				.required(true)
				.desc("the name of the ongoing queue")
				.hasArg() // This option has an argument.
				.build());

		options.addOption(Option.builder("rr")
				.longOpt("rabbitReportQueue")
				.required(true)
				.desc("the name of the report queue")
				.hasArg() // This option has an argument.
				.build());


		options.addOption(Option.builder("w")
				.longOpt("workflowId")
				.required(true)
				.desc("the identifier of the dnet Workflow")
				.hasArg() // This option has an argument.
				.build());

		CommandLineParser parser = new DefaultParser();
		String hdfsPath ;
		String json;
		String nameNode;
		String user;
		String rabbitUser;
		String rabbitPassword;
		String rabbitHost;
		String rabbitOngoingQueue;
		String rabbitReportQueue;
		String workflowId;

		try {
			CommandLine cmd 	= parser.parse(options, args);
			hdfsPath 			= cmd.getOptionValue("p");
			json 				= cmd.getOptionValue("a");
			nameNode 			= cmd.getOptionValue("n");
			user 				= cmd.getOptionValue("u");
			rabbitUser 			= cmd.getOptionValue("ru");
			rabbitPassword		= cmd.getOptionValue("rp");
			rabbitHost 			= cmd.getOptionValue("rh");
			rabbitOngoingQueue 	= cmd.getOptionValue("ro");
			rabbitReportQueue  	= cmd.getOptionValue("rr");
			workflowId 			= cmd.getOptionValue("w");
		} catch (ParseException e) {
			System.out.println("Error on executing collector worker, missing parameter:");
			e.printStackTrace();
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("dhp-collector-worker", options);
			return;
		}
		log.info("hdfsPath ="+hdfsPath);
		log.info("json = "+json);

		final MessageManager manager = new MessageManager(rabbitHost, rabbitUser, rabbitPassword, false, false, null);


		final ObjectMapper jsonMapper = new ObjectMapper();
		final ApiDescriptor api = jsonMapper.readValue(json, ApiDescriptor.class);

		final CollectorPlugin plugin = collectorPluginEnumerator.getPluginByProtocol(api.getProtocol());

		final String hdfsuri =nameNode;

		// ====== Init HDFS File System Object
		Configuration conf = new Configuration();
		// Set FileSystem URI
		conf.set("fs.defaultFS", hdfsuri);
		// Because of Maven
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

		System.setProperty("HADOOP_USER_NAME", user);
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

			final Map<String, String> ongoingMap = new HashMap<>();
			final Map<String, String> reportMap = new HashMap<>();

			plugin.collect(api).forEach(content -> {

				key.set(counter.getAndIncrement());
				value.set(content);
				if (counter.get() % 10 ==0) {
					try {
						ongoingMap.put("ongoing", ""+counter.get());
						manager.sendMessage(new Message(workflowId,"Collection", MessageType.ONGOING, ongoingMap ), rabbitOngoingQueue, true, false);
					} catch (Exception e) {
						log.error("Error on sending message ", e);
					}
				}

				try {
					writer.append(key, value);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			});
			ongoingMap.put("ongoing", ""+counter.get());
			manager.sendMessage(new Message(workflowId,"Collection", MessageType.ONGOING, ongoingMap ), rabbitOngoingQueue, true, false);
			reportMap.put("collected", ""+counter.get());
			manager.sendMessage(new Message(workflowId,"Collection", MessageType.REPORT, reportMap ), rabbitReportQueue, true, false);

		}
	}

}
