package eu.dnetlib.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class MessageManager {

    private final String messageHost;

    private final String username;

    private final String password;

    private Connection connection;

    private Map<String , Channel> channels = new HashMap<>();

    private  boolean durable;

    private  boolean autodelete;

    final private LinkedBlockingQueue<Message> queueMessages;

    public MessageManager(String messageHost, String username, String password, final LinkedBlockingQueue<Message> queueMessages) {
        this.queueMessages = queueMessages;
        this.messageHost = messageHost;
        this.username = username;
        this.password = password;
    }


    public MessageManager(String messageHost, String username, String password, boolean durable, boolean autodelete, final LinkedBlockingQueue<Message> queueMessages) {
        this.queueMessages = queueMessages;
        this.messageHost = messageHost;
        this.username = username;
        this.password = password;

        this.durable = durable;
        this.autodelete = autodelete;
    }

    private Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.messageHost);
        factory.setUsername(this.username);
        factory.setPassword(this.password);
        return factory.newConnection();
    }

    private Channel createChannel(final Connection connection, final String queueName, final boolean durable, final boolean autodelete ) throws Exception {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 10000);
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, durable, false, this.autodelete, args);
        return channel;
    }

    private Channel getOrCreateChannel(final String queueName, boolean durable, boolean autodelete) throws Exception {
        if (channels.containsKey(queueName)) {
            return  channels.get(queueName);
        }

        if (this.connection == null) {
            this.connection = createConnection();
        }
        channels.put(queueName, createChannel(this.connection, queueName, durable, autodelete));
        return channels.get(queueName);
    }



    public void close() throws IOException {
        channels.values().forEach(ch-> {
            try {
                ch.close();
            } catch (Exception e) {
                //TODO LOG
            }
        });

        this.connection.close();
    }

    public boolean sendMessage(final Message message, String queueName) throws Exception {
        try {
            Channel channel = getOrCreateChannel(queueName, this.durable, this.autodelete);
            channel.basicPublish("", queueName,null, message.toString().getBytes());
            return true;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendMessage(final Message message, String queueName, boolean durable_var, boolean autodelete_var) throws Exception {
        try {
            Channel channel = getOrCreateChannel(queueName, durable_var, autodelete_var);
            channel.basicPublish("", queueName,null, message.toString().getBytes());
            return true;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void startConsumingMessage(final String queueName, final boolean durable, final boolean autodelete)  throws Exception{

        Channel channel = createChannel(createConnection(), queueName, durable, autodelete);
        channel.basicConsume(queueName, false, new MessageConsumer(channel,queueMessages));
    }
}
