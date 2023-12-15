package model;

import dao.*;
import entity.Control;
import util.SendEmail;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static dao.ControlDAO.checkRecord;
import static dao.DataWareHouseDAO.*;
import static dao.LogDAO.isLastLogStatusRunning;

public class DataMigration {
    public static void runDataWareHouse() {
        // 1. Line 12 DBContext.
        // 2. Line 41 DBContext.
        // 3. Check create_at = nowDate() and status = "running" in table logs limit 1 (isLastLogStatusRunning())
        if (isLastLogStatusRunning()) {
            // 3.1 Insert 1 row in table log with eventType:Check Run , status: "cannot run", location = "start save data warehouse"
            LogDAO.insertLog("VnExpress", "Check Run", "cannot run", "start ETL");
            return;
        }
        // 4. Check row create_at = now() and status !="SBS" limit 1 in table controls
        Optional<Control> checkRecord = checkRecord("SBS");
        if (checkRecord.isPresent()) {
            // 5. Check create_at = nowDate() and status = "SDBWHS" in table controls limit 1 (checkRecordSDBWHS.isPresent())
            Optional<Control> checkRecordSDBWHS = checkRecord("SDBWHS");
            if (checkRecordSDBWHS.isPresent()) {
                // 5.1 Insert 1 row in table log with eventType:Check save success warehouse today,status: "already exist", location = "ware house"
                LogDAO.insertLog("VnExpress", "Check save success warehouse today", "already exist", "ware house");
            } else {
                // 6. Insert 1 row with name, event_type, location status ="running" in table logs
                LogDAO.insertLog("VnExpress", "Running Data wareHouse", "running", "run save warehouse");
                // 7. Load initialization file (serverName, userID, password, dbName, portNumber)
                Properties prop = new Properties();
                try (InputStream input = new FileInputStream("config.properties")) {
                    prop.load(input);
                } catch (Exception e) {
                    // 8.1 Insert 1 row in table log with status: "error", location = "load config"
                    LogDAO.insertLog("VnExpress", "Check load config", "error", "load config");
                    return;
                }
                // 9. Connect database staging.db and connect database dw_news.db
                String jdbcUrlStaging = "jdbc:mysql://" + prop.getProperty("serverName") + ":" + prop.getProperty("portNumber") + "/" + prop.getProperty("dbNameStaging");
                String jdbcUrlDWNews = "jdbc:mysql://" + prop.getProperty("serverName") + ":" + prop.getProperty("portNumber") + "/" + prop.getProperty("dbNameWarehouse");
                String username = prop.getProperty("userID");
                String password = prop.getProperty("password");
                // 10. Insert 1 row with name, description, status" SSDBWH" in table controls
                int generatedId = ControlDAO.insertControl("VnExpress", "Bắt đầu lấy dữ liệu", "SSDBWH"); // SSDBWH: Start save database warehouse
                try (Connection connectionStaging = DriverManager.getConnection(jdbcUrlStaging, username, password);
                     Connection connectionDWNews = DriverManager.getConnection(jdbcUrlDWNews, username, password)) {
                    // 11. Prepare and execute the SELECT query on the staging database
                    String selectQuery = "SELECT id, title, content, description, url, date, image, author, category, source FROM staging";
                    try (PreparedStatement selectStatement = connectionStaging.prepareStatement(selectQuery,
                            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                         ResultSet resultSet = selectStatement.executeQuery()) {
                        // 12. Insert data into dimension tables
                        List<Integer> sourceKeys = insertSourceDim(connectionDWNews, resultSet);
                        resultSet.beforeFirst();
                        List<Integer> authorKeys = insertAuthorDim(connectionDWNews, resultSet);
                        resultSet.beforeFirst();
                        List<Integer> categoryKeys = insertCategoryDim(connectionDWNews, resultSet);
                        resultSet.beforeFirst();
                        List<Integer> dateKeys = insertDateDim(connectionDWNews, resultSet);
                        resultSet.beforeFirst();
                        // 13. Insert data into the news table
                        List<Integer> newsKeys = insertNews(connectionDWNews, resultSet);
                        // 14. Insert data into the fact table (news_fact)
                        insertNewsFact(connectionDWNews, sourceKeys, authorKeys, categoryKeys, dateKeys, newsKeys);
                        // 15. Update 1 row in table control with status = "SDBWHS"
                        boolean isSuccess = ControlDAO.updateControlStatusById(generatedId, "SDBWHS"); // SDBWHS: save data warehouse success
                        // 16. Insert 1 row in table log with eventType = "ETL" status: "success", location = "save Data Warehouse"
                        LogDAO.insertLog("VnExpress", "ETL", "success", "save Data Warehouse");
                        // 16. Send mail success
                        SendEmail.sendMail("leminhlongit@gmail.com","Data WareHouse Tin Tức","save Data Warehouse success "+ new Date());
                    }
                } catch (SQLException e) {
                    // 17. Insert 1 row in table log with status: "error", location = "ETL warehouse"
                    LogDAO.insertLog("VnExpress", "Check load config", "error", "ETL warehouse");
                    // 17.1 send mail error
                    SendEmail.sendMail("leminhlongit@gmail.com","Data WareHouse Tin Tức","save Data Warehouse error "+ new Date());
                    e.printStackTrace();
                    return;
                }
            }
        } else {
            // 4.1. Insert 1 row in table log with eventType:Check DataBase Staging, status: " not found", location = "check status SBS"
            LogDAO.insertLog("VnExpress", "Check DataBase Staging", "not found", "check status SBS");
            return;
        }
    }
    public static void main(String[] args) {
        runDataWareHouse();
    }
}
