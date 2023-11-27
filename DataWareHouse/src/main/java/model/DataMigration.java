package model;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataMigration {
    private static List<String> listId = new ArrayList<>();

    private static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    private static List<Integer> insertSourceDim(Connection connection, ResultSet resultSet) throws SQLException {
        List<Integer> generatedKeysList = new ArrayList<>();
        String insertQuery = "INSERT INTO source_dim (name, website, create_at, update_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            while (resultSet.next()) {
                insertStatement.setString(1, resultSet.getString("source"));
                insertStatement.setString(2, resultSet.getString("source"));
                insertStatement.setTimestamp(3, getCurrentTimestamp());
                insertStatement.setTimestamp(4, getCurrentTimestamp());

                insertStatement.executeUpdate();

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedKeysList.add(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return generatedKeysList;
    }

    private static List<Integer> insertAuthorDim(Connection connection, ResultSet resultSet) throws SQLException {
        List<Integer> generatedKeysList = new ArrayList<>();
        String insertQuery = "INSERT INTO author_dim (name, create_at, update_at) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            while (resultSet.next()) {
                insertStatement.setString(1, resultSet.getString("author"));
                insertStatement.setTimestamp(2, getCurrentTimestamp());
                insertStatement.setTimestamp(3, getCurrentTimestamp());

                insertStatement.executeUpdate();

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedKeysList.add(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return generatedKeysList;
    }

    private static List<Integer> insertCategoryDim(Connection connection, ResultSet resultSet) throws SQLException {
        List<Integer> generatedKeysList = new ArrayList<>();
        String insertQuery = "INSERT INTO category_dim (name, create_at, update_at) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            while (resultSet.next()) {
                insertStatement.setString(1, resultSet.getString("category"));
                insertStatement.setTimestamp(2, getCurrentTimestamp());
                insertStatement.setTimestamp(3, getCurrentTimestamp());
                insertStatement.executeUpdate();

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedKeysList.add(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return generatedKeysList;
    }

    private static List<Integer> insertDateDim(Connection connection, ResultSet resultSet) throws SQLException {
        List<Integer> generatedKeysList = new ArrayList<>();
        String insertQuery = "INSERT INTO date_dim (date, create_at, update_at) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            while (resultSet.next()) {
                insertStatement.setString(1, resultSet.getString("publication_date"));
                insertStatement.setTimestamp(2, getCurrentTimestamp());
                insertStatement.setTimestamp(3, getCurrentTimestamp());

                insertStatement.executeUpdate();

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedKeysList.add(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return generatedKeysList;
    }

    private static List<Integer> insertNews(Connection connection, ResultSet resultSet) throws SQLException {
        List<Integer> generatedKeysList = new ArrayList<>();
        String insertQuery = "INSERT INTO news_dim (news_sk,title, content, description, url, image, create_at, update_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            while (resultSet.next()) {
                // Set values for the corresponding keys
                insertStatement.setString(1, resultSet.getString("id"));
                listId.add(resultSet.getString("id"));
                insertStatement.setString(2, resultSet.getString("title"));
                insertStatement.setString(3, resultSet.getString("content"));
                insertStatement.setString(4, resultSet.getString("description"));
                insertStatement.setString(5, resultSet.getString("url"));
                insertStatement.setString(6, resultSet.getString("image_url"));
                insertStatement.setTimestamp(7, getCurrentTimestamp());
                insertStatement.setTimestamp(8, getCurrentTimestamp());
                // Thực hiện insert
                insertStatement.executeUpdate();

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedKeysList.add(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return generatedKeysList;
    }

    private static void insertNewsFact(Connection connection, List<Integer> sourceKeys, List<Integer> authorKeys, List<Integer> categoryKeys, List<Integer> dateKeys, List<Integer> newsKeys) throws SQLException {
        String insertQuery = "INSERT INTO news_fact (fact_sk,news_sk, source_sk, author_sk, category_sk, date_sk, create_at, update_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            for (int i = 0; i < sourceKeys.size(); i++) {
                int sourceKey = sourceKeys.get(i);
                int authorKey = authorKeys.get(i);
                int categoryKey = categoryKeys.get(i);
                int dateKey = dateKeys.get(i);
                int newsKey = newsKeys.get(i);
                insertStatement.setString(1, listId.get(i));
                insertStatement.setInt(2, newsKey);
                insertStatement.setInt(3, sourceKey);
                insertStatement.setInt(4, authorKey);
                insertStatement.setInt(5, categoryKey);
                insertStatement.setInt(6, dateKey);
                insertStatement.setTimestamp(7, getCurrentTimestamp());
                insertStatement.setTimestamp(8, getCurrentTimestamp());
                insertStatement.executeUpdate();
            }
        }
    }

    public static void main(String[] args) {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jdbcUrlStaging = "jdbc:mysql://" + prop.getProperty("serverName") + ":" + prop.getProperty("portNumber") + "/" + prop.getProperty("dbNameStaging");
        String jdbcUrlDWNews = "jdbc:mysql://" + prop.getProperty("serverName") + ":" + prop.getProperty("portNumber") + "/" + prop.getProperty("dbNameWarehouse");
        String username = prop.getProperty("userID");
        String password = prop.getProperty("password");

        try (Connection connectionStaging = DriverManager.getConnection(jdbcUrlStaging, username, password); Connection connectionDWNews = DriverManager.getConnection(jdbcUrlDWNews, username, password)) {

            String selectQuery = "SELECT * FROM staging";
            try (PreparedStatement selectStatement = connectionStaging.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet resultSet = selectStatement.executeQuery()) {
                List<Integer> sourceKeys = insertSourceDim(connectionDWNews, resultSet);
                resultSet.beforeFirst();
                List<Integer> authorKeys = insertAuthorDim(connectionDWNews, resultSet);
                resultSet.beforeFirst();
                List<Integer> categoryKeys = insertCategoryDim(connectionDWNews, resultSet);
                resultSet.beforeFirst();
                List<Integer> dateKeys = insertDateDim(connectionDWNews, resultSet);
                resultSet.beforeFirst();
                List<Integer> newsKeys = insertNews(connectionDWNews, resultSet);
                // Chèn dữ liệu vào bảng news_fact
                insertNewsFact(connectionDWNews, sourceKeys, authorKeys, categoryKeys, dateKeys, newsKeys);
            } catch (SQLException e) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
