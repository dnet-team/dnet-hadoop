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
@Table(name = "mdstore_versions")
public class MDStoreVersion implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4763494442274298339L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "mdstore")
	private String mdstore;

	@Column(name = "writing")
	private boolean writing;

	@Column(name = "readcount")
	private int readCount;

	@Column(name = "lastupdate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@Column(name = "size")
	private int size;

	public static MDStoreVersion newInstance(final String mdId, boolean writing) {
		final MDStoreVersion t = new MDStoreVersion();
		t.setId(mdId + "-" + new Date().getTime());
		t.setMdstore(mdId);
		t.setLastUpdate(null);
		t.setWriting(writing);
		t.setReadCount(0);
		t.setSize(0);
		return t;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMdstore() {
		return mdstore;
	}

	public void setMdstore(String mdstore) {
		this.mdstore = mdstore;
	}

	public boolean isWriting() {
		return writing;
	}

	public void setWriting(boolean writing) {
		this.writing = writing;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
