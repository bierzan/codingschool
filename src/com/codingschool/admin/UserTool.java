package com.codingschool.admin;

import com.codingschool.db.DatabaseConnection;
import com.codingschool.model.UserGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class UserTool {
    public static void main() {

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (printAndGetTask(conn).equalsIgnoreCase("add")) {
                addUser(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static String printAndGetTask(Connection conn) throws SQLException {

        Statement stm = conn.createStatement();

        ResultSet rs = stm.executeQuery("SELECT * FROM users");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("username");
            String email = rs.getString("email");
            int groupId = rs.getInt("user_group_id");
            if (groupId > 0) {
                String groupName = UserGroup.loadById(conn, groupId).getName();

                System.out.println(id + "\t|" + name + "\t|" + email + "\t|nr i nazwa grupy: " + groupId + ". " + groupName);
            } else {
                System.out.println(id + "\t|" + name + "\t|" + email + "\t|nr i nazwa grupy: nie przypisano");

            }
        }

        Scanner sc = new Scanner(System.in);
        String task = " ";

        while (!task.toLowerCase().matches("(add)|(edit)|(delete)|(quit)")) {
            System.out.println("\nWybierz jedną z opcji:" +
                    "\n - add - dodanie użytkownika," +
                    "\n - edit - edycja użytkownka," +
                    "\n - delete - usunięcie użytkownika," +
                    "\n - quit - zakońćzenie programu.");
            task = sc.next();
        }

        return task;
    }

    private static void addUser(Connection conn) {
        Scanner sc = new Scanner(System.in);
        String username;
        String password;
        String email = " ";
        String group;


        System.out.println("Podaj nazwę użytkownika");
        username = sc.next();

        System.out.println("Podaj email użytkownika");
        boolean uniqueTest = true;

        while (uniqueTest) {
            email = sc.next();
            while (!email.matches("[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.([a-zA-Z]{2,}){1}")) {
                System.out.println("to nie jest adres e-mail");
                email = sc.next();
            }

            try {
                uniqueTest = doesItExist(conn, "email", email);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (uniqueTest) {
                System.out.println("Taki email już istnieje, podaj inny");
            }
        }


        System.out.println("Podaj hasło");
        password = sc.next();

        System.out.println("Podaj nr grupy do której chesz przypisać użytkownika"); //todo istniejaca grupa lub nowa grupa
        group = sc.next();

        System.out.println(username);
        System.out.println(email);
        System.out.println(password);
        System.out.println(group);

    }

    private static boolean doesItExist(Connection conn, String column, String record) throws SQLException {

        String sql = "SELECT ? FROM users;";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setString(1, column);
        ResultSet rs = prepStm.executeQuery();
        ArrayList<String> records = new ArrayList<>();
        boolean result = false;

        while (rs.next()) {
            records.add(rs.getString("email"));
        }

        for (String mail : records) {
            if (mail.equals(record)) {
                result = true;
                return result;
            }
        }
        return result;

    }
}
