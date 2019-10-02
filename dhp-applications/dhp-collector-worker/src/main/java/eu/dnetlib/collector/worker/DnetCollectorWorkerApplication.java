package eu.dnetlib.collector.worker;

import eu.dnetlib.collector.worker.utils.CollectorPluginFactory;
import eu.dnetlib.message.MessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DnetCollectortWorkerApplication is the main class responsible to start
 * the Dnet Collection into HDFS.
 * This module will be executed on the hadoop cluster and taking in input some parameters
 * that tells it which is the right collector plugin to use  and where store the data into HDFS path
 *
 * @author Sandro La Bruzzo
 */

public class DnetCollectorWorkerApplication {

    private static final Logger log = LoggerFactory.getLogger(DnetCollectorWorkerApplication.class);

    private static CollectorPluginFactory collectorPluginFactory = new CollectorPluginFactory();

    private static DnetCollectorWorkerArgumentParser argumentParser = new DnetCollectorWorkerArgumentParser();


    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        argumentParser.parseArgument(args);
        log.info("hdfsPath =" + argumentParser.getHdfsPath());
        log.info("json = " + argumentParser.getJson());
        final MessageManager manager = new MessageManager(argumentParser.getRabbitHost(), argumentParser.getRabbitUser(), argumentParser.getRabbitPassword(), false, false, null);
        final DnetCollectorWorker worker = new DnetCollectorWorker(collectorPluginFactory, argumentParser, manager);
        worker.collect();


    }


}
