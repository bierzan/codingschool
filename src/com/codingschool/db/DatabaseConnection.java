package com.codingschool.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection openConnection = null;

    public static Connection getConnection() throws SQLException {
        if (openConnection == null || openConnection.isClosed()) {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/coding_school?useSSL=false&useUnicode=true&characterEncoding=UTF-8",
                    "root",
                    "coderslab");
            openConnection = conn;
        }
        return openConnection;
    }
}
