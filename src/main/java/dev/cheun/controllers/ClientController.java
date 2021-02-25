package dev.cheun.controllers;

import com.google.gson.Gson;
import dev.cheun.daos.ClientDaoLocal;
import dev.cheun.daos.ClientDaoPostgres;
import dev.cheun.entities.Client;
import dev.cheun.exceptions.NotFoundException;
import dev.cheun.services.ClientService;
import dev.cheun.services.ClientServiceImpl;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;

import java.util.Set;

// All logic in controller should deal with the API.
// Controller should call services to perform the actions.
public class ClientController {
    private ClientService clientService = new ClientServiceImpl(new ClientDaoPostgres());

    public Handler getAllClientsHandler = ctx -> {
        Set<Client> allClients = this.clientService.getAllClients();
        Gson gson = new Gson();
        String clientsJSON = gson.toJson(allClients);
        ctx.result(clientsJSON);
    };

    public Handler getClientByIdHandler = ctx -> {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Client client;
        try {
            client = this.clientService.getClientById(id);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
        Gson gson = new Gson();
        String clientJSON = gson.toJson(client);
        ctx.result(clientJSON);
    };

    // POST /clients (the body will contain a JSON with the client info)
    public Handler createClientHandler = ctx -> {
        String body = ctx.body();
        Gson gson = new Gson();
        // Convert Client JSON into a Java Client obj.
        Client client = gson.fromJson(body, Client.class);
        Client newClient = this.clientService.registerClient(client);
        ctx.status(201).result(gson.toJson(newClient));
    };

    public Handler updateClientHandler = ctx -> {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String body = ctx.body();
        Gson gson = new Gson();
        Client client = gson.fromJson(body, Client.class);
        client.setId(id); // Often redundant, but the path param takes precedent.
        Client prevClient;
        try {
            prevClient = this.clientService.updateClient(client);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
        ctx.result(gson.toJson(prevClient));
    };

    public Handler deleteClientHandler = ctx -> {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted;
        try {
            deleted = this.clientService.deleteClientById(id);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
        if (deleted) {
            ctx.result("Client with id " + id + " was deleted");
        } else {
            ctx.status(500).result("Unable to delete client with id " + id);
        }
    };
}
