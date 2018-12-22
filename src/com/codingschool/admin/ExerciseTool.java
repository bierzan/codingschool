package com.codingschool.admin;

import com.codingschool.db.DatabaseConnection;
import com.codingschool.model.Exercise;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ExerciseTool {
    public static void main() {
        String task = " ";

        try (Connection conn = DatabaseConnection.getConnection()) {
            while (!task.equalsIgnoreCase("quit")) {
                task = printAndGetTask(conn);
                if (task.equalsIgnoreCase("add")) {
                    addExercise(conn);
                } else if (task.equalsIgnoreCase("edit")) {
                    editExercise(conn);
                } else if (task.equalsIgnoreCase("delete")) {
                    deleteExercise(conn);
                }
            }

            System.out.println("\nPowrót do panelu administratora");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static String printAndGetTask(Connection conn) throws SQLException {

        Statement stm = conn.createStatement();

        ResultSet rs = stm.executeQuery("SELECT * FROM exercise");
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String desc = rs.getString("description");

            System.out.println(id + "\t|" + title + "\t|" + desc);
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

    private static void addExercise(Connection conn) {
        Scanner sc = new Scanner(System.in);
        String title;
        String desc;

        System.out.println("Podaj tytuł zadania");
        title = sc.nextLine();

        System.out.println("Podaj opis zadania");
        desc = sc.nextLine();

        try {
            Exercise exercise = new Exercise(title, desc);
            exercise.save(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Zadanie zostało dodane\n");

    }

    private static void editExercise(Connection conn) {
        int exId = 0;

        String title;
        String desc;

        System.out.println("Podaj id zadania, które chcesz edytować.");
        exId = getIntFromScanner();

        Scanner sc = new Scanner(System.in);

        System.out.println("Podaj nowy tytuł zadania");
        title = sc.nextLine();

        System.out.println("Podaj nowy opis zadania");
        desc = sc.nextLine();

        try {

            Exercise exToEdit = Exercise.loadById(conn, exId);
            exToEdit.setTitle(title);
            exToEdit.setDescription(desc);
            exToEdit.update(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Zadanie zostało zmienione\n");

    }

    private static void deleteExercise(Connection conn) {
        int exId = 0;

        System.out.println("Podaj id zadania, które chcesz usunąć.");
        exId = getIntFromScanner();

        try {
            Exercise.loadById(conn, exId).delete(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Zadanie zostało usunięte\n");

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
