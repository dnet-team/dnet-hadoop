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
@Table(name = "transactions")
public class Transaction implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -4763494442274298339L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "mdstore")
	private String mdstore;

	@Column(name = "active")
	private boolean active;

	@Column(name = "lastupdate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@Column(name = "size")
	private int size;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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
