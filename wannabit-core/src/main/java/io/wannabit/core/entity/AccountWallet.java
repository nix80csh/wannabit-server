package io.wannabit.core.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the account_wallet database table.
 * 
 */
@Entity
@Table(name="account_wallet")
@NamedQuery(name="AccountWallet.findAll", query="SELECT a FROM AccountWallet a")
public class AccountWallet implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AccountWalletPK id;

	@Column(length=20)
	private String name;

	@Column(name="reg_date", insertable=false, updatable=false, nullable=false)
	private Timestamp regDate;

	@Column(name="sign_material", length=150)
	private String signMaterial;

	@Column(name="type_blockchain", length=5)
	private String typeBlockchain;

	@Column(name="type_media", length=1)
	private String typeMedia;

	//bi-directional many-to-one association to Account
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="idf_account", nullable=false, insertable=false, updatable=false)
	private Account account;

	public AccountWallet() {
	}

	public AccountWalletPK getId() {
		return this.id;
	}

	public void setId(AccountWalletPK id) {
		this.id = id;
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

	public String getSignMaterial() {
		return this.signMaterial;
	}

	public void setSignMaterial(String signMaterial) {
		this.signMaterial = signMaterial;
	}

	public String getTypeBlockchain() {
		return this.typeBlockchain;
	}

	public void setTypeBlockchain(String typeBlockchain) {
		this.typeBlockchain = typeBlockchain;
	}

	public String getTypeMedia() {
		return this.typeMedia;
	}

	public void setTypeMedia(String typeMedia) {
		this.typeMedia = typeMedia;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}