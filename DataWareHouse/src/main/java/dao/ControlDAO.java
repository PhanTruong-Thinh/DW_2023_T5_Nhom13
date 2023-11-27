package dao;

import entity.Config;
import entity.Control;
import entity.File;
import model.DBContext;
import org.jdbi.v3.core.Handle;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;


public class ControlDAO {
    public static Optional<Config> getConfigById(int id) {
        try (Handle handle = DBContext.me().open()) {
            String query = "SELECT id, name, description, source_path_varchar, location, separators, format, columns, create_at, update_at, create_by " +
                    "FROM config " +
                    "WHERE id = ?";
            return handle.createQuery(query)
                    .bind(0, id)
                    .mapToBean(Config.class)
                    .findOne();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public static Optional<File> getFileById(int id) {
        try (Handle handle = DBContext.me().open()) {
            String query = "SELECT id, config_id, name, row_count,colum_name, status, file_name, date_format, file_format,dir_save,dir_achive, note, create_at, update_at, create_by, update_by \n" +
                    "FROM files WHERE id = ?";
            return handle.createQuery(query)
                    .bind(0, id)
                    .mapToBean(File.class)
                    .findOne();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public static Optional<Control> checkRecord() {
        try (Handle handle = DBContext.me().open()) {
            String query = "SELECT id, create_at, status FROM control WHERE DATE(create_at) = ? AND status = 'SFS' LIMIT 1";

            LocalDate currentDate = LocalDate.now();

            return handle.createQuery(query)
                    .bind(0, currentDate)
                    .mapToBean(Control.class)
                    .findOne();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public static int insertControl(String name, String description, String status) {
        try (Handle handle = DBContext.me().open()) {
            String query = "INSERT INTO control (name, description, status, create_at, update_at) VALUES (?, ?, ?, ?, ?)";
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            int generatedId = handle.createUpdate(query)
                    .bind(0, name)
                    .bind(1, description)
                    .bind(2, status)
                    .bind(3, now)
                    .bind(4, now)
                    .executeAndReturnGeneratedKeys()
                    .mapTo(Integer.class)
                    .one();

            return generatedId;
        } catch (Exception e) {
            e.printStackTrace();
            // If an exception occurs, return -1 indicating failure
            return -1;
        }
    }

    public static boolean deleteControlById(int id) {
        try (Handle handle = DBContext.me().open()) {
            String query = "DELETE FROM control WHERE id = ?";
            int rowsDeleted = handle.createUpdate(query)
                    .bind(0, id)
                    .execute();

            // If rowsDeleted is greater than 0, the deletion was successful
            return rowsDeleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            // If an exception occurs, return false
            return false;
        }
    }
    public static boolean updateControlStatusById(int id, String newStatus) {
        try (Handle handle = DBContext.me().open()) {
            String query = "UPDATE control SET status = ?, update_at = ? WHERE id = ?";
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            int rowsUpdated = handle.createUpdate(query)
                    .bind(0, newStatus)
                    .bind(1, now)
                    .bind(2, id)
                    .execute();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args) {
//        System.out.println(getConfigById(1));
//        System.out.println(getFileById(1));
        System.out.println(getFileById(1));

    }
}
