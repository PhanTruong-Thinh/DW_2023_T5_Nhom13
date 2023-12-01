package model;

import com.zaxxer.hikari.HikariDataSource;
import dao.LogDAO;
import org.jdbi.v3.core.Jdbi;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBStaging {
    private static final HikariDataSource dataSource;
    static Jdbi jdbi;
    //          8. Load initialization file
    static {
        Properties prop = new Properties();
        try (InputStream input = DBStaging.class.getClassLoader().getResourceAsStream("staging.properties")) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String serverName = prop.getProperty("serverName");
        String dbName = prop.getProperty("dbName");
        String portNumber = prop.getProperty("portNumber");
        String instance = prop.getProperty("instance");
        String userID = prop.getProperty("userID");
        String password = prop.getProperty("password");

        String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + dbName;
        if (instance != null && !instance.trim().isEmpty()) {
            url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + instance + "/" + dbName;
        }

        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(userID);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(200);
        dataSource.setMinimumIdle(30);
    }
    //      8.1 Connect database staging.db
    public static Jdbi me() {

        if (jdbi == null) {
//            8.2 insert 1 row table log with status = "error", location ="connect Database Staging"
//            LogDAO.insertLog("Database","connect", "error", "connect Database Staging");

            jdbi = Jdbi.create(dataSource);
        }
        return jdbi;
    }

    private DBStaging() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(getConnection());
    }
}
