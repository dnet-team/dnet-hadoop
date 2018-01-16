package eu.dnetlib.dhp.common.protobuf;

import com.google.protobuf.Message;
import org.apache.avro.generic.IndexedRecord;

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
public interface AvroToProtoBufConverter<IN extends IndexedRecord, OUT extends Message> {
    String convertIntoKey(IN datum);
    OUT convertIntoValue(IN datum);
}
