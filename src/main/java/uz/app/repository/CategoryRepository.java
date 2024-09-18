package uz.app.repository;

import uz.app.entity.Category;
import uz.app.utils.TestConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository {
    private TestConnection testConnection = TestConnection.getInstance();

    public List<Category>   getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Statement statement = testConnection.getStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM category WHERE active = true");
            while (resultSet.next()) {
                categories.add(new Category(resultSet.getInt("id"), resultSet.getString("name"), true));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Category> getAllCatForAdmin() {
        List<Category> categories = new ArrayList<>();
        try (Statement statement = testConnection.getStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM category");
            while (resultSet.next()) {
                categories.add(new Category(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getBoolean("active")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void save(Category category) {
        String query = String.format("INSERT INTO category (name, active) VALUES ('%s', true)", category.getName());

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Category> getCategoryByName(String name) {
        String query = String.format("SELECT * FROM category WHERE name = '%s' AND active = true", name);

        try (Statement statement = testConnection.getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return Optional.of(new Category(resultSet.getInt("id"), resultSet.getString("name"), true));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Category> getCatForAdmin(String name) {
        String query = String.format("SELECT * FROM category WHERE name = '%s'", name);

        try (Statement statement = testConnection.getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return Optional.of(new Category(resultSet.getInt("id"), resultSet.getString("name"), true));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void update(Category category) {
        String query = String.format("UPDATE category SET name = '%s', active = '%b' WHERE id = %d", category.getName(), category.getActive(), category.getId());

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String categoryName) {
        String query = String.format("UPDATE category SET active = false WHERE name = '%s'", categoryName);

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static CategoryRepository categoryRepository;

    public static CategoryRepository getInstance() {
        if (categoryRepository == null) {
            categoryRepository = new CategoryRepository();
        }
        return categoryRepository;
    }
}
