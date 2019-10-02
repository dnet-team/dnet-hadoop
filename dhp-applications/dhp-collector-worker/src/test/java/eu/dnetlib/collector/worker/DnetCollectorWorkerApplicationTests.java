package eu.dnetlib.collector.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dnetlib.collector.worker.model.ApiDescriptor;
import eu.dnetlib.collector.worker.utils.CollectorPluginFactory;
import eu.dnetlib.message.Message;
import eu.dnetlib.message.MessageManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


public class DnetCollectorWorkerApplicationTests {


    private DnetCollectorWorkerArgumentParser argumentParser = mock(DnetCollectorWorkerArgumentParser.class);
    private MessageManager messageManager = mock(MessageManager.class);

    private DnetCollectorWorker worker;
    @Before
    public void setup() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String apiJson = mapper.writeValueAsString(getApi());
        when(argumentParser.getJson()).thenReturn(apiJson);
        when(argumentParser.getNameNode()).thenReturn("file://tmp/test.seq");
        when(argumentParser.getHdfsPath()).thenReturn("/tmp/file.seq");
        when(argumentParser.getUser()).thenReturn("sandro");
        when(argumentParser.getWorkflowId()).thenReturn("sandro");
        when(argumentParser.getRabbitOngoingQueue()).thenReturn("sandro");

        when(messageManager.sendMessage(any(Message.class), anyString(), anyBoolean(),anyBoolean())).thenAnswer(a -> {
            System.out.println("sent message: "+a.getArguments()[0]);
            return true;
        });
        when(messageManager.sendMessage(any(Message.class), anyString())).thenAnswer(a -> {
            System.out.println("Called");
            return true;
        });
        worker = new DnetCollectorWorker(new CollectorPluginFactory(), argumentParser, messageManager);
    }


    @After
    public void dropDown(){
        File f = new File("/tmp/test.seq");
        f.delete();
    }


    @Test
    public void testFindPlugin() throws Exception {
        final CollectorPluginFactory collectorPluginEnumerator = new CollectorPluginFactory();
        assertNotNull(collectorPluginEnumerator.getPluginByProtocol("oai"));
        assertNotNull(collectorPluginEnumerator.getPluginByProtocol("OAI"));
    }


    @Test
    public void testCollectionOAI() throws Exception {
        final ApiDescriptor api = new ApiDescriptor();
        api.setId("oai");
        api.setProtocol("oai");
        api.setBaseUrl("http://www.revista.vocesdelaeducacion.com.mx/index.php/index/oai");
        api.getParams().put("format", "oai_dc");
        ObjectMapper mapper = new ObjectMapper();
        assertNotNull(mapper.writeValueAsString(api));
    }

    @Test
    public void testFeeding() throws Exception {
        worker.collect();
    }

    private ApiDescriptor getApi() {
        final ApiDescriptor api = new ApiDescriptor();
        api.setId("oai");
        api.setProtocol("oai");
        api.setBaseUrl("http://www.revista.vocesdelaeducacion.com.mx/index.php/index/oai");
        api.getParams().put("format", "oai_dc");
        return api;
    }

}
