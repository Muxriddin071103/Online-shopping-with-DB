package uz.app.controller;

import uz.app.entity.User;
import uz.app.service.AuthService;

import static uz.app.utils.Utill.*;

public class AuthController {
    AuthService authService = AuthService.getInstance();
    AdminController adminController = AdminController.getInstance();
    UserController userController = UserController.getInstance();

    public void service() {
        while (true) {
            System.out.println("========== ONLINE SHOPPING CENTRE ==========");
            System.out.println();
            System.out.println();
            System.out.println("""
                    ========== MAIN MENU ==========
                    0. Exit
                    1. Sign In
                    2. Sign Up
                    3. Confirm Code
                    """);
            switch (getInteger("Please select an option:")) {
                case 0 -> {
                    System.out.println("See you soon!");
                    return;
                }
                case 1 -> {
                    signIn();
                }
                case 2 -> {
                    signUp();
                }
                case 3 -> {
                    confirmCode();
                }
            }
        }
    }

    private void confirmCode() {
        String email = getString("Enter email: ");
        String confirmationCode = getString("Enter confirmation code: ");
        authService.verifySignIn(email, confirmationCode);
    }

    private void signUp() {
        String name = getString("Enter name: ");
        String email = getString("Enter email: ");
        String password = getString("Enter password: ");
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        authService.signUp(user);
    }

    private void signIn() {
        String email = getString("Enter email: ");
        String password = getString("Enter password: ");
        authService.signIn(email, password);
    }
}
