package com.codingschool;


import com.codingschool.admin.Admin;
import com.codingschool.db.DatabaseConnection;
import com.codingschool.user.AddOrViewSolution;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("WITAMY W SZKOLE PROGRAMOWANIA\n\n");

        Scanner sc = new Scanner(System.in);
        boolean validation = false;
        String input = "";
        while (!validation) {
            System.out.println("Podaj swoje Id użytkownika lub -quit- by zakończyć program");
            input = sc.next();
            if (input.equals("admin")) {
                Admin.main();
                validation = true;
            } else if (input.equals("quit")) {
                System.out.println("Kończenie pracy programu...");
                validation = true;
            } else if (!StringUtils.isStrictlyNumeric(input)) {
                System.out.println("Podany numer Id nie jest liczbą. \n");
            } else if (doesUserExists(Integer.valueOf(input))) {
                String[] userId = {input};
                AddOrViewSolution.main(userId);
                validation = true;
            } else {
                System.out.println("Taki użytkownik nie istnieje");
            }
        }

    }

    private static boolean doesUserExists(int userId) {

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM users WHERE id = ? ;";
            PreparedStatement prepStm = conn.prepareStatement(sql);
            prepStm.setInt(1, userId);
            ResultSet rs = prepStm.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
