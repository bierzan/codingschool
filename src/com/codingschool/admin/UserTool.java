package com.codingschool.admin;

import com.codingschool.db.DatabaseConnection;
import com.codingschool.model.User;
import com.codingschool.model.UserGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class UserTool {
    public static void main() {

        String task = " ";

        try (Connection conn = DatabaseConnection.getConnection()) {
            while (!task.equalsIgnoreCase("quit")) {
                task = printAndGetTask(conn);
                if (task.equalsIgnoreCase("add")) {
                    addUser(conn);
                } else if (task.equalsIgnoreCase("edit")){
                    editUser(conn);
                }
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
        int groupId = 0;

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
                uniqueTest = doesEmailExist(conn, email);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (uniqueTest) {
                System.out.println("Taki email już istnieje, podaj inny");
            }
        }


        System.out.println("Podaj hasło");
        password = sc.next();

        boolean stop = false;

        while (!stop) {
            System.out.println("Podaj nr grupy do której chesz przypisać użytkownika");
            while (!sc.hasNextInt()) {
                System.out.println("To nie jest nr Id");
                sc.next();
            }

            groupId = sc.nextInt();

            try {
                if (!doesGroupExist(conn, groupId)) {
                    System.out.println("Taka grupa nie istnieje w bazie danych.\nCzy chcesz dodać nową grupę? (T/N)");
                    String answer = sc.next();
                    while (!answer.toLowerCase().matches("t|n")) {
                        System.out.println("Czy chcesz dodać nową grupę?" +
                                "\nT - tak," +
                                "\nN - nie");
                        answer = sc.next();
                    }

                    if (answer.toLowerCase().equals("t")) {
                        System.out.println("Podaj nazwę dla nowej grupy");
                        String newGroupName = sc.next();
                        UserGroup newGroup = new UserGroup(newGroupName);
                        newGroup.save(conn);
                        groupId = newGroup.getId();
                        stop = true;
                    }

                } else {
                    stop = true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            User user = new User(username, password, email, groupId);
            user.save(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Użytkownik został dodany\n");

    }

    private static void editUser(Connection conn) {
        int userId = 0;

        String username;
        String password;
        String email = " ";
        int groupId = 0;

        System.out.println("Podaj id użytkownika, którego chcesz edytować.");
        userId = getIntFromScanner();

        Scanner sc = new Scanner(System.in);

        System.out.println("Podaj nową nazwę dla użytkownika");
        username = sc.next();

        System.out.println("Podaj nowy email użytkownika");
        boolean uniqueTest = true;

        while (uniqueTest) {
            email = sc.next();
            while (!email.matches("[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.([a-zA-Z]{2,}){1}")) {
                System.out.println("to nie jest adres e-mail");
                email = sc.next();
            }

            try {
                uniqueTest = doesEmailExist(conn, email);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (uniqueTest) {
                System.out.println("Taki email już istnieje, podaj inny");
            }
        }

        System.out.println("Podaj nowe hasło");
        password = sc.next();

        boolean stop = false;

        while (!stop) {
            System.out.println("Podaj nr grupy do której chesz przypisać użytkownika");
            while (!sc.hasNextInt()) {
                System.out.println("To nie jest nr Id");
                sc.next();
            }

            groupId = sc.nextInt();

            try {
                if (!doesGroupExist(conn, groupId)) {
                    System.out.println("Taka grupa nie istnieje w bazie danych.\nCzy chcesz dodać nową grupę? (T/N)");
                    String answer = sc.next();
                    while (!answer.toLowerCase().matches("t|n")) {
                        System.out.println("Czy chcesz dodać nową grupę?" +
                                "\nT - tak," +
                                "\nN - nie");
                        answer = sc.next();
                    }

                    if (answer.toLowerCase().equals("t")) {
                        System.out.println("Podaj nazwę dla nowej grupy");
                        String newGroupName = sc.next();
                        UserGroup newGroup = new UserGroup(newGroupName);
                        newGroup.save(conn);
                        groupId = newGroup.getId();
                        stop = true;
                    }

                } else {
                    stop = true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            User userToEdit = User.loadById(conn, userId);
            userToEdit.setUsername(username);
            userToEdit.setEmail(email);
            userToEdit.setPassword(password);
            userToEdit.setGroup(groupId);

            userToEdit.update(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Użytkownik został edytowany\n");

    }

    private static boolean doesEmailExist(Connection conn, String email) throws SQLException {

        String sql = "SELECT * FROM users WHERE email = ? ;";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setString(1, email);
        ResultSet rs = prepStm.executeQuery();
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }


    private static boolean doesGroupExist(Connection conn, int groupId) throws SQLException {

        String sql = "SELECT * FROM users WHERE user_group_id = ? ;";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setInt(1, groupId);
        ResultSet rs = prepStm.executeQuery();
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    private static int getIntFromScanner()//todo mozna podmienic z resztą sprawdzen
    {
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) {
            System.out.println("To nie jest numer ID");
            scan.next();
        }
        return scan.nextInt();
    }
}
