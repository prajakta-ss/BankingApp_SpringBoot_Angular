package com.example.BankingApp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.BankingApp.model.Account;
import com.example.BankingApp.model.Transaction;
import com.example.BankingApp.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepo;

    public void recordTransaction(Account account, BigDecimal amount, String type) {
        Transaction transaction = new Transaction(amount, type, LocalDateTime.now(), account);
        transactionRepo.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(Account account) {
        return transactionRepo.findByAccountId(account.getId());
    }

    public void recordTransfer(Account from, Account to, BigDecimal amount) {
        recordTransaction(from, amount, "Transfer Out to " + to.getUsername());
        recordTransaction(to, amount, "Transfer In from " + from.getUsername());
    }
}
