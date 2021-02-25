package dev.cheun.controllers;

import com.google.gson.Gson;
import dev.cheun.daos.AccountDaoPostgres;
import dev.cheun.entities.Account;
import dev.cheun.exceptions.BadRequestException;
import dev.cheun.exceptions.NotFoundException;
import dev.cheun.services.AccountService;
import dev.cheun.services.AccountServiceImpl;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;

import java.util.HashSet;
import java.util.Set;

// All logic in controller should deal with the API.
// Controller should call services to perform the actions.
public class AccountController {
    private AccountService accountService = new AccountServiceImpl(new AccountDaoPostgres());

    public Handler getAllAccountsHandler = ctx -> {
        int clientId = Integer.parseInt(ctx.pathParam("id"));
        String alt = ctx.queryParam("amountLessThan");
        String agt = ctx.queryParam("amountGreaterThan");
        int amountLessThan = alt != null ? Integer.parseInt(alt) : Integer.MAX_VALUE;
        int amountGreaterThan = agt != null ? Integer.parseInt(agt) : Integer.MIN_VALUE;
        if (amountGreaterThan > amountLessThan) {
            throw new BadRequestResponse("amountGreaterThan must be less than amountLessThan");
        }
        Set<Account> allAccounts;
        try {
            allAccounts = this.accountService.getAllAccounts(clientId);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
        Set<Account> selectedAccounts = new HashSet<>();
        for (Account a : allAccounts) {
            if (a.getBalanceInCents() < amountLessThan &&
                    a.getBalanceInCents() > amountGreaterThan) {
                selectedAccounts.add(a);
            }
        }
        Gson gson = new Gson();
        String accountsJSON = gson.toJson(selectedAccounts);
        ctx.result(accountsJSON);
    };

    public Handler getAccountByIdHandler = ctx -> {
        int clientId = Integer.parseInt(ctx.pathParam("id"));
        int id = Integer.parseInt(ctx.pathParam("aid"));
        Account account;
        try {
            account = this.accountService.getAccountById(clientId, id);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
        Gson gson = new Gson();
        String accountJSON = gson.toJson(account);
        ctx.result(accountJSON);
    };

    // POST /accounts (the body will contain a JSON with the info)
    public Handler createAccountHandler = ctx -> {
        int clientId = Integer.parseInt(ctx.pathParam("id"));
        String body = ctx.body();
        Gson gson = new Gson();
        // Convert JSON into a Java obj.
        Account account = gson.fromJson(body, Account.class);
        account.setClientId(clientId);
        Account newAccount;
        try {
            newAccount = this.accountService.createAccount(account);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestResponse(e.getMessage());
        }
        ctx.status(201).result(gson.toJson(newAccount));
    };

    public Handler updateAccountHandler = ctx -> {
        int clientId = Integer.parseInt(ctx.pathParam("id"));
        int id = Integer.parseInt(ctx.pathParam("aid"));
        String body = ctx.body();
        Gson gson = new Gson();
        Account account = gson.fromJson(body, Account.class);
        account.setId(id); // Often redundant, but the path param takes precedent.
        account.setClientId(clientId);
        Account updatedAccount;
        try {
            updatedAccount = this.accountService.updateAccount(account);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestResponse(e.getMessage());
        }
        ctx.result(gson.toJson(updatedAccount));
    };

    public Handler deleteAccountHandler = ctx -> {
        int clientId = Integer.parseInt(ctx.pathParam("id"));
        int id = Integer.parseInt(ctx.pathParam("aid"));
        boolean deleted;
        try {
            deleted = this.accountService.deleteAccountById(clientId, id);
        } catch (NotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
        if (deleted) {
            ctx.result("Account with id " + id + " was deleted");
        } else {
            ctx.status(500).result("Unable to delete account with id " + id);
        }
    };
}
