package com.example.BankingApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.BankingApp.model.Account;

public interface AccountRepository extends JpaRepository<Account,Long>{
	Optional<Account>findByUsername(String username);

}
