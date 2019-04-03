package eu.dnetlib.dhp.transformation;

import eu.dnetlib.dhp.model.mdstore.MetadataRecord;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.util.LongAccumulator;

public class TransformFunction implements MapFunction<MetadataRecord, MetadataRecord> {


    private final LongAccumulator totalItems;

    public TransformFunction(LongAccumulator totalItems) {
        this.totalItems= totalItems;
    }

    @Override
    public MetadataRecord call(MetadataRecord value) throws Exception {
        totalItems.add(1);
        return value;
    }
}
