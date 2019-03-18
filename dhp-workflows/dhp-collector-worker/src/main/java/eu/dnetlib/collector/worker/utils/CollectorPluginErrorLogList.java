package eu.dnetlib.collector.worker.utils;

import java.util.LinkedList;

public class CollectorPluginErrorLogList extends LinkedList<String> {

	private static final long serialVersionUID = -6925786561303289704L;

	@Override
	public String toString() {
		String log = new String();
		int index = 0;
		for (final String errorMessage : this) {
			log += String.format("Retry #%s: %s / ", index++, errorMessage);
		}
		return log;
	}

}
