package dev.cheun.services;

import dev.cheun.daos.AccountDaoPostgres;
import dev.cheun.daos.ClientDaoPostgres;
import dev.cheun.entities.Account;
import dev.cheun.entities.Client;
import dev.cheun.exceptions.BadRequestException;
import dev.cheun.exceptions.NotFoundException;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceTests {
    private static AccountService aserv = new AccountServiceImpl(new AccountDaoPostgres());
    private static ClientService cserv = new ClientServiceImpl(new ClientDaoPostgres());
    private static Client testClient1 = null;
    private static Client testClient2 = null;
    private static Account testAccount1 = null;
    private static Account testAccount2 = null;

    @BeforeAll
    public static void setUpOnce() {
        // Set up test resources.
        System.out.println("Set Up (one time)");
        Client c1 = new Client(0,"Harry Potter");
        Client c2 = new Client(0,"Hermoine Granger");
        testClient1 = cserv.registerClient(c1);
        testClient2 = cserv.registerClient(c2);
    }

    @Test
    @Order(1)
    void create_account() {
        Account a1 = new Account(0, "61234", 3000, testClient1.getId());
        Account a2 = new Account(0, "61235", 4000, testClient2.getId());
        testAccount1 = aserv.createAccount(a1);
        testAccount2 = aserv.createAccount(a2);

        Assertions.assertNotEquals(0, testAccount1.getId());
        Assertions.assertEquals("61234", testAccount1.getAccountNumber());
        Assertions.assertNotEquals(0, testAccount2.getId());
        Assertions.assertEquals("61235", testAccount2.getAccountNumber());
    }

    @Test
    @Order(2)
    void create_account_number_exists() {
        Account account = new Account(
                0,
                testAccount1.getAccountNumber(),
                4000,
                testAccount1.getClientId()
        );
        Exception e = Assertions.assertThrows(BadRequestException.class, () -> {
            aserv.createAccount(account);
        });
        String expectedMessage = "Account number already exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // Test update where account number already exists
    // should throw BadRequestException
    @Test
    @Order(3)
    void update_account_number_exists() {
        // NOTE: Try to update testAccount1 account number with testAccount2's
        // account number.
        Account account = new Account(
                testAccount1.getId(),
                testAccount2.getAccountNumber(),
                4000,
                testAccount1.getClientId()
        );
        Exception e = Assertions.assertThrows(BadRequestException.class, () -> {
            aserv.updateAccount(account);
        });
        String expectedMessage = "Account number already exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // test less than greater than -> move to own test file using mockito

    // test bad request less than greater than -> move to own test file using mockito

    @Test
    @Order(4)
    void delete_account_by_id() {
        Assertions.assertTrue(aserv.deleteAccountById(
                testAccount1.getClientId(),
                testAccount1.getId()
        ));
        Assertions.assertTrue(aserv.deleteAccountById(
                testAccount2.getClientId(),
                testAccount2.getId()
        ));
    }

    // Test delete account where client is not found.
    // Should throw NotFoundException.
    @Test
    @Order(5)
    void delete_account_client_not_exists() {
        // NOTE: testAccount1 deleted in previous test.
        // Delete corresponding client - testClient1
        Assertions.assertTrue(cserv.deleteClientById(testClient1.getId()));
        // deleteAccountById checks client exists first.
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            aserv.deleteAccountById(testAccount1.getClientId(), testAccount1.getId());
        });
        String expectedMessage = "No such client exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // Test delete account where account is not found.
    // Should throw NotFoundException.
    @Test
    @Order(6)
    void delete_account_account_not_exists() {
        // NOTE: testAccount2 deleted in previous test.
        // However, its corresponding client has not been deleted.
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            aserv.deleteAccountById(testAccount2.getClientId(), testAccount2.getId());
        });
        String expectedMessage = "No such account exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // Test update account where client is not found.
    // Should throw NotFoundException.
    @Test
    @Order(7)
    void update_account_client_not_exists() {
        // See notes on delete_account_client_not_exists.
        Account account = new Account(
                testAccount1.getId(),
                testAccount1.getAccountNumber(),
                4000,
                testAccount1.getClientId()
        );
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            aserv.updateAccount(account);
        });
        String expectedMessage = "No such client exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // Test update account where account is not found.
    // Should throw NotFoundException.
    @Test
    @Order(8)
    void update_account_account_not_exists() {
        // See notes on delete_account_account_not_exists.
        Account account = new Account(
                testAccount2.getId(),
                testAccount2.getAccountNumber(),
                5000,
                testAccount2.getClientId()
        );
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            aserv.updateAccount(account);
        });
        String expectedMessage = "No such account exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // Test create account where client is not found.
    // Should throw NotFoundException.
    @Test
    @Order(9)
    void create_account_client_not_exists() {
        // NOTE: testClient1 previously deleted.
        Account account = new Account(
                0,
                "712345",
                10000,
                testClient1.getId()
        );
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            aserv.createAccount(account);
        });
        String expectedMessage = "No such client exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // Test get account where client is not found.
    // Should throw NotFoundException.
    @Test
    @Order(10)
    void get_all_accounts_client_not_exists() {
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            aserv.getAllAccounts(testClient1.getId());
        });
        String expectedMessage = "No such client exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    // Test get account where account is not found.
    // Should throw NotFoundException.
    @Test
    @Order(11)
    void get_account_by_id_account_not_exists() {
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            aserv.getAccountById(testAccount2.getClientId(), testAccount2.getId());
        });
        String expectedMessage = "No such account exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    @AfterAll
    public static void tearDownOnce() {
        // Clean up test resources.
        System.out.println("Tear down (one time)");
        cserv.deleteClientById(testClient2.getId());
    }

}
