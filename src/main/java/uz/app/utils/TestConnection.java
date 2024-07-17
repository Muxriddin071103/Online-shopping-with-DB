package uz.app.utils;

import java.sql.*;
import java.util.Properties;


public class TestConnection {
    private final  String databaseURL = "jdbc:postgresql://127.0.0.1:5432/online_shop";
    private  Connection connection = null;

    public  void test() {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "root123");
        try {
             connection = DriverManager.getConnection(databaseURL, props);

//            Statement statement = connection.createStatement();
//            statement.execute("update users set password = 'parol'");
//            PreparedStatement preparedStatement = connection.prepareStatement("insert into users(name,email,password,enabled) values(?,?,?,?)");
//            preparedStatement.setString(1,"ali");
//            preparedStatement.setString(2,"ali@gmail.com");
//            preparedStatement.setString(3,"root123");
//            preparedStatement.setBoolean(4,true);
//            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Statement getStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static TestConnection instance;

    public static TestConnection getInstance() {
        if (instance == null) {
            instance = new TestConnection();
            instance.test();
        }
        return instance;
    }
}
