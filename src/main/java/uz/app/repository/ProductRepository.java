package uz.app.repository;

import uz.app.entity.Category;
import uz.app.entity.Product;
import uz.app.utils.TestConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ProductRepository {
    private TestConnection testConnection = TestConnection.getInstance();

    public Map<Category, List<Product>> getAllProducts() {
        Map<Category, List<Product>> categorizedProducts = new HashMap<>();
        String query = "SELECT p.*, c.name AS category_name, c.active AS category_active " +
                "FROM product p JOIN category c ON p.category_id = c.id " +
                "WHERE p.available = true";

        try (Statement statement = testConnection.getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                long productId = resultSet.getLong("id");
                String productName = resultSet.getString("name");
                int productPrice = resultSet.getInt("price");
                boolean productAvailable = resultSet.getBoolean("available");
                int productCount = resultSet.getInt("count_of_p");
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("category_name");
                boolean categoryAvailable = resultSet.getBoolean("category_active");

                Category category = new Category(categoryId, categoryName, categoryAvailable);
                Product product = new Product(productId, productName, productPrice, productAvailable, productCount, category, null);

                categorizedProducts.computeIfAbsent(category, k -> new ArrayList<>()).add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorizedProducts;
    }

    public Map<Category, List<Product>> getAllPrForAdmin() {
        Map<Category, List<Product>> categorizedProducts = new HashMap<>();
        String query = "SELECT p.*, c.name AS category_name, c.active AS category_active " +
                "FROM product p JOIN category c ON p.category_id = c.id ";

        try (Statement statement = testConnection.getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                long productId = resultSet.getLong("id");
                String productName = resultSet.getString("name");
                int productPrice = resultSet.getInt("price");
                boolean productAvailable = resultSet.getBoolean("available");
                int productCount = resultSet.getInt("count_of_p");
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("category_name");
                boolean categoryAvailable = resultSet.getBoolean("category_active");

                Category category = new Category(categoryId, categoryName, categoryAvailable);
                Product product = new Product(productId, productName, productPrice, productAvailable, productCount, category, null);

                categorizedProducts.computeIfAbsent(category, k -> new ArrayList<>()).add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorizedProducts;
    }

    public Optional<Product> getProductByName(String name) {
        String query = String.format("SELECT p.*, c.name AS category_name FROM product p " +
                "JOIN category c ON p.category_id = c.id WHERE p.name = '%s'", name);

        try (Statement statement = testConnection.getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return Optional.of(new Product(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("price"),
                        resultSet.getBoolean("available"),
                        resultSet.getInt("count_of_p"),
                        new Category(resultSet.getInt("category_id"), resultSet.getString("category_name"),true),
                        null // Owner can be set later if needed
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void save(Product product) {
        String query = String.format("INSERT INTO product (name, price, available, count_of_p, category_id) VALUES ('%s', %d, %b, %d, %d)",
                product.getName(), product.getPrice(), product.isAvailable(), product.getCount(), product.getCategory().getId());

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Product product) {
        String query = String.format("UPDATE product SET name = '%s', price = %d, available = %b, count_of_p = %d, category_id = %d WHERE id = %d",
                product.getName(), product.getPrice(), product.isAvailable(), product.getCount(), product.getCategory().getId(), product.getId());

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCount(Product product) {
        String query = String.format("UPDATE product SET count_of_p = %d, owner_id = %d WHERE id = %d",
                product.getCount(), product.getOwner() != null ? product.getOwner().getId() : null, product.getId());

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String productName) {
        String query = "UPDATE product SET available = false WHERE name = '" + productName + "'";

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ProductRepository productRepository;

    public static ProductRepository getInstance() {
        if (productRepository == null) {
            productRepository = new ProductRepository();
        }
        return productRepository;
    }
}
