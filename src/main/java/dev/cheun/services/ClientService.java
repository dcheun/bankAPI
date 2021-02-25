package dev.cheun.services;

import dev.cheun.entities.Client;

import java.util.Set;

// Business Logic
public interface ClientService {
    // CREATE
    Client registerClient(Client client);

    // READ
    Set<Client> getAllClients();
    Set<Client> getClientsByName(String name);
    Client getClientById(int id);

    // UPDATE
    Client updateClient(Client client);

    // DELETE
    boolean deleteClientById(int id);
}
