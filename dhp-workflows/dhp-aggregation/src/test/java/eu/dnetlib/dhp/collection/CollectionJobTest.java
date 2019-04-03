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
        GenerateNativeStoreSparkJob.main(new String[] {"-e", "XML","-d", ""+System.currentTimeMillis(),"-p", new ObjectMapper().writeValueAsString(provenance), "-x","./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']","-i","/home/sandro/Downloads/oai_1","-o","/home/sandro/Downloads/mdstore_result"});
        System.out.println(new ObjectMapper().writeValueAsString(provenance));
    }


    @Test
    public void transformTest () throws Exception {

        TransformSparkJobNode.main(new String[]{"-o","/home/sandro/Downloads/mdstore_cleande","-i","/home/sandro/Downloads/mdstore_result"});




    }



    @Test
    public void testGenerationMetadataRecord() throws Exception {

        final String xml = IOUtils.toString(this.getClass().getResourceAsStream("./record.xml"));

        MetadataRecord record = GenerateNativeStoreSparkJob.parseRecord(xml, "./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']", "XML", new Provenance("foo", "bar", "ns_prefix"), System.currentTimeMillis(), null,null);

        assert record != null;
        System.out.println(record.getId());
        System.out.println(record.getOriginalId());


    }


    @Test
    public void TestEquals () throws IOException {

        final String xml = IOUtils.toString(this.getClass().getResourceAsStream("./record.xml"));
        MetadataRecord record = GenerateNativeStoreSparkJob.parseRecord(xml, "./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']", "XML", new Provenance("foo", "bar", "ns_prefix"), System.currentTimeMillis(), null,null);
        MetadataRecord record1 = GenerateNativeStoreSparkJob.parseRecord(xml, "./*[local-name()='record']/*[local-name()='header']/*[local-name()='identifier']", "XML", new Provenance("foo", "bar", "ns_prefix"), System.currentTimeMillis(), null,null);
        assert record != null;
        record.setBody("ciao");
        assert record1 != null;
        record1.setBody("mondo");
        Assert.assertEquals(record, record1);

    }

}
