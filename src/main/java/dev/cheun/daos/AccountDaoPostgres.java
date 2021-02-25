package dev.cheun.daos;

import dev.cheun.entities.Account;
import dev.cheun.exceptions.NotFoundException;
import dev.cheun.utils.ConnectionUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class AccountDaoPostgres implements AccountDAO {

    private static Logger logger = Logger.getLogger(AccountDaoPostgres.class.getName());

    @Override
    public Account createAccount(Account account) {
        // Try with resources syntax.
        // Java will always close the object to prevent resource leaks.
        try(Connection conn = ConnectionUtil.createConnection()) {
            String sql = "INSERT INTO account " +
                    "(account_number, balance_in_cents, client_id) " +
                    "VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );
            // Set the field params.
            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getBalanceInCents());
            ps.setInt(3, account.getClientId());
            ps.execute();
            // Return the val of the generated keys.
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int key = rs.getInt("id");
            account.setId(key);
            logger.info("createAccount: " + account);
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Set<Account> getAllAccounts() {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String sql = "SELECT * FROM account";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Set<Account> accounts = new HashSet<>();
            // While there exists records, create a new instance and store
            // the information into the new instance.
            while(rs.next()) {
                Account account = new Account();
                account.setId(rs.getInt("id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setBalanceInCents(rs.getInt("balance_in_cents"));
                account.setClientId(rs.getInt("client_id"));
                accounts.add(account);
            }
            logger.info("getAllAccounts: size=" + accounts.size());
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getAllAccounts: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Account getAccountById(int id) {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String sql = "SELECT * FROM account WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                throw new NotFoundException("No such account exists");
            }
            Account account = new Account();
            account.setId(rs.getInt("id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setBalanceInCents(rs.getInt("balance_in_cents"));
            account.setClientId(rs.getInt("client_id"));
            logger.info("getAccountById: id=" + id);
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getAccountById: Unable to get account with id=" + id);
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Account updateAccount(Account account) {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String sql = "UPDATE account " +
                    "SET account_number = ?, " +
                    "balance_in_cents = ?, " +
                    "client_id = ? " +
                    "WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getBalanceInCents());
            ps.setInt(3, account.getClientId());
            ps.setInt(4, account.getId());
            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                throw new NotFoundException("No such account exists");
            }
            logger.info("updateAccount: " + account);
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("updateAccount: Unable to update: " + account);
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteAccountById(int id) {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String sql = "DELETE FROM account WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                throw new NotFoundException("Unable to delete: No such account exists");
            }
            logger.info("deleteAccountById: id=" + id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("deleteAccountById: Unable to delete record with id=" + id);
            logger.error(e.getMessage());
            return false;
        }
    }
}
