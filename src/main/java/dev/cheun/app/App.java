package dev.cheun.app;

import dev.cheun.controllers.AccountController;
import dev.cheun.controllers.ClientController;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create();

        ClientController clientController = new ClientController();
        AccountController accountController = new AccountController();

        // GET /clients => return all clients
        app.get("/clients", clientController.getAllClientsHandler);

        // GET /clients/12 => get client with ID 12
        app.get("/clients/:id", clientController.getClientByIdHandler);
        app.get("/clients/:id/accounts",
                accountController.getAllAccountsHandler);
        app.get("/clients/:id/accounts/:aid",
                accountController.getAccountByIdHandler);

        // POST /clients => create a new client
        app.post("/clients", clientController.createClientHandler);
        app.post("/clients/:id/accounts",
                accountController.createAccountHandler);

        // PUT /clients/12 => update client 12
        app.put("/clients/:id", clientController.updateClientHandler);
        app.put("/clients/:id/accounts/:aid",
                accountController.updateAccountHandler);

        // DELETE /clients/11 => delete client 11
        app.delete("/clients/:id", clientController.deleteClientHandler);
        app.delete("/clients/:id/accounts/:aid",
                accountController.deleteAccountHandler);

        app.start();
    }
}
