package uz.app.service;

import uz.app.entity.Product;
import uz.app.entity.Category;
import uz.app.entity.User;
import uz.app.repository.ProductRepository;
import uz.app.repository.CategoryRepository;
import uz.app.repository.UserRepository;
import uz.app.role.UsersRole;
import uz.app.utils.TestConnection;

import static java.lang.Boolean.parseBoolean;
import static uz.app.utils.Utill.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminService {
    private ProductRepository productRepository = ProductRepository.getInstance();
    private CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    TestConnection testConnection = TestConnection.getInstance();

    public void showProducts() {
        Map<Category, List<Product>> categorizedProducts = productRepository.getAllPrForAdmin();

        if (categorizedProducts.isEmpty()) {
            System.out.println("No products available.");
        } else {
            System.out.println("Available Products by Category:");
            for (Map.Entry<Category, List<Product>> entry : categorizedProducts.entrySet()) {
                Category category = entry.getKey();
                List<Product> products = entry.getValue();

                System.out.println("Category: " + category.getName());
                products.forEach(product -> System.out.printf(" Id: %s,  Name: %s, Price: %d, Available: %s, Count: %d%n",
                        product.getId(), product.getName(), product.getPrice(), product.isAvailable() ? "Yes" : "No", product.getCount()));
            }
        }
    }

    public void showCategories() {
        List<Category> categories = categoryRepository.getAllCatForAdmin();
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
        } else {
            System.out.println("Available Categories:");
            categories.forEach(category -> {
                System.out.println("===== Category =====");
                System.out.println("ID: " + category.getId());
                System.out.println("Name: " + category.getName());
                System.out.println("Active: " + category.getActive());
                System.out.println("====================");
            });
        }
    }

    public void showUsers() {
        String query = "SELECT * FROM users WHERE role_name = 'USER'";

        try (Statement statement = testConnection.getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                System.out.println("Users:");
                do {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    boolean enabled = resultSet.getBoolean("enabled");
                    boolean confirmed = resultSet.getBoolean("confirmed");
                    double balance = resultSet.getDouble("balance");
                    String roleName = resultSet.getString("role_name");

                    System.out.println("ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Email: " + email);
                    System.out.println("Password: " + password);
                    System.out.println("Enabled: " + enabled);
                    System.out.println("Confirmed: " + confirmed);
                    System.out.println("Balance: " + balance);
                    System.out.println("Role: " + roleName);
                    System.out.println("------------------------------------");
                } while (resultSet.next());

            } else {
                System.out.println("No users available.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addProduct() {
        String name = getString("Enter product name: ");
        int price = getInteger("Enter product price: ");
        int count = getInteger("Enter product count: ");

        List<Category> categories = categoryRepository.getAllCatForAdmin();
        if (categories.isEmpty()) {
            System.out.println("No categories available. Please add a category first.");
            return;
        }

        System.out.println("Available Categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, categories.get(i).getName());
        }

        int categoryIndex = getInteger("Choose a category by entering the corresponding number: ") - 1;
        if (categoryIndex < 0 || categoryIndex >= categories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }

        Category selectedCategory = categories.get(categoryIndex);

        Product product = new Product(0, name, price, true, count, selectedCategory, null); // Use 0 for ID
        productRepository.save(product);
        System.out.println("Product added: " + product.getName());
    }

    public void addCategory() {
        String name = getString("Enter category name: ");

        Category category = new Category(0, name,true);
        categoryRepository.save(category);
        System.out.println("Category added: " + category.getName());
    }

    public void find() {
        int choice = getInteger("""
            Choose what you are going to find:
            0. BACK
            1. USER
            2. PRODUCT
            3. CATEGORY
            """);
        switch (choice) {
            case 0->{
                System.out.println("Returning....");
                return;
            }
            case 1->{findUser();}
            case 2->{findProduct();}
            case 3->{findCategory();}
        }
    }

    private void findUser() {
        String userEmail = getString("Enter user email to find: ");
        Optional<User> user = userRepository.getByEmailForAdmin(userEmail);
        if (user.isPresent()) {

            User foundUser = user.get();
            System.out.println("Found user:");
            System.out.println("  Name: " + foundUser.getName());
            System.out.println("  Email: " + foundUser.getEmail());
            System.out.println("  Password: " + foundUser.getPassword());
            System.out.println("  Enabled: " + foundUser.getEnabled());
            System.out.println("  Confirmation Code: " + foundUser.getConfirmationCode());
            System.out.println("  Confirmed: " + foundUser.isConfirmed());
            System.out.println("  Balance: " + foundUser.getBalance());
            System.out.println("  Role: " + foundUser.getRole());
        } else {
            System.out.println("User not found.");
        }
    }

    private void findProduct() {
        String productName = getString("Enter product name to find: ");
        Optional<Product> product = productRepository.getProductByName(productName);
        if (product.isPresent()) {
            Product foundProduct = product.get();
            System.out.println("Found product:");
            System.out.println("  Name: " + foundProduct.getName());
            System.out.println("  Price: " + foundProduct.getPrice());
            System.out.println("  Available: " + foundProduct.isAvailable());
            System.out.println("  Count: " + foundProduct.getCount());
            System.out.println("  Category: " + foundProduct.getCategory());
        } else {
            System.out.println("Product not found.");
        }
    }

    private void findCategory() {
        String categoryName = getString("Enter category name to find: ");
        Optional<Category> optionalCategory = categoryRepository.getCatForAdmin(categoryName);

        if (optionalCategory.isPresent()) {
            Category foundCategory = optionalCategory.get();
            List<Product> allProducts = productRepository.getAllPrForAdmin()
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            List<Product> productsByCategory = allProducts.stream()
                    .filter(product -> product.getCategory().getId() == foundCategory.getId())
                    .collect(Collectors.toList());

            System.out.println("===== Category =====");
            System.out.println("ID: " + foundCategory.getId());
            System.out.println("Name: " + foundCategory.getName());
            System.out.println("====================");

            if (productsByCategory.isEmpty()) {
                System.out.println("No products available in this category.");
            } else {
                System.out.println("Products:");
                productsByCategory.forEach(product -> {
                    System.out.println("----- Product -----");
                    System.out.printf("  Name: %s%n", product.getName());
                    System.out.printf("  Price: %d%n", product.getPrice());
                    System.out.printf("  Available: %s%n", product.isAvailable() ? "Yes" : "No");
                    System.out.printf("  Count: %d%n", product.getCount());
                    System.out.println("-------------------");
                });
            }
        } else {
            System.out.println("Category not found.");
        }
    }

    public void edit() {
        String type = """
            Choose what you want to edit:
            0. BACK
            1. USER
            2. PRODUCT
            3. CATEGORY
            """;
        switch (getInteger(type)) {
            case 0->{
                System.out.println("Returning....");
                return;
            }
            case 1 -> editUser();
            case 2 -> editProduct();
            case 3 -> editCategory();
            default -> System.out.println("Invalid update type. Please enter 1, 2, or 3.");
        }
    }

    public void editUser() {
        showUsers();
        String userEmail = getString("Enter user email to update: ");
        Optional<User> optionalUser = userRepository.getByEmailForAdmin(userEmail);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            System.out.println("Current Details:");
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Enabled: " + user.getEnabled());
            System.out.println("Confirmed: " + user.isConfirmed());
            System.out.println("Balance: " + user.getBalance());
            System.out.println("Role: " + user.getRole());

            String newName = getString("Enter new name (or press Enter to keep current): ");
            if (!newName.isEmpty()) {
                user.setName(newName);
            }

            String newEmail = getString("Enter new email (or press Enter to keep current): ");
            if (!newEmail.isEmpty()) {
                user.setEmail(newEmail);
            }

            String newPassword = getString("Enter new password (or press Enter to keep current): ");
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
            }

            String newEnabledStr = getString("Enter new enabled status (true/false): ");
            if (!newEnabledStr.isEmpty()) {
                boolean newEnabled = parseBoolean(newEnabledStr);
                user.setEnabled(newEnabled);
            }

            String newConfirmedStr = getString("Enter new confirmed status (true/false): ");
            if (!newConfirmedStr.isEmpty()) {
                boolean newConfirmed = parseBoolean(newConfirmedStr);
                user.setConfirmed(newConfirmed);
            }

            int newBalance = getInteger("Enter new balance: ");
            user.setBalance(newBalance);

            userRepository.update(user);

            System.out.println("User updated successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    public void editProduct() {
        showProducts();
        System.out.println();
        long productId = (long) getInteger("Enter product ID to edit: ");
        String name = getString("Enter new product name: ");
        int price = getInteger("Enter new product price: ");
        int count = getInteger("Enter new product count: ");

        List<Category> categories = categoryRepository.getAllCatForAdmin();
        if (categories.isEmpty()) {
            System.out.println("No categories available. Please add a category first.");
            return;
        }

        System.out.println("Available Categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, categories.get(i).getName());
        }

        int categoryIndex = getInteger("Choose a category by entering the corresponding number: ") - 1;
        if (categoryIndex < 0 || categoryIndex >= categories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }

        Category selectedCategory = categories.get(categoryIndex);

        Product product = new Product(productId, name, price, true, count, selectedCategory, null);
        productRepository.update(product);
        System.out.println("Product updated: " + product.getName());
    }

    public void editCategory() {
        showCategories();
        int categoryId = getInteger("Enter category ID to edit: ");
        String newName = getString("Enter new category name: ");
        boolean newActive = parseBoolean(getString("Enter new active status (true/false): "));

        Category category = new Category(categoryId, newName, newActive);
        categoryRepository.update(category);

        System.out.println("Category updated: " + category.getName());
    }

    public void delete(){
        String type = """
            Choose what you want to delete:
            0. BACK
            1. USER
            2. PRODUCT
            3. CATEGORY
            """;
        switch (getInteger(type)) {
            case 0->{
                System.out.println("Returning....");
                return;
            }
            case 1 -> deleteUser();
            case 2 -> deleteProduct();
            case 3 -> deleteCategory();
            default -> System.out.println("Invalid update type. Please enter 1, 2, or 3.");
        }
    }

    public void deleteProduct() {
        showProducts();
        String productName = getString("Enter product name to delete: ");
        productRepository.delete(productName);
        System.out.println("Product deleted: " + productName);
    }

    public void deleteCategory() {
        String categoryName = getString("Enter category name to delete: ");
        categoryRepository.delete(categoryName);
        System.out.println("Category deleted: " + categoryName);
    }

    public void deleteUser() {
        String userEmail = getString("Enter user email to delete: ");
        userRepository.delete(userEmail);
        System.out.println("User deleted: " + userEmail);
    }

    private static AdminService adminService;

    public static AdminService getInstance() {
        if (adminService == null) {
            adminService = new AdminService();
        }
        return adminService;
    }
}
