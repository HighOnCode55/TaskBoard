package com.github.highoncode55.taskboard.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/taskboard_db";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // <<< ATUALIZE AQUI

    // 2. A única instância da conexão. É 'static' para pertencer à classe, não a um objeto.
    private static Connection connection = null;

    // 3. O construtor é 'private' para que ninguém de fora possa criar instâncias desta classe.
    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Fechando a conexão com o banco de dados...");
            connection.close();
        }
    }
}