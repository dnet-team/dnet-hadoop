package eu.dnetlib.collector.worker.plugins;

import java.util.stream.Stream;

import eu.dnetlib.collector.worker.DnetCollectorException;
import eu.dnetlib.collector.worker.model.ApiDescriptor;

public interface CollectorPlugin {

	Stream<String> collect(ApiDescriptor api) throws DnetCollectorException;
}
