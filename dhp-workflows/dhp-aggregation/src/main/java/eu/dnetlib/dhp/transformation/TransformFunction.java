package eu.dnetlib.dhp.collection;

import eu.dnetlib.dhp.model.mdstore.MetadataRecord;
import org.apache.spark.api.java.function.MapFunction;

public class TransformFunction implements MapFunction<MetadataRecord, MetadataRecord> {


    @Override
    public MetadataRecord call(MetadataRecord value) throws Exception {
        return null;
    }
}
