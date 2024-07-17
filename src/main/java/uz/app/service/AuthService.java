package uz.app.service;

import uz.app.entity.User;
import uz.app.repository.AuthRepository;

import java.util.Optional;

public class AuthService {
    AuthRepository authRepository = AuthRepository.getInstance();
    NotificationService notificationService = NotificationService.getInstance();

    public void signUp(User user) {
        Optional<User> optionalUser = authRepository.getByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            System.out.println("This email is already in use");
            return;
        }

        String confirmationCode = notificationService.generateCode();
        user.setConfirmationCode(confirmationCode);
        notificationService.sendCodeToEmail(user.getEmail(), confirmationCode);

        authRepository.save(user);
    }

    public void signIn(String email, String password) {
        Optional<User> optionalUser = authRepository.getByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isConfirmed()) {
                System.out.println("You should confirm the code first");
                return;
            }
            if (user.getPassword().equals(password) && user.isConfirmed()) {
                System.out.println("Welcome to the system");
            } else {
                System.out.println("Wrong password");
            }
        } else {
            System.out.println("No such email");
        }
    }

    public boolean verifyConfirmationCode(String email, String confirmationCode) {
        Optional<User> optionalUser = authRepository.getByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getConfirmationCode().equals(confirmationCode)) {
                user.setConfirmed(true);
                authRepository.updateConfirmationStatus(user);
                return true;
            }
        }
        return false;
    }

    public void verifySignIn(String email, String confirmationCode) {
        if (verifyConfirmationCode(email, confirmationCode)) {
            System.out.println("Account confirmed. You can now sign in without a confirmation code.");
        } else {
            System.out.println("Invalid confirmation code");
        }
    }

    private static AuthService authService;
    public static AuthService getInstance() {
        if (authService == null) {
            authService = new AuthService();
        }
        return authService;
    }

    public void allUsers() {
        authRepository.getAllUsers().forEach(System.out::println);
    }
}
