package model;

import com.opencsv.CSVWriter;
import dao.ControlDAO;
import dao.LogDAO;
import entity.Config;
import entity.Control;
import entity.File;
import entity.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static dao.ControlDAO.*;
import static dao.LogDAO.*;

public class VNEDataExtractor {

    private static String BASE_URL;
    private static String previousPageContent = null;
    private SimpleDateFormat dateFormat;

    public static void getDataFromVNELink(String url, CSVWriter csvWriter) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String strDate = doc.select("span.date").text();
        String year = "";
        if (strDate.contains(",")) {
            String[] dateParts = strDate.split(",");
            if (dateParts.length > 1) {
                String[] yearParts = dateParts[1].split("/");
                if (yearParts.length > 2) {
                    year = yearParts[2];
                }
            }
        }

        String id = "";
        String[] idParts = url.split("-");
        if (idParts.length > 1) {
            String[] idNumberParts = idParts[idParts.length - 1].split(".html");
            if (idNumberParts.length > 0) {
                id = idNumberParts[0];
            }
        }

        String strTitle = doc.select("h1.title-detail").text();
        String description = doc.select("meta[name=description]").attr("content");
        String imageUrl = doc.select("meta[property=og:image]").attr("content");
        String author = doc.select("p.author_mail strong").text();
        if (author.isEmpty()) {
            Elements backupAuthorElements = doc.select("p.Normal strong");

            if (!backupAuthorElements.isEmpty()) {
                author = backupAuthorElements.first().text();
            }
        }

        Elements categoryElements = doc.select(".breadcrumb a[data-medium]");
        String category = "";
        if (!categoryElements.isEmpty()) {
            category = categoryElements.last().text();
        }
        String publibdate = doc.select(".date").text();
        Elements contentElements = doc.select("p.Normal");
        contentElements.remove(contentElements.last());
        String content = contentElements.toString().replaceAll("<.*?>", "");
        String[] record = {id, strTitle, content, description, url, publibdate, imageUrl, author, category, BASE_URL};
        csvWriter.writeNext(record);
    }

    public static boolean getDataFromVNE(String url, CSVWriter csvWriter) throws IOException {
        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            return false;
        }
        String currentContent = doc.toString();
        if (previousPageContent != null && currentContent.equals(previousPageContent)) {
            return false;
        }
        previousPageContent = currentContent;
        Elements titleElements = doc.select("h3.title-news");
        if (titleElements.isEmpty()) {
            return false;
        }
        for (Element a : titleElements) {
            String link = a.select("a").attr("href");
            getDataFromVNELink(link, csvWriter);
        }
        return true;
    }

    public boolean getAllDataSaveCsvFile(Config config, File file) {
        try {
//          8. Move yesterday file to ~\fileArchive\temporary_news_ddmmyyyy.csv
            movePreviousDayFile(file, file.getDir_achive());
            String dateFormatValue = file.getDate_format();
            String fileNameValue = file.getFile_name();
            String fileFormatValue = file.getFile_format();
            String columNameValue = file.getColum_name();

            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatValue);
            String currentDate = dateFormat.format(new Date());
            String csvFileName = fileNameValue + currentDate + "." + fileFormatValue;

            try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file.getDir_save()+"/"+csvFileName))) {
                String[] header = {columNameValue};
                csvWriter.writeNext(header);
                int page = 1;
                boolean dataAvailable = true;
                while (dataAvailable) {
                    String url = BASE_URL + (page > 1 ? "-p" + page : "");
                    dataAvailable = getDataFromVNE(url, csvWriter);
                    page++;
                }

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void movePreviousDayFile(File file, String archiveDirectoryPath) {
        try {
            String dateFormatValue = file.getDate_format();
            String fileNameValue = file.getFile_name();
            String fileFormatValue = file.getFile_format();
            String fileDirSto = file.getDir_save();

            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatValue);
            String yesterday = dateFormat.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
            String previousDayFileName = fileNameValue + yesterday + "." + fileFormatValue;

            java.io.File previousDayFile = new java.io.File(fileDirSto+"/"+previousDayFileName);
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


    public static void main(String[] args) throws IOException {
//      1.Line 12 DBContext.
//      2.Line 41 DBContext.
//      3. Check create_at = nowDate() and status = "running" in table logs limit 1 (runningLog.isPresent())
        if (isLastLogStatusRunning()) {
//      3.1 Insert 1 row in table log with status: "cannot run", location = "start save files"
            LogDAO.insertLog("VnExpress", "Data retrieved", "cannot run", "start save files");
            return;
        }
//      4. Check create_at = nowDate() and status = "SFS" in table controls limit 1 (checkRecord.isPresent())
        Optional<Control> checkRecord = checkRecord();
        if (checkRecord.isPresent()) {
//      4.1 Insert 1 row in table log with status: "already exist", location = "find files"
            LogDAO.insertLog("VnExpress", "Data retrieved", "already exist", "find files");
        } else {
//      5. Insert 1 row with name, event_type, location status ="running" in table logs
            LogDAO.insertLog("VnExpress", "Data retrieved", "running", "run save files");
//      6. Load configs form database control.db( name, separator,... ) with name = "XXX"
            Optional<Config> configOptional = getConfigById(1);
            Optional<File> fileOptional = getFileById(1);
//      6.1 Check if load configs error or faise
            if (configOptional.isPresent() && fileOptional.isPresent()) {
                Config config = configOptional.get();
                File file = fileOptional.get();
                BASE_URL = config.getSource_path_varchar();
//      7. Insert 1 row with name, description, status" SSF" in table controls
                int generatedId = ControlDAO.insertControl("VnExpress", "Bắt đầu lấy dữ liệu", "SSF");
//      8. Line 108 - VNEDataExtractor
//      9. Extract data from RSS and save it into C:\tempSto\temporary_news_ddmmyyyy.csv in accordance with the table configs.
                VNEDataExtractor extractor = new VNEDataExtractor();
                boolean saveFile = extractor.getAllDataSaveCsvFile(config, file);
//      10. Check if save file success
                if (!saveFile) {
//      10.1 Delete 1 row in table control with code from control.db
                    boolean isSuccess = ControlDAO.deleteControlById(generatedId);
//      10.2 Insert 1 row in table log with status: " error", location = "saveFile"
                    LogDAO.insertLog("VnExpress", "Check save file", "error", "saveFile");
                } else {
//      11. update 1  row in table control with status = "SFS"
                    boolean isSuccess = ControlDAO.updateControlStatusById(generatedId, "SFS");
//      12. Insert 1 row in table log with status: " success", location = "saveFile"
                    LogDAO.insertLog("VnExpress", "Check save file", "success", "saveFile");
                }
            } else {
//      6.2 Insert 1 row in table log with status: " error", location = "load config"
                LogDAO.insertLog("VnExpress", "Check load config", "error", "load config");
            }
        }
    }
}
