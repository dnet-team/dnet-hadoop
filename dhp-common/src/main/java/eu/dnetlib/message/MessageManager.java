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
    public boolean sendMessage(final Message message, String queueName) throws Exception {
        try (Connection connection = createConnection(); Channel channel = createChannel(connection, queueName, this.durable, this.autodelete)) {

            channel.basicPublish("", queueName,null, message.toString().getBytes());
            return true;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendMessage(final Message message, String queueName, boolean durable_var, boolean autodelete_var) throws Exception {
        try (Connection connection = createConnection();  Channel channel = createChannel(connection, queueName, durable_var, autodelete_var)) {

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
