package io.wannabit.core.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the account_token database table.
 * 
 */
@Entity
@Table(name="account_token")
@NamedQuery(name="AccountToken.findAll", query="SELECT a FROM AccountToken a")
public class AccountToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AccountTokenPK id;

	@Column(length=2)
	private String decimals;

	@Column(length=50)
	private String name;

	@Column(name="reg_date", insertable=false, updatable=false)
	private Timestamp regDate;

	@Column(length=10)
	private String symbol;

	@Column(name="type_blockchain", length=5)
	private String typeBlockchain;

	//bi-directional many-to-one association to Account
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="idf_account", nullable=false, insertable=false, updatable=false)
	private Account account;

	public AccountToken() {
	}

	public AccountTokenPK getId() {
		return this.id;
	}

	public void setId(AccountTokenPK id) {
		this.id = id;
	}

	public String getDecimals() {
		return this.decimals;
	}

	public void setDecimals(String decimals) {
		this.decimals = decimals;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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