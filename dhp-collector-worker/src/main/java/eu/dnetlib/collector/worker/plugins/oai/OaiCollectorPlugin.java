package eu.dnetlib.collector.worker.plugins.oai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import eu.dnetlib.collector.worker.DnetCollectorException;
import eu.dnetlib.collector.worker.model.ApiDescriptor;
import eu.dnetlib.collector.worker.plugins.CollectorPlugin;
import eu.dnetlib.collector.worker.utils.DnetWorkerCollector;

@Component
@DnetWorkerCollector("oai")
public class OaiCollectorPlugin implements CollectorPlugin {

	private static final String FORMAT_PARAM = "format";
	private static final String OAI_SET_PARAM = "set";
	private static final Object OAI_FROM_DATE_PARAM = "fromDate";
	private static final Object AI_UNTIL_DATE_PARAM = "untilDate";

	@Autowired
	private OaiIteratorFactory oaiIteratorFactory;

	@Override
	public Stream<String> collect(final ApiDescriptor api) throws DnetCollectorException {
		final String baseUrl = api.getBaseUrl();
		final String mdFormat = api.getParams().get(FORMAT_PARAM);
		final String setParam = api.getParams().get(OAI_SET_PARAM);
		final String fromDate = api.getParams().get(OAI_FROM_DATE_PARAM);
		final String untilDate = api.getParams().get(AI_UNTIL_DATE_PARAM);

		final List<String> sets = new ArrayList<>();
		if (setParam != null) {
			sets.addAll(Lists.newArrayList(Splitter.on(",").omitEmptyStrings().trimResults().split(setParam)));
		}
		if (sets.isEmpty()) {
			// If no set is defined, ALL the sets must be harvested
			sets.add("");
		}

		if (baseUrl == null || baseUrl.isEmpty()) { throw new DnetCollectorException("Param 'baseurl' is null or empty"); }

		if (mdFormat == null || mdFormat.isEmpty()) { throw new DnetCollectorException("Param 'mdFormat' is null or empty"); }

		if (fromDate != null && !fromDate.matches("\\d{4}-\\d{2}-\\d{2}")) { throw new DnetCollectorException("Invalid date (YYYY-MM-DD): " + fromDate); }

		if (untilDate != null && !untilDate.matches("\\d{4}-\\d{2}-\\d{2}")) { throw new DnetCollectorException("Invalid date (YYYY-MM-DD): " + untilDate); }

		final Iterator<Iterator<String>> iters = sets.stream()
				.map(set -> oaiIteratorFactory.newIterator(baseUrl, mdFormat, set, fromDate, untilDate))
				.iterator();

		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(Iterators.concat(iters), Spliterator.ORDERED), false);
	}
}
