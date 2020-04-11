package com.geekbrains.student.cloud.storage.server;

import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.*;

public class AuthBD implements AuthService {
    private Connection connection;
    private Statement stmt;
    private PreparedStatement psSelect;

    public AuthBD() {
        try {
            inizializeConnect();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private void createTableEx() throws SQLException {
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Users (\n" +
                "        id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "        nickname  TEXT,\n" +
                "        login TEXT,\n" +
                "        password TEXT\n" +
                "    );");
    }

    public void prepareStatements() throws SQLException {
        psSelect = connection.prepareStatement("SELECT password, nickname FROM Users WHERE login = ?;");
    }

    private void inizializeConnect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:cloud-server/CloudUsers.db");
        stmt = connection.createStatement();
        prepareStatements();
        createTableEx();
    }

    private void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psSelect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            inizializeConnect();
            psSelect.setString(1, login);
            ResultSet rs = psSelect.executeQuery();
            if (rs.next()) {
                if (rs.getString(1).equals(password)) {
                    String nick = rs.getString(2);
                    rs.close();
                    return nick;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return null;
    }
}
