package dev.cheun.services;

import dev.cheun.daos.AccountDAO;
import dev.cheun.daos.ClientDAO;
import dev.cheun.daos.ClientDaoPostgres;
import dev.cheun.entities.Account;
import dev.cheun.entities.Client;
import dev.cheun.exceptions.BadRequestException;
import dev.cheun.exceptions.NotFoundException;
import io.javalin.http.BadRequestResponse;

import java.util.HashSet;
import java.util.Set;

public class AccountServiceImpl implements AccountService {

    private AccountDAO dao;
    private ClientDAO cDao;

    // Dependency injection.
    // A service is created by passing in the dependency it needs.
    public AccountServiceImpl(AccountDAO dao) {
        this.dao = dao;
        this.cDao = new ClientDaoPostgres();
    }

    @Override
    public Account createAccount(Account account) {
        // Check if client exists (throws NotFoundException if not found)
        this.cDao.getClientById(account.getClientId());
        // Check if account number already exists.
        Set<Account> allAccounts = this.dao.getAllAccounts();
        for (Account a : allAccounts) {
            if (a.getAccountNumber().compareTo(account.getAccountNumber()) == 0) {
                throw new BadRequestException("Account number already exists");
            }
        }
        Account newAccount = this.dao.createAccount(account);
        return newAccount;
    }

    @Override
    public Set<Account> getAllAccounts(int clientId) {
        // Check if client exists.
        Client client = this.cDao.getClientById(clientId);
        // Filter accounts to the respective client.
        Set<Account> allAccounts = this.dao.getAllAccounts();
        Set<Account> selectedAccounts = new HashSet<>();
        for (Account a : allAccounts) {
            if (a.getClientId() == client.getId()) {
                selectedAccounts.add(a);
            }
        }
        return selectedAccounts;
    }

    @Override
    public Account getAccountById(int clientId, int id) {
        // Check if client exists.
        Client client = this.cDao.getClientById(clientId);
        Account account = this.dao.getAccountById(id);
        if (account.getClientId() != client.getId()) {
            throw new NotFoundException("No such account exists");
        }
        return account;
    }

    @Override
    public Account updateAccount(Account account) {
        // getAccountById will check if client and account exists.
        getAccountById(account.getClientId(), account.getId());
        // Check if account number already exists.
        Set<Account> allAccounts = this.dao.getAllAccounts();
        for (Account a : allAccounts) {
            if (a.getId() != account.getId() &&
                    a.getAccountNumber().compareTo(account.getAccountNumber()) == 0) {
                throw new BadRequestException("Account number already exists");
            }
        }
        Account updatedAccount = this.dao.updateAccount(account);
        return updatedAccount;
    }

    @Override
    public boolean deleteAccountById(int clientId, int id) {
        // getAccountById will check if client and account exists.
        Account account = getAccountById(clientId, id);
        return this.dao.deleteAccountById(account.getId());
    }
}
