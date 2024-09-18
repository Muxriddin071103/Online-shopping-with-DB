package uz.app.repository;

import uz.app.entity.User;
import uz.app.utils.TestConnection;
import uz.app.role.UsersRole;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final TestConnection testConnection = TestConnection.getInstance();

    /*private void initializeDefaultAdmin() {
        String adminEmail = "admin@gmail.com";
        Optional<User> existingAdmin = getByEmail(adminEmail);

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword("admin123");
            admin.setEnabled(true);
            admin.setConfirmationCode(null);
            admin.setConfirmed(true);
            admin.setBalance(0);
            admin.setRole(UsersRole.ADMIN);

            save(admin);
            System.out.println("Default admin user created.");
        }
    }*/

    public void save(User user) {
        String query = "INSERT INTO users (name, email, password, enabled, confirmation_code, confirmed, balance, role_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Statement statement = testConnection.getStatement();
             PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setBoolean(4, user.getEnabled());
            preparedStatement.setString(5, user.getConfirmationCode());
            preparedStatement.setBoolean(6, user.isConfirmed());
            preparedStatement.setDouble(7, user.getBalance());
            preparedStatement.setString(8, user.getRole().name());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBalance(long userId, int newBalance) {
        String query = String.format("UPDATE users SET balance = %d WHERE id = %d", newBalance, userId);

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> getByEmail(String email) {
        try (Statement statement = testConnection.getStatement()) {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM users WHERE email = '%s' and enabled = 'true'", email));
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setEnabled(resultSet.getBoolean("enabled"));
                user.setConfirmationCode(resultSet.getString("confirmation_code"));
                user.setConfirmed(resultSet.getBoolean("confirmed"));
                user.setBalance(resultSet.getInt("balance"));
                user.setRole(UsersRole.valueOf(resultSet.getString("role_name")));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> getByEmailForAdmin(String email) {
        try (Statement statement = testConnection.getStatement()) {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM users WHERE email = '%s'", email));
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setEnabled(resultSet.getBoolean("enabled"));
                user.setConfirmationCode(resultSet.getString("confirmation_code"));
                user.setConfirmed(resultSet.getBoolean("confirmed"));
                user.setBalance(resultSet.getInt("balance"));
                user.setRole(UsersRole.valueOf(resultSet.getString("role_name")));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Statement statement = testConnection.getStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE enabled = 'true'");
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setEnabled(resultSet.getBoolean("enabled"));
                user.setConfirmationCode(resultSet.getString("confirmation_code"));
                user.setConfirmed(resultSet.getBoolean("confirmed"));
                user.setBalance(resultSet.getInt("balance"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void delete(String email) {
        String query = "UPDATE users SET enabled = false WHERE email = '" + email + "'";

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        String query = String.format("UPDATE users SET name = '%s', email = '%s', password = '%s', enabled = %b, confirmed = %b, balance = %d WHERE id = %d",
                user.getName(), user.getEmail(), user.getPassword(), user.getEnabled(), user.isConfirmed(), user.getBalance(), user.getId());

        try (Statement statement = testConnection.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static UserRepository userRepository;
    public static UserRepository getInstance(){
        if(userRepository == null){
            userRepository = new UserRepository();
//            userRepository.initializeDefaultAdmin();
        }
        return userRepository;
    }
}
