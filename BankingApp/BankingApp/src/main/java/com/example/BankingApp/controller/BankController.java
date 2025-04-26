package com.example.BankingApp.controller;

import com.example.BankingApp.model.Account;
import com.example.BankingApp.model.Transaction;
import com.example.BankingApp.service.AccountService;
import com.example.BankingApp.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:4200")
public class BankController {

    private static final Logger logger = LoggerFactory.getLogger(BankController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TransactionService transactionService;
    // Register Account
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerAccount(@RequestBody Account account) {
        logger.debug("Register request received for username: {}", account.getUsername());
        try {
            String jwt = accountService.register(account);
            logger.debug("Account registered successfully for username: {}", account.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Registration failed for username: {} - {}", account.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
 // Login
   
//    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
//        logger.debug("Login request received for username: {}", username);
//        try {
//            UsernamePasswordAuthenticationToken token =
//                    new UsernamePasswordAuthenticationToken(username, password);
//            authenticationManager.authenticate(token); // ✅ This is the correct place
//
//            SecurityContextHolder.getContext().setAuthentication(token);
//            String jwt = accountService.login(username, password); // returns JWT
//            logger.debug("Login successful for username: {}", username);
//            return ResponseEntity.ok(jwt);
//        } catch (Exception e) {
//            logger.error("Login failed for username: {} - {}", username, e.getMessage());
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Account account) {
        String jwt = accountService.login(account.getUsername(), account.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }


    // Deposit
   
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Deposit request received from username: {} for amount: {}", username, amount);

        Account account = accountService.findAccountByUsername(username);
        accountService.deposit(account, amount);
        logger.debug("Deposit successful for username: {}", username);

        return ResponseEntity.ok("Deposit successful");
    }

    // Withdraw
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Withdraw request received from username: {} for amount: {}", username, amount);

        Account account = accountService.findAccountByUsername(username);
        accountService.withdraw(account, amount);
        logger.debug("Withdraw successful for username: {}", username);

        return ResponseEntity.ok("Withdraw successful");
    }

    // Transfer
    @PostMapping("/transfer")
//    public ResponseEntity<String> transfer(@RequestBody String toUsername, @RequestBody BigDecimal amount) {
//        String fromUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//        logger.debug("Transfer request from {} to {} for amount: {}", fromUsername, toUsername, amount);
//        Account fromAccount = accountService.findAccountByUsername(fromUsername);
//        try {
//            accountService.transferAmount(fromAccount, toUsername, amount);
//            logger.debug("Transfer successful from {} to {}", fromUsername, toUsername);
//            return ResponseEntity.ok("Transfer successful");
//        } catch (RuntimeException e) {
//            logger.error("Transfer failed from {} to {} - {}", fromUsername, toUsername, e.getMessage());
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
    
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> payload) {
        String toUsername = (String) payload.get("to");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());

        String fromUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Account fromAccount = accountService.findAccountByUsername(fromUsername);
        accountService.transferAmount(fromAccount, toUsername, amount);

        return ResponseEntity.ok("Transfer successful");
    }
    @GetMapping("/balance")
    public BigDecimal getBalance() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findAccountByUsername(username);
        return account.getBalance();
    }
    @GetMapping("/transactions/history")
    public List<Transaction> getTransactionHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findAccountByUsername(username);
        return transactionService.getTransactionsByAccount(account);  
    }

}
