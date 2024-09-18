package uz.app.service;

import uz.app.controller.AdminController;
import uz.app.controller.UserController;
import uz.app.entity.User;
import uz.app.repository.AuthRepository;
import uz.app.repository.UserRepository;
import uz.app.role.UsersRole;

import java.util.Optional;

public class AuthService {
    AuthRepository authRepository = AuthRepository.getInstance();
    NotificationService notificationService = NotificationService.getInstance();
    UserController userController = UserController.getInstance();
    AdminController adminController = AdminController.getInstance();
    UserRepository userRepository = UserRepository.getInstance();

    public void signUp(User user) {
        Optional<User> optionalUser = authRepository.getByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            System.out.println("This email is already in use");
            return;
        }

        user.setRole(UsersRole.USER);
        String confirmationCode = notificationService.generateCode();
        user.setConfirmationCode(confirmationCode);
        notificationService.sendCodeToEmail(user.getEmail(), confirmationCode);

        userRepository.save(user);
    }

    public void signIn(String email, String password) {
        Optional<User> optionalUser = authRepository.getByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isConfirmed()) {
                System.out.println("You should confirm the code first");
                return;
            }
            if (user.getPassword().equals(password)) {
                showMenuBasedOnRole(user);
            } else {
                System.out.println("Wrong password");
            }
        } else {
            System.out.println("No such email");
        }
    }

    private void showMenuBasedOnRole(User user) {
        switch (user.getRole()) {
            case ADMIN:
                adminController.adminMenu();
                break;
            case USER:
                userController.userMenu(user);
                break;
            default: System.out.println("Unknown role. Access denied.");
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

}
