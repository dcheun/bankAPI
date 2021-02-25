package dev.cheun.daotests;

import dev.cheun.daos.AccountDAO;
import dev.cheun.daos.AccountDaoPostgres;
import dev.cheun.daos.ClientDAO;
import dev.cheun.daos.ClientDaoPostgres;
import dev.cheun.entities.Account;
import dev.cheun.entities.Client;
import org.junit.jupiter.api.*;

import java.util.Set;

// Specify order Junit tests should run.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDaoTest {
    private static AccountDAO adao = new AccountDaoPostgres();
    private static ClientDAO cdao = new ClientDaoPostgres();
    private static Client testClient = null;
    private static Account testAccount = null;

    @BeforeAll
    public static void setUpOnce() {
        System.out.println("Set Up Once");
        Client c1 = new Client(0, "Harry Potter");
        testClient = cdao.createClient(c1);
    }

    @Test
    @Order(1)
    void create_account() {
        // ID of zero means object has not been saved/persisted.
        Account account = new Account(0, "56789", 1000, testClient.getId());
        adao.createAccount(account);
        testAccount = account;
        Assertions.assertNotEquals(0, testAccount.getId());
    }

    @Test
    @Order(2)
    void get_account_by_id() {
        int id = testAccount.getId();
        Account account = adao.getAccountById(id);
        Assertions.assertEquals(testAccount.getAccountNumber(), account.getAccountNumber());
    }

    @Test
    @Order(3)
    void update_account() {
        Account account = adao.getAccountById(testAccount.getId());
        account.setBalanceInCents(2000);
        adao.updateAccount(account);

        Account updatedAccount = adao.getAccountById(testAccount.getId());
        Assertions.assertEquals(2000, updatedAccount.getBalanceInCents());
    }

    @Test
    @Order(4)
    void get_all_accounts() {
        Account a1 = new Account(0, "56790", 1000, testClient.getId());
        Account a2 = new Account(0, "56791", 1001, testClient.getId());
        Account a3 = new Account(0, "56792", 1002, testClient.getId());

        adao.createAccount(a1);
        adao.createAccount(a2);
        adao.createAccount(a3);

        Set<Account> allAccounts = adao.getAllAccounts();
        Assertions.assertTrue(allAccounts.size() > 2);
    }

    @Test
    @Order(5)
    void delete_account_by_id() {
        int id = testAccount.getId();
        boolean result = adao.deleteAccountById(id);
        Assertions.assertTrue(result);
    }
}
