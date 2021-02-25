package dev.cheun.services;

import dev.cheun.daos.ClientDaoPostgres;
import dev.cheun.entities.Client;
import dev.cheun.exceptions.NotFoundException;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientServiceTests {
    private static ClientService cserv = new ClientServiceImpl(new ClientDaoPostgres());
    private static Client testClient = null;

    @Test
    @Order(1)
    void register_client() {
        Client client = new Client(0, "John Smith");
        cserv.registerClient(client);

        Assertions.assertNotEquals(0, client.getId());
        Assertions.assertEquals("John Smith", client.getName());
        this.testClient = client;
    }

    @Test
    @Order(2)
    void delete_client_by_id() {
        int id = testClient.getId();
        boolean result = cserv.deleteClientById(id);
        Assertions.assertTrue(result);
    }

    @Test
    @Order(3)
    void get_client_not_exists() {
        // testClient was deleted previously.
        int id = testClient.getId();
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            cserv.getClientById(id);
        });
        String expectedMessage = "No such client exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(4)
    void update_client_not_exists() {
        // Mock a client with the id of testClient, whose record
        // should already been deleted from db.
        // This should generate a NotFoundException.
        Client client = new Client(testClient.getId(), "Jane Smith");
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            cserv.updateClient(client);
        });
        String expectedMessage = "No such client exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(5)
    void delete_client_not_exists() {
        // Attempt to delete client with id of testClient
        // who was previously deleted, therefore should throw
        // an NotFoundException.
        int id = testClient.getId();
        Exception e = Assertions.assertThrows(NotFoundException.class, () -> {
            cserv.deleteClientById(id);
        });
        String expectedMessage = "No such client exists";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }
}
