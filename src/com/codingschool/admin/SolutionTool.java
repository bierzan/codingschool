package com.codingschool.admin;

import com.codingschool.db.DatabaseConnection;
import com.codingschool.model.Exercise;
import com.codingschool.model.Solution;
import com.codingschool.model.User;
import com.codingschool.model.UserGroup;

import java.sql.*;
import java.util.Scanner;

public class SolutionTool {
    public static void main() {

        String task = " ";

        try (Connection conn = DatabaseConnection.getConnection()) {
            while (!task.equalsIgnoreCase("quit")) {
                task = printAndGetTask(conn);
                if (task.equalsIgnoreCase("add")) {
                    addSolution(conn);
                } else if (task.equalsIgnoreCase("view")) {
                    viewSolutionByUser(conn);
                }
            }

            System.out.println("\nKończenie programu");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static String printAndGetTask(Connection conn) throws SQLException {

        Scanner sc = new Scanner(System.in);
        String task = " ";

        while (!task.toLowerCase().

                matches("(add)|(view)|(quit)")) {
            System.out.println("\nWybierz jedną z opcji:" +
                    "\n - add - przypisywanie zadań do użytkowników," +
                    "\n - view - podgląðanie rozwiązań danego użytkownika," +
                    "\n - quit - zakończenie programu.");
            task = sc.next();
        }
        return task;
    }

    private static void addSolution(Connection conn) {
        try {
            boolean userExists = false;
            printUsers(conn);
            Solution sol = new Solution();
            while (!userExists) {
                System.out.println("\n Podaj Id użytkownika, do którego chcesz przypisać zadanie");
                int userId = getIntFromScanner();
                String sqlUsers = "SELECT id FROM users WHERE id = ?";
                PreparedStatement prepStm = conn.prepareStatement(sqlUsers);
                prepStm.setInt(1, userId);
                ResultSet rsUsers = prepStm.executeQuery();
                if (!rsUsers.next()) {
                    System.out.println("Nie ma użytkownika o podanym numerze id");
                } else {
                    sol.setUser(User.loadById(conn, userId));
                    userExists = true;
                }
            }

            boolean exerciseExists = false;
            printExercises(conn);
            while (!exerciseExists) {
                System.out.println("\n Podaj Id zadania, do którego chcesz przypisać rozwiązanie");
                int exId = getIntFromScanner();
                String sqlEx = "SELECT id FROM exercise WHERE id = ?";
                PreparedStatement prepStm = conn.prepareStatement(sqlEx);
                prepStm.setInt(1, exId);
                ResultSet rsUsers = prepStm.executeQuery();
                if (!rsUsers.next()) {
                    System.out.println("Nie ma zadania o takim id");
                } else {
                    sol.setExercise(Exercise.loadById(conn, exId));
                    exerciseExists = true;
                }
            }

            sol.save(conn);
            System.out.println("\nPrzypisano...\n");

            printExercises(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void viewSolutionByUser(Connection conn) throws SQLException {

        boolean userExists = false;
        printUsers(conn);
        while (!userExists) {
            System.out.println("\nPodaj id użytkownika, którego zadania chcesz zobaczyć\n");
            int userId = getIntFromScanner();
            String sqlUsers = "SELECT created, updated, description, exercise_id FROM solution WHERE user_id = ? ORDER BY exercise_id;";
            PreparedStatement prepStm = conn.prepareStatement(sqlUsers);
            prepStm.setInt(1, userId);
            ResultSet rsUsers = prepStm.executeQuery();
            if (!rsUsers.next()) {
                System.out.println("Nie ma użytkownika o podanym numerze id");
            } else {
                while (rsUsers.next()) {
                    String created = rsUsers.getString("created");
                    String updated = rsUsers.getString("updated");
                    String desc = rsUsers.getString("description");
                    String exeId = rsUsers.getString("exercise_id");
                    System.out.println(exeId + " | stworzone: " + created + " | zaktualizowane: " + updated + " | rozwiązanie: " + desc);
                }
                userExists = true;
            }
        }

    }

    private static void printUsers(Connection conn) throws SQLException {
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
    }

    private static void printExercises(Connection conn) throws SQLException {
        Statement stm = conn.createStatement();

        ResultSet rs = stm.executeQuery("SELECT * FROM exercise");
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String desc = rs.getString("description");

            System.out.println(id + "\t|" + title + "\t|" + desc);
        }
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
