package dev.cheun.daos;

import dev.cheun.entities.Client;

import java.util.Set;

public interface ClientDAO {
    // CREATE
    Client createClient(Client client);

    // READ
    Set<Client> getAllClients();
    Client getClientById(int id);

    // UPDATE
    Client updateClient(Client client);

    // DELETE
    boolean deleteClientById(int id);
}
