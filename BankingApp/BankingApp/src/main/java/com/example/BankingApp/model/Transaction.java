package com.example.BankingApp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "timestamp")
	private LocalDateTime timestamp;
	@ManyToOne
	@JoinColumn(name="account_id")
	@JsonIgnore
	private Account account;
	public Transaction() {
		
	}
	public Transaction(BigDecimal amount, String type, LocalDateTime timestamp, Account account) {
		super();
		this.amount = amount;
		this.type = type;
		this.timestamp = timestamp;
		this.account = account;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
}
