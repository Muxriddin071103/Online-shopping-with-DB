package uz.app.repository;

import uz.app.entity.User;
import uz.app.utils.TestConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthRepository {
    TestConnection testConnection = TestConnection.getInstance();

    public void save(User user) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format(
                    "insert into users(name,email,password,enabled,confirmation_code,confirmed) values('%s','%s','%s','%s','%s','%s')",
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    "true",
                    user.getConfirmationCode(),
                    user.isConfirmed()
            );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateConfirmationStatus(User user) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("update users set confirmed = '%s' where email = '%s';",
                    user.isConfirmed(), user.getEmail());
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        try {
            Statement statement = testConnection.getStatement();
            return getUsers(statement.executeQuery(String.format("select * from users;")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Optional<User> getByEmail(String email) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from users where email = '%s';", email));
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setEnabled(resultSet.getBoolean("enabled"));
                user.setConfirmationCode(resultSet.getString("confirmation_code"));
                user.setConfirmed(resultSet.getBoolean("confirmed"));
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
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setEnabled(resultSet.getBoolean("enabled"));
                user.setConfirmationCode(resultSet.getString("confirmation_code"));
                user.setConfirmed(resultSet.getBoolean("confirmed"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    private static AuthRepository instance;

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }
}
