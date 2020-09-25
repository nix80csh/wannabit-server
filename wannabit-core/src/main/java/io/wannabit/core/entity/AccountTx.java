package io.wannabit.core.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the account_tx database table.
 * 
 */
@Entity
@Table(name="account_tx")
@NamedQuery(name="AccountTx.findAll", query="SELECT a FROM AccountTx a")
public class AccountTx implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idf_account_tx", insertable=false, updatable=false, unique=true, nullable=false)
	private int idfAccountTx;

	@Column(name="reg_date", insertable=false, updatable=false, nullable=false)
	private Timestamp regDate;

	@Column(length=10)
	private String symbol;

	@Column(length=80)
	private String txhash;

	@Column(name="type_blockchain", length=5)
	private String typeBlockchain;

	//bi-directional many-to-one association to Account
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="idf_account", nullable=false)
	private Account account;

	public AccountTx() {
	}

	public int getIdfAccountTx() {
		return this.idfAccountTx;
	}

	public void setIdfAccountTx(int idfAccountTx) {
		this.idfAccountTx = idfAccountTx;
	}

	public Timestamp getRegDate() {
		return this.regDate;
	}

	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getTxhash() {
		return this.txhash;
	}

	public void setTxhash(String txhash) {
		this.txhash = txhash;
	}

	public String getTypeBlockchain() {
		return this.typeBlockchain;
	}

	public void setTypeBlockchain(String typeBlockchain) {
		this.typeBlockchain = typeBlockchain;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}