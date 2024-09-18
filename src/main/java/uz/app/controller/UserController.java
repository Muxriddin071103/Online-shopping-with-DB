package uz.app.controller;

import uz.app.entity.User;
import uz.app.service.UserService;

import static uz.app.utils.Utill.getInteger;

public class UserController {
    UserService userService = UserService.getInstance();

    public void userMenu(User user) {
        userService.setCurrentUser(user);

        while (true) {
            System.out.println();
            System.out.println("""
                =========== USER MENU ===========
                       0. Exit
                       1. Show Categories
                       2. Show Products
                       3. Add Product To Basket
                       4. View Basket
                       5. Find Product
                       6. Find Category
                       7. Fill Balance
                       8. Show balance
                       9. History
                =================================
                """);
            switch (getInteger("Please select an option:")) {
                case 0 -> {
                    System.out.println("See you soon! Dear " + user.getName());
                    return;
                }
                case 1 -> userService.showCategories();
                case 2 -> userService.showProducts();
                case 3 -> userService.addProductToBasket();
                case 4 -> userService.showBasket();
                case 5 -> userService.findProduct();
                case 6 -> userService.findCategory();
                case 7 -> userService.fillBalance();
                case 8 -> {
                    System.out.println("Your balance is " + user.getBalance());
                }
                case 9 -> {userService.history();}
            }
        }
    }

    private static UserController userController;

    public static UserController getInstance() {
        if (userController == null) {
            userController = new UserController();
        }
        return userController;
    }
}
