package dao;

import entity.Log;
import org.jdbi.v3.core.Handle;
import model.DBContext;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class LogDAO {

    public static void insertLog(String name, String eventType, String status, String location) {
        try (Handle handle = DBContext.me().open()) {
            String query = "INSERT INTO logs (name, event_type, status, location, create_at) VALUES (?, ?, ?, ?, ?)";
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            handle.createUpdate(query)
                    .bind(0, name) // Assuming 'name' is the first parameter
                    .bind(1, eventType)
                    .bind(2, status)
                    .bind(3, location)
                    .bind(4, now)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isLastLogStatusRunning() {
        try (Handle handle = DBContext.me().open()) {
            String query = "SELECT id, name, event_type, status, location, create_at FROM logs ORDER BY create_at DESC LIMIT 1";

            Optional<Log> lastLog = handle.createQuery(query)
                    .mapToBean(Log.class) // Assuming you have a Log class that corresponds to the 'logs' table
                    .findOne();

            return lastLog.isPresent() && "running".equals(lastLog.get().getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
//        System.out.printf(String.valueOf(isLastLogStatusRunning()));
//        LogDAO.insertLog("VnExpress", "Check File Sucsecc", "running", "saveFile");
    }
}
