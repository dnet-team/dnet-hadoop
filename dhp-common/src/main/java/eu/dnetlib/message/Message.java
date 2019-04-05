package eu.dnetlib.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class Message {

    private String workflowId;

    private String jobName;

    private MessageType type;

    private Map<String, String> body;


    public static Message fromJson(final String json) throws IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(json, Message.class);


    }


    public Message() {



    }

    public Message(String workflowId, String jobName, MessageType type, Map<String, String> body) {
        this.workflowId = workflowId;
        this.jobName = jobName;
        this.type = type;
        this.body = body;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final ObjectMapper jsonMapper = new ObjectMapper();
        try {
            return jsonMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
