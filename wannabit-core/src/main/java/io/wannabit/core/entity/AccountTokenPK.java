package io.wannabit.core.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the account_token database table.
 * 
 */
@Embeddable
public class AccountTokenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="idf_account", insertable=false, updatable=false, unique=true, nullable=false)
	private int idfAccount;

	@Column(name="contract_addr", unique=true, nullable=false, length=42)
	private String contractAddr;

	public AccountTokenPK() {
	}
	public int getIdfAccount() {
		return this.idfAccount;
	}
	public void setIdfAccount(int idfAccount) {
		this.idfAccount = idfAccount;
	}
	public String getContractAddr() {
		return this.contractAddr;
	}
	public void setContractAddr(String contractAddr) {
		this.contractAddr = contractAddr;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AccountTokenPK)) {
			return false;
		}
		AccountTokenPK castOther = (AccountTokenPK)other;
		return 
			(this.idfAccount == castOther.idfAccount)
			&& this.contractAddr.equals(castOther.contractAddr);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idfAccount;
		hash = hash * prime + this.contractAddr.hashCode();
		
		return hash;
	}
}