package eu.dnetlib.dhp.collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dnetlib.dhp.model.mdstore.MetadataRecord;
import eu.dnetlib.dhp.model.mdstore.Provenance;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CollectionJobTest {


    @Test
    public void test () throws Exception {
        Provenance provenance = new Provenance("pippo", "puppa", "ns_prefix");


//        GenerateNativeStoreSparkJob.main(new String[] {"XML", ""+System.currentTimeMillis(), new ObjectMapper().writeValueAsString(provenance), "./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']","/home/sandro/Downloads/mdstore_oai","/home/sandro/Downloads/mdstore_result"});
        System.out.println(new ObjectMapper().writeValueAsString(provenance));
    }


    @Test
    public void testGenerationMetadataRecord() throws Exception {

        final String xml = IOUtils.toString(this.getClass().getResourceAsStream("./record.xml"));

        MetadataRecord record = GenerateNativeStoreSparkJob.parseRecord(xml, "./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']", "XML", new Provenance("foo", "bar", "ns_prefix"), System.currentTimeMillis(), null,null);

        System.out.println(record.getId());
        System.out.println(record.getOriginalId());


    }


    @Test
    public void TestEquals () throws IOException {

        final String xml = IOUtils.toString(this.getClass().getResourceAsStream("./record.xml"));
        MetadataRecord record = GenerateNativeStoreSparkJob.parseRecord(xml, "./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']", "XML", new Provenance("foo", "bar", "ns_prefix"), System.currentTimeMillis(), null,null);
        MetadataRecord record1 = GenerateNativeStoreSparkJob.parseRecord(xml, "./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']", "XML", new Provenance("foo", "bar", "ns_prefix"), System.currentTimeMillis(), null,null);
        record.setBody("ciao");
        record1.setBody("mondo");
        Assert.assertTrue(record.equals(record1));

    }

}
