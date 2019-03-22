package eu.dnetlib.data.mdstore.manager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mdstore_current_versions")
public class MDStoreCurrentVersion implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4757725888593745773L;

	@Id
	@Column(name = "mdstore")
	private String mdstore;

	@Column(name = "current_version")
	private String currentVersion;

	public String getMdstore() {
		return mdstore;
	}

	public void setMdstore(final String mdstore) {
		this.mdstore = mdstore;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(final String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public static MDStoreCurrentVersion newInstance(final String mdId, final String versionId) {
		final MDStoreCurrentVersion cv = new MDStoreCurrentVersion();
		cv.setMdstore(mdId);
		cv.setCurrentVersion(versionId);
		return cv;
	}

	public static MDStoreCurrentVersion newInstance(final MDStoreVersion v) {
		return newInstance(v.getMdstore(), v.getId());
	}
}
