package model;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CSVReaderExample {

    // (phần mã khác không thay đổi)

    public static void main(String[] args) {
        String csvFilePath = "tempSto/temporary_news_26112023.csv";

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {

            CsvToBean<Staging> csvToBean = new CsvToBeanBuilder<Staging>(reader)
                    .withType(Staging.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Staging> stagingList = csvToBean.parse();

            // Thực hiện kết nối đến cơ sở dữ liệu MySQL
            String jdbcUrl = "jdbc:mysql://localhost:3306/staging";
            String username = "root";
            String password = "123456";

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {

                // Thực hiện insert dữ liệu từ stagingList vào bảng staging
                String insertQuery = "INSERT INTO staging (id, title, content, description, url, publication_date, image_url, author, catelory, source) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    for (Staging staging : stagingList) {
                        preparedStatement.setString(1, staging.getId());
                        preparedStatement.setString(2, staging.getTitle());
                        preparedStatement.setString(3, staging.getContent());
                        preparedStatement.setString(4, staging.getDescription());
                        preparedStatement.setString(5, staging.getLink());
                        preparedStatement.setString(6, staging.getDateTime());
                        preparedStatement.setString(7, staging.getImageUrl());
                        preparedStatement.setString(8, staging.getAuthor());
                        preparedStatement.setString(9, staging.getCategory());
                        preparedStatement.setString(10, staging.getSource());

                        // Thực hiện insert
                        preparedStatement.executeUpdate();
                    }
                }

                System.out.println("Insert into database successfully!");

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


