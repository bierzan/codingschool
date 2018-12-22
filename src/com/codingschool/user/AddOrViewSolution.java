package com.codingschool.user;

import com.codingschool.db.DatabaseConnection;
import com.codingschool.model.Exercise;
import com.codingschool.model.Solution;
import com.codingschool.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddOrViewSolution {
    public static void main(String[] args) {

        String task = " ";

        try (Connection conn = DatabaseConnection.getConnection()) {
            User user = User.loadById(conn, Integer.valueOf(args[0]));
            while (!task.equalsIgnoreCase("quit")) {
                task = printAndGetTask(conn);
                if (task.equalsIgnoreCase("add")) {
                    addSolution(conn, user);
                } else if (task.equalsIgnoreCase("view")) {
                    viewSolutionByUser(conn, user);
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
                    "\n - add - dodanie roziązania," +
                    "\n - view - przeglądanie swoich rozwiązań" +
                    "\n - quit - zakończenie programu.");
            task = sc.next();
        }
        return task;
    }

    private static void printExercises(Connection conn, User user) throws SQLException {
        PreparedStatement prepStm = conn.prepareStatement("SELECT DISTINCT exercise.id, exercise.title, exercise.description " +
                "FROM exercise JOIN solution ON exercise.id = solution.exercise_id " +
                "WHERE NOT solution.user_id = ?;");
        prepStm.setInt(1, user.getId());

        ResultSet rs = prepStm.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String desc = rs.getString("description");

            System.out.println(id + "\t|" + title + "\t|" + desc);
        }
    }

    private static void addSolution(Connection conn, User user) {
        try {
            PreparedStatement prepStm = conn.prepareStatement("SELECT DISTINCT exercise.id, exercise.title, exercise.description " +
                    "FROM exercise JOIN solution ON exercise.id = solution.exercise_id " +
                    "WHERE NOT solution.user_id = ?;");
            prepStm.setInt(1, user.getId());
            ResultSet rs = prepStm.executeQuery();

            List<Integer> availableIds = new ArrayList<>();

            while (rs.next()) {
                availableIds.add(rs.getInt("id"));
            }

            Solution sol = new Solution();

            boolean exerciseExists = false;
            printExercises(conn, user);
            while (!exerciseExists) {
                System.out.println("\nPodaj Id zadania, do którego chcesz przypisać rozwiązanie");
                int exId = getIntFromScanner();
                String sqlEx = "SELECT id FROM exercise WHERE id = ?";
                PreparedStatement prepStm2 = conn.prepareStatement(sqlEx);
                prepStm2.setInt(1, exId);
                ResultSet rsExercises = prepStm2.executeQuery();
                if (!rsExercises.next()) {
                    System.out.println("Nie ma zadania o takim id");
                } else if (!availableIds.contains(exId)) {
                    System.out.println("Zadanie o takim id posiada już rozwiązanie");
                } else {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Podaj treść rozwiązania");
                    String desc = sc.nextLine();
                    sol.setUser(user);
                    sol.setDescription(desc);
                    sol.setExercise(Exercise.loadById(conn, exId));
                    exerciseExists = true;
                }
            }

            sol.save(conn);
            System.out.println("\nPrzypisano...\n");

            printExercises(conn, user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void viewSolutionByUser(Connection conn, User user) throws SQLException {

        int userId = user.getId();
        String sqlUsers = "SELECT created, updated, description, exercise_id FROM solution WHERE user_id = ? ORDER BY exercise_id;";
        PreparedStatement prepStm = conn.prepareStatement(sqlUsers);
        prepStm.setInt(1, userId);
        ResultSet rsUsers = prepStm.executeQuery();

        while (rsUsers.next()) {
            String created = rsUsers.getString("created");
            String updated = rsUsers.getString("updated");
            String desc = rsUsers.getString("description");
            String exeId = rsUsers.getString("exercise_id");
            System.out.println(exeId + " | stworzone: " + created + " | zaktualizowane: " + updated + " | rozwiązanie: " + desc);
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
