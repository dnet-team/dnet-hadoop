package eu.dnetlib.collector.worker;

import org.apache.commons.cli.*;

public class DnetCollectorWorkerArgumentParser {

    private final Options options;
    private String hdfsPath ;
    private String json;
    private String nameNode;
    private String user;
    private String rabbitUser;
    private String rabbitPassword;
    private String rabbitHost;
    private String rabbitOngoingQueue;
    private String rabbitReportQueue;
    private String workflowId;

    public DnetCollectorWorkerArgumentParser(){
         options = new Options();
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
    }

    public void parseArgument(final String[] args) throws DnetCollectorException {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            hdfsPath = cmd.getOptionValue("p");
            json = cmd.getOptionValue("a");
            nameNode = cmd.getOptionValue("n");
            user = cmd.getOptionValue("u");
            rabbitUser = cmd.getOptionValue("ru");
            rabbitPassword = cmd.getOptionValue("rp");
            rabbitHost = cmd.getOptionValue("rh");
            rabbitOngoingQueue = cmd.getOptionValue("ro");
            rabbitReportQueue = cmd.getOptionValue("rr");
            workflowId = cmd.getOptionValue("w");
        } catch (Throwable e){
            throw new DnetCollectorException("Error during parsing arguments ",e);
        }

    }

    public Options getOptions() {
        return options;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getNameNode() {
        return nameNode;
    }

    public void setNameNode(String nameNode) {
        this.nameNode = nameNode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRabbitUser() {
        return rabbitUser;
    }

    public void setRabbitUser(String rabbitUser) {
        this.rabbitUser = rabbitUser;
    }

    public String getRabbitPassword() {
        return rabbitPassword;
    }

    public void setRabbitPassword(String rabbitPassword) {
        this.rabbitPassword = rabbitPassword;
    }

    public String getRabbitHost() {
        return rabbitHost;
    }

    public void setRabbitHost(String rabbitHost) {
        this.rabbitHost = rabbitHost;
    }

    public String getRabbitOngoingQueue() {
        return rabbitOngoingQueue;
    }

    public void setRabbitOngoingQueue(String rabbitOngoingQueue) {
        this.rabbitOngoingQueue = rabbitOngoingQueue;
    }

    public String getRabbitReportQueue() {
        return rabbitReportQueue;
    }

    public void setRabbitReportQueue(String rabbitReportQueue) {
        this.rabbitReportQueue = rabbitReportQueue;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
