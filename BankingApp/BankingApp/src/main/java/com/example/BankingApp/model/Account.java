package com.example.BankingApp.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;


@Entity
@Table(name = "userDetails")
public class Account implements UserDetails {
 @Id
 @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
 @Column(name = "username", nullable = false,unique = true)
 private String username;
 
 @Column(name = "password", nullable = false)
 private String password;
 
 @Column(nullable = false, unique = true)
 private String email;
 
 @Column(nullable = false)
 private int age;
 
 @Column(nullable = false)
 private String address;
 
 @Column(nullable = false)
 private BigDecimal balance = BigDecimal.ZERO;
 


 @OneToMany(mappedBy="account")
 private List<Transaction> transaction;
 
 @Transient
 private Collection<? extends GrantedAuthority> authorities;

public Account() {
	super();
}

public Account(String username, String password, BigDecimal balance, List<Transaction> transaction,
		Collection<? extends GrantedAuthority> authorities,int age,String address,String email) {
	super();
	this.username = username;
	this.password = password;
	this.balance = balance;
	this.transaction = transaction;
	this.authorities = authorities;
	this.age=age;
	this.email=email;
	this.address=address;
}

public long getId() {
	return id;
}

public void setId(long id) {
	this.id = id;
}

public String getUsername() {
	return username;
}

public void setUsername(String username) {
	this.username = username;
}

public String getPassword() {
	return password;
}

public void setPassword(String password) {
	this.password = password;
}

public BigDecimal getBalance() {
	return balance;
}

public void setBalance(BigDecimal balance) {
	this.balance = balance;
}

public List<Transaction> getTransaction() {
	return transaction;
}

public void setTransaction(List<Transaction> transaction) {
	this.transaction = transaction;
}

public Collection<? extends GrantedAuthority> getAuthorities() {
	return authorities;
}

public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
	this.authorities = authorities;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public int getAge() {
	return age;
}

public void setAge(int age) {
	this.age = age;
}

public String getAddress() {
	return address;
}

public void setAddress(String address) {
	this.address = address;
}
 
 
 
}
