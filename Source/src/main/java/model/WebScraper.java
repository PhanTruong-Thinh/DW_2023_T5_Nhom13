package model;

import com.opencsv.CSVWriter;
import dao.ControlDAO;
import dao.LogDAO;
import entity.Config;
import entity.Control;
import entity.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.SendEmail;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static dao.ControlDAO.*;
import static dao.LogDAO.isLastLogStatusRunning;

public class WebScraper {
    private static String BASE_URL;
    private SimpleDateFormat dateFormat;

    private static boolean isToday(String dateString) throws ParseException {
        Date postingDate = parseVietnameseDate(dateString);
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(postingDate).equals(dateFormat.format(currentDate));
    }

    public static Date parseVietnameseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy - HH:mm", new Locale("vi", "VN"));
        return dateFormat.parse(dateString);
    }

    public static Map<String, List<String>> scrapeNews(int stt, String url, Map<String, List<String>> newsData) {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int sttCounter = stt;
        Elements articleElements = document.select("article.article-item");
        for (Element item : articleElements) {  
            try {
                String subUrl = item.select("a").attr("href");
                String fullUrl = BASE_URL + subUrl;
                Thread.sleep(1000); // Adjust the delay time as needed
                Document subDocument = Jsoup.connect(fullUrl).get();
                Element newsElement = subDocument.selectFirst("div.grid-container");
                if (newsElement == null) {
                    continue;
                }
                Element linkElement = subDocument.select("h3.article-title a").first();
                String hrefValue = linkElement.attr("href");
                // Extract content from the individual article URL
                String articleTitle = newsElement.selectFirst("h1.title-page.detail").text().trim();
                Element articleContentElement = newsElement.selectFirst("div.singular-content");
                String articleContent = (articleContentElement != null) ? articleContentElement.text().trim() : "";
                Element authorElement = newsElement.selectFirst("div.author-name");
                if (authorElement == null) {
                    continue;
                }
                String author = authorElement.text().trim();
                String category = newsElement.selectFirst("ul.breadcrumbs a").text().trim();
                String postingTime = newsElement.selectFirst("time.author-time").text().trim();
                // Extract additional information
                Element imageElement = newsElement.selectFirst("figure.image img");
                String imageUrl = (imageElement != null) ? imageElement.attr("data-original") : "";
                // Extract description
                Element descriptionElement = newsElement.selectFirst("h2.singular-sapo");
                String description = (descriptionElement != null) ? descriptionElement.text().trim() : "";

                sttCounter++;
                if (isToday(postingTime)) {
                    // Create a list to store article information
                    List<String> articleInfo = new ArrayList<>();
                    articleInfo.add(articleTitle);
                    articleInfo.add(articleContent);
                    articleInfo.add(description);
                    articleInfo.add(fullUrl);
                    articleInfo.add(postingTime);
                    articleInfo.add(imageUrl);
                    articleInfo.add(author);
                    articleInfo.add(category);
                    articleInfo.add(url);
                    // Add the information to the newsData map
                    newsData.put("ID" + sttCounter, articleInfo);
                }
            } catch (Exception ignored) {
            }
        }

        return newsData;
    }

    private static void movePreviousDayFile(File file, String archiveDirectoryPath) {
        try {
            String dateFormatValue = file.getDate_format();
            String fileNameValue = file.getFile_name();
            String fileFormatValue = file.getFile_format();
            String fileDirSto = file.getDir_save();

            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatValue);
            String yesterday = dateFormat.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
            String previousDayFileName = fileNameValue + yesterday + "." + fileFormatValue;

            java.io.File previousDayFile = new java.io.File(fileDirSto + "/" + previousDayFileName);
            if (previousDayFile.exists()) {
                java.io.File archiveDirectory = new java.io.File(archiveDirectoryPath);
                // Create the archive directory if it doesn't exist
                if (!archiveDirectory.exists()) {
                    archiveDirectory.mkdirs();
                }
                // Move the file to the archive directory
                java.io.File destinationFile = new java.io.File(archiveDirectory, previousDayFile.getName());
                Files.move(previousDayFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean saveToCSV(Map<String, List<String>> newsData, Config config, File file) throws IOException {
        movePreviousDayFile(file, file.getDir_achive());
        String dateFormatValue = file.getDate_format();
        String fileNameValue = file.getFile_name();
        String fileFormatValue = file.getFile_format();
        String columNameValue = file.getColum_name();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatValue);
        String currentDate = dateFormat.format(new Date());
        String csvFileName = fileNameValue + currentDate + "." + fileFormatValue;
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file.getDir_save() + "/" + csvFileName))) {
            // Write CSV header
            String[] header = {columNameValue};
            csvWriter.writeNext(header);

            int id = 1;
            for (Map.Entry<String, List<String>> entry : newsData.entrySet()) {
                List<String> data = entry.getValue();
                String[] row = {
                        String.valueOf(id),
                        data.get(0),  // Title
                        data.get(1),  // Content
                        data.get(2),  // Description
                        data.get(3),  // URL
                        data.get(4),  // Publication Date
                        data.get(5),  // Image URL
                        data.get(6),  // Author
                        data.get(7),  // Category
                        data.get(8),  // Category
                };
                id++;
                csvWriter.writeNext(row);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void runSource() throws IOException {
        // 1. Line 12 DBContext.
        // 2. Line 41 DBContext.
        // 3. Check create_at = nowDate() and status = "running" in table logs limit 1 (runningLog.isPresent())
        if (isLastLogStatusRunning()) {
            // 3.1 Insert 1 row in table log with status: "cannot run", location = "start save files"
            LogDAO.insertLog("DanTri", "Data retrieved", "cannot run", "start save files");
            return;
        }
        // 4. Check create_at = nowDate() and status = "SFS" in table controls limit 1 (checkRecord.isPresent())
        Optional<Control> checkRecord = checkRecord("DanTri");
        if (checkRecord.isPresent()) {
            // 4.1 Insert 1 row in table log with status: "already exist", location = "find files"
            LogDAO.insertLog("DanTri", "Data retrieved", "already exist", "find files");
        } else {
            // 5. Insert 1 row with name, event_type = "Taking data", location = "run save files" status ="running" in table logs
            LogDAO.insertLog("DanTri", "Taking data", "running", "run save files");
            // 6. Load configs from database control.db (name, separator,... ) with name = "XXX"
            Optional<Config> configOptional = getConfigById(2);
            Optional<File> fileOptional = getFileById(2);
            // 6.1 Check if load configs error or false
            if (configOptional.isPresent() && fileOptional.isPresent()) {
                Config config = configOptional.get();
                File file = fileOptional.get();
                BASE_URL = config.getSource_path_varchar();
//                BASE_URL = config.getSource_path_varchar();
                // 7. Insert 1 row with name, description, status" SSF" in table controls
                int generatedId = ControlDAO.insertControl("DanTri", "Bắt đầu lấy dữ liệu", "SSF");
                // 8. Line 108 - VNEDataExtractor
                // 9. Extract data from RSS and save it into C:\tempSto\temporary_news_ddmmyyyy.csv in accordance with the table configs.
                // Save data to CSV
                Map<String, List<String>> newsData = new HashMap<>();
                newsData = WebScraper.scrapeNews(0, BASE_URL, newsData);
                boolean saveFile = saveToCSV(newsData, config, file);
                // 10. Check if save file success
                if (!saveFile) {
                    // 10.1 Delete 1 row in table control with code from control.db
                    boolean isSuccess = ControlDAO.deleteControlById(generatedId);
                    // 10.2 Insert 1 row in table log with status: "error", location = "saveFile"
                    LogDAO.insertLog("DanTri", "Check save file", "error", "saveFile");
                    // 10.3 send mail error
                    SendEmail.sendMail("leminhlongit@gmail.com","Data WareHouse Tin Tức","Save file error dantri.com.vn "+ new Date());
                } else {
                    // 11. update 1 row in table control with status = "SFS"
                    boolean isSuccess = ControlDAO.updateControlStatusById(generatedId, "SFS");
                    // 12. Insert 1 row in table log with status: "success", location = "saveFile"
                    LogDAO.insertLog("DanTri", "Check save file", "success", "saveFile");
                    // 13. Send mail success
                    SendEmail.sendMail("leminhlongit@gmail.com","Data WareHouse Tin Tức","Save file success dantri.com.vn "+ new Date());
                }
            } else {
                // 6.2 Insert 1 row in table log with status: "error", location = "load config"
                LogDAO.insertLog("DanTri", "Check load config", "error", "load config");
                // 6.3 send mail error
                SendEmail.sendMail("leminhlongit@gmail.com","Data WareHouse Tin Tức","Save file error dantri.com.vn "+ new Date());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        runSource();
    }
}
