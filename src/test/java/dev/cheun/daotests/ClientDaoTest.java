package dev.cheun.daotests;

import dev.cheun.daos.ClientDAO;
import dev.cheun.daos.ClientDaoPostgres;
import dev.cheun.entities.Client;
import org.junit.jupiter.api.*;

import java.util.Set;

// Junit tests not guaranteed to run in order unless you tell it to.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientDaoTest {
    private static ClientDAO cdao = new ClientDaoPostgres();
    private static Client testClient = null;

    @Test
    @Order(1)
    void create_client() {
        // ID of zero means that object has not been saved/persisted somewhere.
        Client bob = new Client(0, "Bob Smith");
        cdao.createClient(bob);
        testClient = bob;
        Assertions.assertNotEquals(0, testClient.getId());
    }

    @Test
    @Order(2)
    void get_client_by_id() {
        int id = testClient.getId();
        Client client = cdao.getClientById(id);
        Assertions.assertEquals(testClient.getName(), client.getName());
    }

    @Test
    @Order(3)
    void update_client() {
        Client client = cdao.getClientById(testClient.getId());
        client.setName("Dave Jones");
        // Should update name.
        cdao.updateClient(client);

        Client updatedClient = cdao.getClientById(testClient.getId());
        Assertions.assertEquals("Dave Jones", updatedClient.getName());
    }

    @Test
    @Order(4)
    void get_all_clients() {
        Client c1 = new Client(0, "S.E. Hinton");
        Client c2 = new Client(0, "Ken Kesey");
        Client c3 = new Client(0, "Charles Dickens");

        cdao.createClient(c1);
        cdao.createClient(c2);
        cdao.createClient(c3);

        Set<Client> allClients = cdao.getAllClients();
        Assertions.assertTrue(allClients.size() > 2);
    }

    @Test
    @Order(5)
    void delete_client_by_id() {
        int id = testClient.getId();
        boolean result = cdao.deleteClientById(id);
        Assertions.assertTrue(result);
    }
}
