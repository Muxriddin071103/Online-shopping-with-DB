package uz.app.repository;

import uz.app.entity.User;
import uz.app.role.UsersRole;
import uz.app.utils.TestConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthRepository {
    private TestConnection testConnection = TestConnection.getInstance();

    public void updateConfirmationStatus(User user) {
        String query = "UPDATE users SET confirmed = ? WHERE email = ?";
        try (Statement statement = testConnection.getStatement();
             PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query)) {
            preparedStatement.setBoolean(1, user.isConfirmed());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        try (Statement statement = testConnection.getStatement()) {
            return getUsers(statement.executeQuery("SELECT * FROM users;"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Optional<User> getByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Statement statement = testConnection.getStatement();
             PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = mapRowToUser(resultSet);
                return Optional.of(user);
            } else {
                System.out.println("No user found with email: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<User> getUsers(ResultSet resultSet) {
        List<User> users = new ArrayList<>();
        try {
            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
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
        return user;
    }

    private static AuthRepository instance;

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }
}
