package eu.dnetlib.data.mdstore.manager.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "mdstores_with_info")
public class MDStoreWithInfo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8445784770687571492L;

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

	@Column(name = "current_version")
	private String currentVersion;

	@Column(name = "lastupdate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@Column(name = "size")
	private long size = 0;

	@Column(name = "n_versions")
	private long numberOfVersions = 0;

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

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(final String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public long getSize() {
		return size;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public long getNumberOfVersions() {
		return numberOfVersions;
	}

	public void setNumberOfVersions(final long numberOfVersions) {
		this.numberOfVersions = numberOfVersions;
	}

}
