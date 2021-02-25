package dev.cheun.services;

import dev.cheun.entities.Account;

import java.util.Set;

public interface AccountService {
    // CREATE
    Account createAccount(Account account);

    // READ
    Set<Account> getAllAccounts(int clientId);
    Account getAccountById(int clientId, int id);

    // UPDATE
    Account updateAccount(Account account);

    // DELETE
    boolean deleteAccountById(int clientId, int id);
}
