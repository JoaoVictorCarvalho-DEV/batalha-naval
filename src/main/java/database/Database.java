package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Database instance;
    private Connection connection;

    private final String URL = "jdbc:sqlite:batalha_naval.db";


    private Database() {
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Banco de dados conectado.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}