package io.wannabit.core.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the account database table.
 * 
 */
@Entity
@Table(name="account")
@NamedQuery(name="Account.findAll", query="SELECT a FROM Account a")
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idf_account", insertable=false, updatable=false, unique=true, nullable=false)
	private int idfAccount;

	@Column(name="auth_code_email", length=20)
	private String authCodeEmail;

	@Column(name="auth_code_password", length=20)
	private String authCodePassword;

	@Column(length=50)
	private String email;

	@Column(name="otp_key", length=21)
	private String otpKey;

	@Column(length=64)
	private String password;

	@Column(name="reg_date", insertable=false, updatable=false, nullable=false)
	private Timestamp regDate;

	//bi-directional many-to-one association to AccountToken
	@OneToMany(mappedBy="account")
	private List<AccountToken> accountTokens;

	//bi-directional many-to-one association to AccountTx
	@OneToMany(mappedBy="account")
	private List<AccountTx> accountTxs;

	//bi-directional many-to-one association to AccountWallet
	@OneToMany(mappedBy="account")
	private List<AccountWallet> accountWallets;

	public Account() {
	}

	public int getIdfAccount() {
		return this.idfAccount;
	}

	public void setIdfAccount(int idfAccount) {
		this.idfAccount = idfAccount;
	}

	public String getAuthCodeEmail() {
		return this.authCodeEmail;
	}

	public void setAuthCodeEmail(String authCodeEmail) {
		this.authCodeEmail = authCodeEmail;
	}

	public String getAuthCodePassword() {
		return this.authCodePassword;
	}

	public void setAuthCodePassword(String authCodePassword) {
		this.authCodePassword = authCodePassword;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOtpKey() {
		return this.otpKey;
	}

	public void setOtpKey(String otpKey) {
		this.otpKey = otpKey;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getRegDate() {
		return this.regDate;
	}

	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}

	public List<AccountToken> getAccountTokens() {
		return this.accountTokens;
	}

	public void setAccountTokens(List<AccountToken> accountTokens) {
		this.accountTokens = accountTokens;
	}

	public AccountToken addAccountToken(AccountToken accountToken) {
		getAccountTokens().add(accountToken);
		accountToken.setAccount(this);

		return accountToken;
	}

	public AccountToken removeAccountToken(AccountToken accountToken) {
		getAccountTokens().remove(accountToken);
		accountToken.setAccount(null);

		return accountToken;
	}

	public List<AccountTx> getAccountTxs() {
		return this.accountTxs;
	}

	public void setAccountTxs(List<AccountTx> accountTxs) {
		this.accountTxs = accountTxs;
	}

	public AccountTx addAccountTx(AccountTx accountTx) {
		getAccountTxs().add(accountTx);
		accountTx.setAccount(this);

		return accountTx;
	}

	public AccountTx removeAccountTx(AccountTx accountTx) {
		getAccountTxs().remove(accountTx);
		accountTx.setAccount(null);

		return accountTx;
	}

	public List<AccountWallet> getAccountWallets() {
		return this.accountWallets;
	}

	public void setAccountWallets(List<AccountWallet> accountWallets) {
		this.accountWallets = accountWallets;
	}

	public AccountWallet addAccountWallet(AccountWallet accountWallet) {
		getAccountWallets().add(accountWallet);
		accountWallet.setAccount(this);

		return accountWallet;
	}

	public AccountWallet removeAccountWallet(AccountWallet accountWallet) {
		getAccountWallets().remove(accountWallet);
		accountWallet.setAccount(null);

		return accountWallet;
	}

}