package uz.app.controller;

import uz.app.entity.User;
import uz.app.service.AuthService;

import static uz.app.utils.Utill.getInteger;
import static uz.app.utils.Utill.getString;

public class AuthController {
    AuthService authService = AuthService.getInstance();

    public void service() {
        while (true) {
            switch (getInteger("""
                    0 exit
                    1 sign in
                    2 sign up
                    3 confirm code
                    """)) {
                case 0 -> {
                    System.out.println("See you soon!");
                    return;
                }
                case 1 -> {
                    String email = getString("Enter email: ");
                    String password = getString("Enter password: ");
                    authService.signIn(email, password);
                }
                case 2 -> {
                    String name = getString("Enter name: ");
                    String email = getString("Enter email: ");
                    String password = getString("Enter password: ");
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(password);
                    authService.signUp(user);
                }
                case 3 -> {
                    String email = getString("Enter email: ");
                    String confirmationCode = getString("Enter confirmation code: ");
                    authService.verifySignIn(email, confirmationCode);
                }
            }
        }
    }
}
