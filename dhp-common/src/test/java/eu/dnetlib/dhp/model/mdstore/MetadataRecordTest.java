package eu.dnetlib.dhp.model.mdstore;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MetadataRecordTest {

    @Test
    public void getTimestamp() {

        MetadataRecord r = new MetadataRecord();
        assertTrue(r.getDateOfCollection() >0);
    }
}