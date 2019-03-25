package eu.dnetlib.data.mdstore.manager.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mdstores")
public class MDStore implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3160530489149700055L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "format")
	private String format;

	@Column(name = "layout")
	private String layout;

	@Column(name = "interpretation")
	private String interpretation;

	@Column(name = "datasource_name")
	private String datasourceName;

	@Column(name = "datasource_id")
	private String datasourceId;

	@Column(name = "api_id")
	private String apiId;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(final String format) {
		this.format = format;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(final String layout) {
		this.layout = layout;
	}

	public String getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(final String interpretation) {
		this.interpretation = interpretation;
	}

	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(final String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(final String datasourceId) {
		this.datasourceId = datasourceId;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(final String apiId) {
		this.apiId = apiId;
	}

	public static MDStore newInstance(final String format, final String layout, final String interpretation) {
		return newInstance(format, layout, interpretation, null, null, null);
	}

	public static MDStore newInstance(final String format,
			final String layout,
			final String interpretation,
			final String dsName,
			final String dsId,
			final String apiId) {
		final MDStore md = new MDStore();
		md.setId("md-" + UUID.randomUUID());
		md.setFormat(format);
		md.setLayout(layout);
		md.setInterpretation(interpretation);
		md.setDatasourceName(dsName);
		md.setDatasourceId(dsId);
		md.setApiId(apiId);
		return md;
	}

}
