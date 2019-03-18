package eu.dnetlib.collector.worker.plugins.oai;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.dnetlib.collector.worker.utils.HttpConnector;

@Component
public class OaiIteratorFactory {

	@Autowired
	private HttpConnector httpConnector;

	public Iterator<String> newIterator(final String baseUrl, final String mdFormat, final String set, final String fromDate, final String untilDate) {
		return new OaiIterator(baseUrl, mdFormat, set, fromDate, untilDate, httpConnector);
	}

}
