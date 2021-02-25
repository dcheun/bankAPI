package dev.cheun.daos;

import dev.cheun.entities.Account;
import dev.cheun.entities.Client;
import dev.cheun.exceptions.InvalidCreateException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AccountDaoLocal implements AccountDAO {

    private static Logger logger = Logger.getLogger(AccountDaoLocal.class.getName());

    private static Map<Integer, Account> table = new HashMap<>();
    private static ClientDaoLocal clientDaoLocal = new ClientDaoLocal();
    private static int idMaker;

    @Override
    public Account createAccount(Account account) {
        String message;
        // Require clientId param.
        if (account.getClientId() == 0) {
            message = "Missing required field: clientId";
            logger.error(message);
            throw new InvalidCreateException(message);
        }
        // Require client to exist.
        Set<Client> clients = clientDaoLocal.getAllClients();
        boolean found = false;
        for (Client c : clients) {
            if (c.getId() == account.getClientId()) {
                found = true;
                break;
            }
        }
        if (!found) {
            message = "Client with id " + account.getClientId() + " does not exist";
            logger.error(message);
            throw new InvalidCreateException(message);
        }
        // Check for unique accountNumber
        Set<Account> accounts = getAllAccounts();
        for (Account a : accounts) {
            System.out.println(a.getAccountNumber()+" "+account.getAccountNumber());
            if (a.getAccountNumber().compareTo(account.getAccountNumber()) == 0) {
                message = "Account number " + account.getAccountNumber() +
                        " already exists";
                logger.error(message);
                throw new InvalidCreateException(message);
            }
        }
        // Create a new and unique id for the account.
        account.setId(++idMaker);
        // Emulate store in db.
        table.put(account.getId(), account);
        return account;
    }

    @Override
    public Set<Account> getAllAccounts() {
        Set<Account> allAccounts = new HashSet<>(table.values());
        return allAccounts;
    }

    @Override
    public Account getAccountById(int id) {
        return table.get(id);
    }

    @Override
    public Account updateAccount(Account account) {
        return table.put(account.getId(), account);
    }

    @Override
    public boolean deleteAccountById(int id) {
        // This will remove the obj, but will also return it.
        Account account = table.remove(id);
        return account != null;
    }
}
