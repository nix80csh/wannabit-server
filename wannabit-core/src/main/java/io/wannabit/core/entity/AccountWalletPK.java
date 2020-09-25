package io.wannabit.core.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the account_wallet database table.
 * 
 */
@Embeddable
public class AccountWalletPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="idf_account", insertable=false, updatable=false, unique=true, nullable=false)
	private int idfAccount;

	@Column(unique=true, nullable=false, length=42)
	private String addr;

	public AccountWalletPK() {
	}
	public int getIdfAccount() {
		return this.idfAccount;
	}
	public void setIdfAccount(int idfAccount) {
		this.idfAccount = idfAccount;
	}
	public String getAddr() {
		return this.addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AccountWalletPK)) {
			return false;
		}
		AccountWalletPK castOther = (AccountWalletPK)other;
		return 
			(this.idfAccount == castOther.idfAccount)
			&& this.addr.equals(castOther.addr);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idfAccount;
		hash = hash * prime + this.addr.hashCode();
		
		return hash;
	}
}