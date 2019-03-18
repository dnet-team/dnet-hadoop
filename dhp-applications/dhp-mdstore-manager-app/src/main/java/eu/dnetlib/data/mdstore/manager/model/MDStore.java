package eu.dnetlib.data.mdstore.manager.model;

import java.io.Serializable;

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

	@Column(name = "datasource_id")
	private String datasourceId;

	@Column(name = "api_id")
	private String apiId ;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}





}
