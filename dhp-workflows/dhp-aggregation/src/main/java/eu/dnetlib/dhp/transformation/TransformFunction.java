package eu.dnetlib.dhp.transformation;

import eu.dnetlib.dhp.model.mdstore.MetadataRecord;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.util.LongAccumulator;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

public class TransformFunction implements MapFunction<MetadataRecord, MetadataRecord> {


    private final LongAccumulator totalItems;
    private final LongAccumulator errorItems;
    private final LongAccumulator transformedItems;
    private final String trasformationRule;

    private final long dateOfTransformation;


    public TransformFunction(LongAccumulator totalItems, LongAccumulator errorItems, LongAccumulator transformedItems, final String trasformationRule, long dateOfTransformation) {
        this.totalItems= totalItems;
        this.errorItems = errorItems;
        this.transformedItems = transformedItems;
        this.trasformationRule = trasformationRule;
        this.dateOfTransformation = dateOfTransformation;
    }

    @Override
    public MetadataRecord call(MetadataRecord value) {
        totalItems.add(1);
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.newTransformer();
            final StreamSource xsltSource = new StreamSource(new ByteArrayInputStream(trasformationRule.getBytes()));
            final Transformer transformer = factory.newTransformer(xsltSource);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final StringWriter output = new StringWriter();
            transformer.transform(new StreamSource(new ByteArrayInputStream(value.getBody().getBytes())), new StreamResult(output));
            final String xml = output.toString();
            value.setBody(xml);
            value.setDateOfCollection(dateOfTransformation);
            transformedItems.add(1);
            return value;
        }catch (Throwable e) {
            errorItems.add(1);
            return null;
        }
    }
}