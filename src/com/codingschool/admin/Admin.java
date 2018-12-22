package com.codingschool.admin;

import java.util.Scanner;

public class Admin {
    public static void main() {
        String task = " ";

        while (!task.equalsIgnoreCase("quit")) {
            task = printAndGetTask();
            if (task.equalsIgnoreCase("u")) {
                UserTool.main();
            } else if (task.equalsIgnoreCase("g")) {
                UserGroupTool.main();
            } else if (task.equalsIgnoreCase("e")) {
                ExerciseTool.main();
            } else if (task.equalsIgnoreCase("s")) {
                SolutionTool.main();
            }
        }

        System.out.println("\nKończenie programu");

    }

    private static String printAndGetTask() {

        Scanner sc = new Scanner(System.in);
        String task = " ";

        while (!task.toLowerCase().matches("(u)|(g)|(e)|(s)|(quit)")) {
            System.out.println("\nWybierz jedną z opcji:" +
                    "\n - u - zarządzanie użytkownikami," +
                    "\n - g - zarządzanie grupami," +
                    "\n - e - zarządzanie zadaniami," +
                    "\n - s - zarządzanie rozwiązaniami," +
                    "\n - quit - zakończenie programu.");
            task = sc.next();
        }

        return task;
    }
}
