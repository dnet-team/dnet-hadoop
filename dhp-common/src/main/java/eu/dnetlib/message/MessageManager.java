package eu.dnetlib.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

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
    private Channel createChannel(final String queueName, final boolean durable, final boolean autodelete ) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.messageHost);
        factory.setUsername(this.username);
        factory.setPassword(this.password);
        Connection connection = factory.newConnection();
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-message-ttl", 10000);
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, durable, false, this.autodelete, args);
        return channel;
    }
    public boolean sendMessage(final Message message, String queueName) throws Exception {
        try (Channel channel = createChannel(queueName, this.durable, this.autodelete)) {

            channel.basicPublish("", queueName,null, message.toString().getBytes());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendMessage(final Message message, String queueName, boolean durable_var, boolean autodelete_var) throws Exception {
        try (Channel channel = createChannel(queueName, durable_var, autodelete_var)) {

            channel.basicPublish("", queueName,null, message.toString().getBytes());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public void startConsumingMessage(final String queueName, final boolean durable, final boolean autodelete)  throws Exception{
        Channel channel = createChannel(queueName, durable, autodelete);
        channel.basicConsume(queueName, false, new MessageConsumer(channel,queueMessages));
    }
}
