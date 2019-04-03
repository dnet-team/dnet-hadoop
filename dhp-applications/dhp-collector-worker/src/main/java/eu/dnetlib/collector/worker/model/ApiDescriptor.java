package eu.dnetlib.collector.worker.model;

import java.util.HashMap;
import java.util.Map;


//TODO sholud be moved on dhp-common
public class ApiDescriptor {

	private String id;

	private String baseUrl;

	private String protocol;

	private Map<String, String> params = new HashMap<>();

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(final String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(final HashMap<String, String> params) {
		this.params = params;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(final String protocol) {
		this.protocol = protocol;
	}

}
