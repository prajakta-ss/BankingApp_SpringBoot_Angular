package com.example.BankingApp.service;


import com.example.BankingApp.exception.InsufficientFundsException;
import com.example.BankingApp.model.Account;
import com.example.BankingApp.repository.AccountRepository;
import com.example.BankingApp.repository.TransactionRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AccountService implements UserDetailsService  {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private  final TransactionRepository transRepo ;
    @Autowired
    private TransactionService transactionService;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,TransactionRepository transRepo) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.transRepo =transRepo;
    }

    public String register(Account account) {
    	if (account.getEmail() == null || account.getAddress() == null || account.getAge() <= 0) {
            throw new RuntimeException("Missing required account details.");
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);
        return generateToken(account.getUsername());
    }

    public String login(String username, String password) {
        Account user = accountRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return generateToken(username);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    private String generateToken(String username) {
        String jwtToken = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
            .compact();

        System.out.println("Generated JWT Token: " + jwtToken);  // Ensure the token has three parts separated by periods.
        return jwtToken;
    }



    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	 System.out.println("Loading user: " + username);
    	Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
            .withUsername(account.getUsername())
            .password(account.getPassword())
            
            .build();
    }
    public Account findAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        transactionService.recordTransaction(account, amount, "Deposit");
        
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal. Your balance is ₹" + account.getBalance());
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }


    public void transferAmount(Account fromAccount, String toUsername, BigDecimal amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        Account toAccount = accountRepository.findByUsername(toUsername)
            .orElseThrow(() -> new RuntimeException("Recipient not found"));

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transactionService.recordTransfer(fromAccount, toAccount, amount);

    }

}