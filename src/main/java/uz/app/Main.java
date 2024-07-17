package uz.app;

import uz.app.controller.AuthController;
import uz.app.service.AuthService;
import uz.app.utils.TestConnection;

public class Main {
    public static void main(String[] args) {
        new AuthController().service();
    }
}