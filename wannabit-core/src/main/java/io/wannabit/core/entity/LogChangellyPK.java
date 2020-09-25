package io.wannabit.core.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the log_changelly database table.
 * 
 */
@Embeddable
public class LogChangellyPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="idf_account", unique=true, nullable=false)
	private int idfAccount;

	@Column(name="idf_changelly", unique=true, nullable=false, length=20)
	private String idfChangelly;

	public LogChangellyPK() {
	}
	public int getIdfAccount() {
		return this.idfAccount;
	}
	public void setIdfAccount(int idfAccount) {
		this.idfAccount = idfAccount;
	}
	public String getIdfChangelly() {
		return this.idfChangelly;
	}
	public void setIdfChangelly(String idfChangelly) {
		this.idfChangelly = idfChangelly;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof LogChangellyPK)) {
			return false;
		}
		LogChangellyPK castOther = (LogChangellyPK)other;
		return 
			(this.idfAccount == castOther.idfAccount)
			&& this.idfChangelly.equals(castOther.idfChangelly);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idfAccount;
		hash = hash * prime + this.idfChangelly.hashCode();
		
		return hash;
	}
}