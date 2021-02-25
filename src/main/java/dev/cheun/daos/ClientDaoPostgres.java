package dev.cheun.daos;

import dev.cheun.entities.Client;
import dev.cheun.exceptions.NotFoundException;
import dev.cheun.utils.ConnectionUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ClientDaoPostgres implements ClientDAO {

    private static Logger logger = Logger.getLogger(ClientDaoPostgres.class.getName());

    @Override
    public Client createClient(Client client) {
        // Try with resources syntax.
        // Java will always close the object to prevent any resource leaks.
        try (Connection conn = ConnectionUtil.createConnection()) {
            String sql = "INSERT INTO client " +
                    "(client_name) " +
                    "VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );
            // Set the field params.
            ps.setString(1, client.getName());
            ps.execute();
            // Return the value of the generated keys.
            ResultSet rs = ps.getGeneratedKeys();
            rs.next(); // The first element.
            int key = rs.getInt("id");
            client.setId(key);
            logger.info("createClient: " + client);
            return client;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("createClient: " + client);
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Set<Client> getAllClients() {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String sql = "SELECT * FROM client";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Set<Client> clients = new HashSet<>();

            // While there exists clients, create a new client and store the
            // information into the new client.
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setName(rs.getString("client_name"));
                clients.add(client);
            }
            logger.info("getAllClients: size=" + clients.size());
            return clients;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getAllClients: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Client getClientById(int id) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String sql = "SELECT * FROM client WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NotFoundException("No such client exists");
            }
            Client client = new Client();
            client.setId(rs.getInt("id"));
            client.setName(rs.getString("client_name"));
            logger.info("getClientById: id=" + id);
            return client;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getClientById: Unable to get client with id=" + id);
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Client updateClient(Client client) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String sql = "UPDATE client " +
                    "SET client_name = ? " +
                    "WHERE id = ? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client.getName());
            ps.setInt(2, client.getId());
            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                throw new NotFoundException("No such client exists");
            }
            logger.info("updateClient: " + client);
            return client;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("updateClient: Unable to update: " + client);
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteClientById(int id) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String sql = "DELETE FROM client WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                throw new NotFoundException("Unable to delete: No such client exists");
            }
            logger.info("deleteClientById: id=" + id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("deleteClientById: Unable to delete record with id=" + id);
            logger.error(e.getMessage());
            return false;
        }
    }
}
