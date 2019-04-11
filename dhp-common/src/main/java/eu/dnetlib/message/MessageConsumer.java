package eu.dnetlib.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageConsumer extends DefaultConsumer {

    final LinkedBlockingQueue<Message> queueMessages;


    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     * @param queueMessages
     */
    public MessageConsumer(Channel channel, LinkedBlockingQueue<Message> queueMessages) {
        super(channel);
        this.queueMessages = queueMessages;
    }


    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        final String json = new String(body, StandardCharsets.UTF_8);
        Message message = Message.fromJson(json);
        try {
            this.queueMessages.put(message);
            System.out.println("Receiving Message "+message);
        } catch (InterruptedException e) {
            if (message.getType()== MessageType.REPORT)
                throw new RuntimeException("Error on sending message");
            else {
                //TODO LOGGING EXCEPTION
            }
        } finally {
            getChannel().basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
