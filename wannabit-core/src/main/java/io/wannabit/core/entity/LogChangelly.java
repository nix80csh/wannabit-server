package io.wannabit.core.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the log_changelly database table.
 * 
 */
@Entity
@Table(name = "log_changelly")
@NamedQuery(name = "LogChangelly.findAll", query = "SELECT l FROM LogChangelly l")
public class LogChangelly implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId private LogChangellyPK id;

  @Column(length = 50) private String amountFrom;

  @Column(length = 50) private String amountTo;

  @Column(length = 5) private String apiExtraFee;

  @Column(length = 5) private String changellyFee;

  @Column(length = 50) private String createdAt;

  @Column(length = 10) private String currencyFrom;

  @Column(length = 10) private String currencyTo;

  @Column(length = 100) private String payinAddress;

  @Column(length = 50) private String payinExtraId;

  @Column(length = 100) private String payoutAddress;

  @Column(name = "reg_date", insertable = false, updatable = false,
      nullable = false) private Timestamp regDate;

  @Column(length = 10) private String status;

  public LogChangelly() {}

  public LogChangellyPK getId() {
    return this.id;
  }

  public void setId(LogChangellyPK id) {
    this.id = id;
  }

  public String getAmountFrom() {
    return this.amountFrom;
  }

  public void setAmountFrom(String amountFrom) {
    this.amountFrom = amountFrom;
  }

  public String getAmountTo() {
    return this.amountTo;
  }

  public void setAmountTo(String amountTo) {
    this.amountTo = amountTo;
  }

  public String getApiExtraFee() {
    return this.apiExtraFee;
  }

  public void setApiExtraFee(String apiExtraFee) {
    this.apiExtraFee = apiExtraFee;
  }

  public String getChangellyFee() {
    return this.changellyFee;
  }

  public void setChangellyFee(String changellyFee) {
    this.changellyFee = changellyFee;
  }

  public String getCreatedAt() {
    return this.createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getCurrencyFrom() {
    return this.currencyFrom;
  }

  public void setCurrencyFrom(String currencyFrom) {
    this.currencyFrom = currencyFrom;
  }

  public String getCurrencyTo() {
    return this.currencyTo;
  }

  public void setCurrencyTo(String currencyTo) {
    this.currencyTo = currencyTo;
  }

  public String getPayinAddress() {
    return this.payinAddress;
  }

  public void setPayinAddress(String payinAddress) {
    this.payinAddress = payinAddress;
  }

  public String getPayinExtraId() {
    return this.payinExtraId;
  }

  public void setPayinExtraId(String payinExtraId) {
    this.payinExtraId = payinExtraId;
  }

  public String getPayoutAddress() {
    return this.payoutAddress;
  }

  public void setPayoutAddress(String payoutAddress) {
    this.payoutAddress = payoutAddress;
  }

  public Timestamp getRegDate() {
    return this.regDate;
  }

  public void setRegDate(Timestamp regDate) {
    this.regDate = regDate;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
