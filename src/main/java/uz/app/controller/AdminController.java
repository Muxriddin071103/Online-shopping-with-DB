package uz.app.controller;

import uz.app.service.AdminService;

import static uz.app.utils.Utill.*;

public class AdminController {
    AdminService adminService = AdminService.getInstance();

    public void adminMenu() {
        while (true) {
            System.out.println();
            System.out.println("""
               =============== ADMIN MENU ===================
                    0. Exit
                    1. Add Product
                    2. Add Category
                    3. Show Products
                    4. Show Categories
                    5. Show Users
                    6. Find (User, Product, Category)
                    7. Update (User, Product, Category)
                    8. Delete (User, Product, Category)
               ==============================================
               """);
            switch (getInteger("Please select an option:")) {
                case 0 -> {
                    System.out.println("See you soon! Dear Admin!!!");
                    return;
                }
                case 1 -> adminService.addProduct();
                case 2 -> adminService.addCategory();
                case 3 -> adminService.showProducts();
                case 4 -> adminService.showCategories();
                case 5 -> adminService.showUsers();
                case 6 -> adminService.find();
                case 7 -> adminService.edit();
                case 8 -> adminService.delete();
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static AdminController adminController;
    public static AdminController getInstance() {
        if (adminController == null) {
            adminController = new AdminController();
        }
        return adminController;
    }
}
