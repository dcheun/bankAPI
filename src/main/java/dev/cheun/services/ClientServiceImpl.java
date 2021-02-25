package dev.cheun.services;

import dev.cheun.daos.ClientDAO;
import dev.cheun.entities.Client;

import java.util.HashSet;
import java.util.Set;

public class ClientServiceImpl implements ClientService {

    // Our service is going to need a DAO to get and save clients.
    private ClientDAO dao;

    // Dependency injection.
    // A service is created by passing in the dependencies it needs.
    public ClientServiceImpl(ClientDAO dao) {
        this.dao = dao;
    }

    @Override
    public Client registerClient(Client client) {
        return this.dao.createClient(client);
    }

    @Override
    public Set<Client> getAllClients() {
        return this.dao.getAllClients();
    }

    @Override
    public Set<Client> getClientsByName(String name) {
        Set<Client> allClients = this.getAllClients();
        Set<Client> selectedClients = new HashSet<>();
        for (Client c : allClients) {
            if (c.getName().contains(name)) {
                selectedClients.add(c);
            }
        }
        return selectedClients;
    }

    @Override
    public Client getClientById(int id) {
        return this.dao.getClientById(id);
    }

    @Override
    public Client updateClient(Client client) {
        return this.dao.updateClient(client);
    }

    @Override
    public boolean deleteClientById(int id) {
        return this.dao.deleteClientById(id);
    }
}
