package eu.dnetlib.message;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void fromJsonTest() throws IOException {
        Message m = new Message();
        m.setWorkflowId("wId");
        m.setType(MessageType.ONGOING);
        m.setJobName("Collection");
        Map<String,String> body= new HashMap<>();
        body.put("parsedItem", "300");
        body.put("ExecutionTime", "30s");

        m.setBody(body);
        System.out.println("m = " + m);
        Message m1 = Message.fromJson(m.toString());
        assertEquals(m1.getWorkflowId(), m.getWorkflowId());
        assertEquals(m1.getType(), m.getType());
        assertEquals(m1.getJobName(), m.getJobName());

        assertNotNull(m1.getBody());
        m1.getBody().keySet().forEach(it -> assertEquals(m1.getBody().get(it), m.getBody().get(it)));
        assertEquals(m1.getJobName(), m.getJobName());
    }

    @Test
    public void toStringTest() {
        final String expectedJson= "{\"workflowId\":\"wId\",\"jobName\":\"Collection\",\"type\":\"ONGOING\",\"body\":{\"ExecutionTime\":\"30s\",\"parsedItem\":\"300\"}}";
        Message m = new Message();
        m.setWorkflowId("wId");
        m.setType(MessageType.ONGOING);
        m.setJobName("Collection");
        Map<String,String> body= new HashMap<>();
        body.put("parsedItem", "300");
        body.put("ExecutionTime", "30s");

        m.setBody(body);

        assertEquals(expectedJson,m.toString());


    }


    @Test
    public void sendMessageTest() throws Exception {

        final String expectedJson= "{\"workflowId\":\"wId\",\"jobName\":\"Collection\",\"type\":\"ONGOING\",\"body\":{\"ExecutionTime\":\"30s\",\"parsedItem\":\"300\"}}";
        Message m = new Message();
        m.setWorkflowId("wf_20190405_105048_275");
        m.setType(MessageType.ONGOING);
        m.setJobName("Collection");
        Map<String,String> body= new HashMap<>();
        body.put("progressCount", "100");
        body.put("ExecutionTime", "30s");

        m.setBody(body);

        MessageManager mm = new MessageManager("broker1-dev-dnet.d4science.org","r_admin", "9g8fed7gpohef9y84th98h", false,false, null);





        mm.sendMessage(m, "dev_ongoing");

        m.setType(MessageType.REPORT);

        body.put("mdStoreSize", "368");


        mm.sendMessage(m, "dev_report", true, false);
    }
}