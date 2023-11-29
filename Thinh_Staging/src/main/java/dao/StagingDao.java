package dao;

import model.DBStaging;
import org.jdbi.v3.core.Handle;

import java.util.List;

public class StagingDao {
    public static void insertStaging(String id, String title, String content, String description, String url, String date, String image, String author, String category, String source) {
        try (Handle handle = DBStaging.me().open()) {
            String query = "INSERT INTO staging (id, title, content, description, url, date, image, author, category, source) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            handle.createUpdate(query)
                    .bind(0, id)
                    .bind(1, title)
                    .bind(2, content)
                    .bind(3, description)
                    .bind(4, url)
                    .bind(5, date)
                    .bind(6, image)
                    .bind(7, author)
                    .bind(8, category)
                    .bind(9, source)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<String> readIDsFromStaging() {
        try (Handle handle = DBStaging.me().open()) {
            // Thực hiện truy vấn để lấy dữ liệu ID từ bảng staging
            String query = "SELECT id FROM staging";

            return handle.createQuery(query)
                    .mapTo(String.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, trả về một danh sách trống
            return List.of();
        }
    }

    public static void deleteDataStagingtable(){
        try (Handle handle = DBStaging.me().open()) {
            // Thực hiện truy vấn để lấy dữ liệu ID từ bảng staging
            String query = "TRUNCATE TABLE staging";

            handle.createUpdate(query).execute();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
