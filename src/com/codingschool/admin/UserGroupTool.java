package com.codingschool.admin;

import com.codingschool.db.DatabaseConnection;
import com.codingschool.model.UserGroup;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class UserGroupTool {
    public static void main() {
        String task = " ";

        try (Connection conn = DatabaseConnection.getConnection()) {
            while (!task.equalsIgnoreCase("quit")) {
                task = printAndGetTask(conn);
                if (task.equalsIgnoreCase("add")) {
                    addGroup(conn);
                } else if (task.equalsIgnoreCase("edit")) {
                    editGroup(conn);
                } else if (task.equalsIgnoreCase("delete")) {
                    deleteGroup(conn);
                }
            }

            System.out.println("\nKończenie programu");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static String printAndGetTask(Connection conn) throws SQLException {

        Statement stm = conn.createStatement();

        ResultSet rs = stm.executeQuery("SELECT * FROM user_group");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");

            System.out.println(id + "\t|" + name);
        }

        Scanner sc = new Scanner(System.in);
        String task = " ";

        while (!task.toLowerCase().

                matches("(add)|(edit)|(delete)|(quit)")) {
            System.out.println("\nWybierz jedną z opcji:" +
                    "\n - add - dodanie zadania," +
                    "\n - edit - edycja zadania," +
                    "\n - delete - usunięcie zadania," +
                    "\n - quit - zakończenie programu.");
            task = sc.next();
        }
        return task;
    }

    private static void addGroup(Connection conn) {
        Scanner sc = new Scanner(System.in);
        String name;

        System.out.println("Podaj nazwę grupy");
        name = sc.nextLine();

        try {
            UserGroup group = new UserGroup(name);
            group.save(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Grupa została dodana\n");

    }

    private static void editGroup(Connection conn) {
        int groupId = 0;

        String name;

        System.out.println("Podaj id grupy, którą chcesz edytować.");
        groupId = getIntFromScanner();

        Scanner sc = new Scanner(System.in);

        System.out.println("Podaj nową nazwę grupy");
        name = sc.nextLine();

        try {

            UserGroup grToEdit = UserGroup.loadById(conn, groupId);
            grToEdit.setName(name);
            grToEdit.update(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Grupa została zmieniona\n");

    }

    private static void deleteGroup(Connection conn) {
        int grId = 0;

        System.out.println("Podaj id grupy, którą chcesz usunąć.");
        grId = getIntFromScanner();

        try {
            UserGroup.loadById(conn, grId).delete(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Grupa została usunięta\n");

    }

    private static int getIntFromScanner() {
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) {
            System.out.println("To nie jest numer ID");
            scan.next();
        }
        return scan.nextInt();
    }
}
