package eu.dnetlib.collector.worker;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.dnetlib.collector.worker.model.ApiDescriptor;
import eu.dnetlib.collector.worker.utils.CollectorPluginEnumerator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DnetCollectorWorkerApplicationTests {

	@Autowired
	private ApplicationContext ctx;

	@Test
	public void testFindPlugin() throws Exception {
		final CollectorPluginEnumerator collectorPluginEnumerator = ctx.getBean(CollectorPluginEnumerator.class);
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

		System.out.println(mapper.writeValueAsString(api));
	}

}
