package eu.dnetlib.collector.worker.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.dnetlib.collector.worker.plugins.CollectorPlugin;

@Component
public class CollectorPluginEnumerator {

	@Autowired
	private List<CollectorPlugin> plugins;

	public CollectorPlugin getPluginByProtocol(final String protocol) {
		return plugins.stream()
				.filter(p -> p.getClass().isAnnotationPresent(DnetWorkerCollector.class))
				.filter(p -> p.getClass().getAnnotation(DnetWorkerCollector.class).value().equalsIgnoreCase(protocol))
				.findFirst()
				.get();
	}

}
