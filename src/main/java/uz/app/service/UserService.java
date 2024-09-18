package uz.app.service;

import uz.app.entity.Category;
import uz.app.entity.Product;
import uz.app.entity.User;
import uz.app.repository.CategoryRepository;
import uz.app.repository.ProductRepository;
import uz.app.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static uz.app.utils.Utill.*;

public class UserService {
    private CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private ProductRepository productRepository = ProductRepository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    private User currentUser;
    private List<Product> basket = new ArrayList<>();
    private List<Map<String, Object>> purchaseHistory = new ArrayList<>();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void showCategories() {
        List<Category> categories = categoryRepository.getAllCategories();
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

    public void showProducts() {
        Map<Category, List<Product>> categorizedProducts = productRepository.getAllProducts();

        if (categorizedProducts.isEmpty()) {
            System.out.println("No products available.");
        } else {
            System.out.println("Available Products by Category:");
            for (Map.Entry<Category, List<Product>> entry : categorizedProducts.entrySet()) {
                Category category = entry.getKey();
                List<Product> products = entry.getValue();

                System.out.println("===== Category =====");
                System.out.println("Category: " + category.getName());
                System.out.println("====================");

                products.forEach(product -> {
                    System.out.println("----- Product -----");
                    System.out.printf("  Name: %s%n", product.getName());
                    System.out.printf("  Price: %d%n", product.getPrice());
                    System.out.printf("  Available: %s%n", product.isAvailable() ? "Yes" : "No");
                    System.out.printf("  Count: %d%n", product.getCount());
                    System.out.println("-------------------");
                });
            }
        }
    }

    public void showBasket() {
        if (basket.isEmpty()) {
            System.out.println("Your basket is empty.");
            return;
        }

        Map<String, Product> productCountMap = new HashMap<>();
        int totalPrice = 0;

        for (Product product : basket) {
            productCountMap.put(product.getName(), product);
        }

        System.out.println("Your Basket:");
        for (Product product : productCountMap.values()) {
            int count = (int) basket
                    .stream()
                    .filter(p -> p.getName().equals(product.getName()))
                    .count();
            int price = product.getPrice() * count;
            totalPrice += price;
            System.out.printf("Name: %s, Price: %d, Count: %d, Total: %d%n", product.getName(), product.getPrice(), count, price);
        }

        System.out.println("===================");
        System.out.printf("Total Price: %d%n", totalPrice);
        System.out.println("===================");

        int action = getInteger("Do you want to confirm the purchase (1), deny (2), or go back (3)? ");
        switch (action) {
            case 1 -> {
                confirmPurchase();
            }
            case 2 -> {
                denyPurchase();
            }
            case 3 -> {
                System.out.println("Returning to main menu...");
                return;
            }
        }
    }

    private void confirmPurchase() {
        for (Product product : basket) {
            if (currentUser.getBalance() >= product.getPrice()) {
                int newBalance = currentUser.getBalance() - product.getPrice();
                currentUser.setBalance(newBalance);
                product.setCount(product.getCount() - 1);
                product.setOwner(currentUser);
                productRepository.updateCount(product);
                userRepository.updateBalance(currentUser.getId(), newBalance);

                Map<String, Object> purchaseRecord = new HashMap<>();
                purchaseRecord.put("productName", product.getName());
                purchaseRecord.put("price", product.getPrice());
                purchaseRecord.put("quantity", 1); // Assuming quantity is 1 for each purchase
                purchaseRecord.put("status", "confirmed");
                purchaseHistory.add(purchaseRecord);

                System.out.println("You have bought: " + product.getName());
            } else {
                System.out.println("Insufficient balance for: " + product.getName());
            }
        }
        basket.clear();
    }

    private void denyPurchase() {
        for (Product product : basket) {
            Map<String, Object> purchaseRecord = new HashMap<>();
            purchaseRecord.put("productName", product.getName());
            purchaseRecord.put("price", product.getPrice());
            purchaseRecord.put("quantity", 1);
            purchaseRecord.put("status", "denied");
            purchaseHistory.add(purchaseRecord);
        }
        basket.clear();
        System.out.println("Your basket has been cleared.");
    }

    public void addProductToBasket() {
        showProducts();
        String productName = getString("Enter the name of the product you want to buy: ");
        int count = getInteger("Enter the count of the product you want to buy: ");
        Optional<Product> optionalProduct = productRepository.getProductByName(productName);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.isAvailable() && product.getCount() >= count) {
                for (int i = 0; i < count; i++) {
                    basket.add(product);
                }
                product.setCount(product.getCount() - count);
                System.out.println("Product(s) added to basket!");
            } else {
                System.out.println("Product not available or insufficient stock.");
            }
        } else {
            System.out.println("Product not found.");
        }
    }

    public void history() {
        if (purchaseHistory.isEmpty()) {
            System.out.println("No purchase history available.");
            return;
        }

        Map<String, Map<String, Object>> purchaseHistoryMap = new HashMap<>();

        for (Map<String, Object> purchaseRecord : purchaseHistory) {
            String productName = (String) purchaseRecord.get("productName");
            int quantity = (int) purchaseRecord.get("quantity");
            String status = (String) purchaseRecord.get("status");

            if (!purchaseHistoryMap.containsKey(productName)) {
                Map<String, Object> details = new HashMap<>();
                details.put("confirmedQuantity", 0);
                details.put("deniedQuantity", 0);
                purchaseHistoryMap.put(productName, details);
            }

            Map<String, Object> details = purchaseHistoryMap.get(productName);
            if (status.equals("confirmed")) {
                int confirmedQuantity = (int) details.get("confirmedQuantity");
                details.put("confirmedQuantity", confirmedQuantity + quantity);
            } else if (status.equals("denied")) {
                int deniedQuantity = (int) details.get("deniedQuantity");
                details.put("deniedQuantity", deniedQuantity + quantity);
            }
        }

        System.out.println("Purchase History:");
        for (Map.Entry<String, Map<String, Object>> entry : purchaseHistoryMap.entrySet()) {
            String productName = entry.getKey();
            Map<String, Object> details = entry.getValue();
            int confirmedCount = (int) details.get("confirmedQuantity");
            int deniedCount = (int) details.get("deniedQuantity");
            int price = productRepository.getProductByName(productName).get().getPrice();

            System.out.printf("Name: %s, Price: %d, Confirmed: %d, Denied: %d%n", productName, price, confirmedCount, deniedCount);
        }
    }

    public void findProduct() {
        String productName = getString("Enter the name of the product to find: ");
        Optional<Product> optionalProduct = productRepository.getProductByName(productName);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            System.out.printf("Found Product: Name: %s, Price: %d, Available: %s, Count: %d%n",
                    product.getName(), product.getPrice(), product.isAvailable() ? "Yes" : "No", product.getCount());
        } else {
            System.out.println("Product not found.");
        }
    }

    public void findCategory() {
        String categoryName = getString("Enter category name to find: ");
        Optional<Category> optionalCategory = categoryRepository.getCategoryByName(categoryName);

        if (optionalCategory.isPresent()) {
            Category foundCategory = optionalCategory.get();
            List<Product> allProducts = productRepository.getAllProducts()
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

    public void fillBalance() {
        if (currentUser == null) {
            System.out.println("You must be signed in to fill your balance.");
            return;
        }

        Integer amount = getInteger("Enter amount to add: ");
        if (amount > 0) {
            currentUser.setBalance(currentUser.getBalance() + amount);
            userRepository.updateBalance(currentUser.getId(),amount);
            System.out.printf("Your balance has been updated. New balance: %d%n", currentUser.getBalance());
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    private static UserService userService;

    public static UserService getInstance() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }

}
