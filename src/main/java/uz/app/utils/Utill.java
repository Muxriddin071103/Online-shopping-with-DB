package uz.app.utils;

import java.util.Scanner;

public interface Utill {

    Scanner scanner = new Scanner(System.in);

    static String getString(String text) {
        System.out.println(text);
        return scanner.nextLine();
    }

    static Integer getInteger(String text) {
        try {
            System.out.println(text);
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return getInteger(text);
        }
    }
}
