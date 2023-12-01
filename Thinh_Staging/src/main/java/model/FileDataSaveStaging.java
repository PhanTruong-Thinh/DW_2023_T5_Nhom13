package model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dao.ControlDAO;
import dao.LogDAO;
import dao.StagingDao;
import entity.Config;
import entity.Control;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import static dao.ControlDAO.checkRecord;
import static dao.LogDAO.isLastLogStatusRunning;

public class FileDataSaveStaging {
    public static void getFile(String pathDic){
        File file = new File(pathDic);
        if (file.exists() && file.isDirectory()){
            File[] listFile = file.listFiles();
            assert listFile != null;
            for (File f : listFile){
                if (f.isFile()){
                    String source = f.getAbsolutePath();
                    try {
                        FileReader fileReader = new FileReader(source);
                        CSVReader csvReader = new CSVReader(fileReader);

                        csvReader.skip(1);
                        String[] nextRecord;
//                9. lấy từng dòng dữ liệu trong file CSV
//                    12. kiểm tra xem còn dòng dữ liệu nào nửa không trong file CSV
                        while ((nextRecord = csvReader.readNext()) != null) {
//                    10. kiểm tra dữ liệu đã có trong database chưa bằng trường id
                            if (!equalsIDDataStaging(nextRecord[0])){
//                        11. insert dữ liệu vao database staging
                                    StagingDao.insertStaging(nextRecord[0],nextRecord[1],nextRecord[2],nextRecord[3],nextRecord[4],nextRecord[5],nextRecord[6],nextRecord[7],nextRecord[8],nextRecord[9]);

                            }
                        }

                        fileReader.close();

                    } catch (CsvValidationException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    Optional<Config> configOptional = ControlDAO.getConfigById(1);
                    moveFile(configOptional.get().getMoveFolder(), f);



                }
            }
        }
    }

    public static boolean equalsIDDataStaging(String id){
        List<String> listID = StagingDao.readIDsFromStaging();
        if (listID.isEmpty()){
            for (String i : listID){
                if (i.equals(id)) return true;
            }
        }
        return false;
    }

    public static void moveFile(String desPath, File file){
        // Đường dẫn của tệp tin nguồn và đích
        Path nguon = Paths.get(file.getAbsolutePath());
        Path dich = Paths.get(desPath+"\\"+file.getName());

        try {
            // Di chuyển tệp tin từ vị trí nguồn sang vị trí đích
            Files.move(nguon, dich, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Đã di chuyển tệp tin thành công!");

            // Xóa tệp tin nguồn sau khi di chuyển
            Files.delete(nguon);

            System.out.println("Đã xóa tệp tin nguồn thành công!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        // 3. Check create_at = nowDate() and status = "running" in table logs limit 1 (isLastLogStatusRunning())
        if (isLastLogStatusRunning()) {
            // 3.1 Insert 1 row in table log with eventType:Check Run , status: "cannot run", location = "start save data warehouse"
            LogDAO.insertLog("VnExpress", "Check Run", "cannot run", "start ETL");
            return;
        }
//        4. Check create_at = now() and status = "SFS" in table controls limit 1
        Optional<Control> checkRecord =checkRecord("SFS") ;
        if (checkRecord.isPresent()  ) {
//          5. Check create_at = now() and status = "SBS" in table controls limit 1
            Optional<Control> checkRecord_2 =checkRecord("SBS") ;
            if (checkRecord_2.isPresent()){
//                5.1 insert 1 row in table log with status = "already exist", location = "save database staging"
                LogDAO.insertLog("VnExpress","Check save success staging today", "already exist", "save database staging");
            }else {


//                6. insert 1 row with name, discription, code, status"running" in table control
                LogDAO.insertLog("VnExpress", "Running Data Staging", "running", "run save staging");
//                6.1 Insert 1 row with name, description, code, status" SSF" in table controls
                int generatedId = ControlDAO.insertControl("Source File", "Bắt đầu lấy dữ liệu", "SSB");


//                        7. load C:\temSto\...
                Optional<Config> configOptional = ControlDAO.getConfigById(1);

//               8.3 xóa toàn bộ dữ liệu có trong bang
                StagingDao.deleteDataStagingtable();
//              insert data into the Database Staging
                getFile(configOptional.get().getSourceFolder());

//                14 Update row in table control with status ="SBS"
                boolean isSuccess = ControlDAO.updateControlStatusById(generatedId, "SBS");
//                14.1 insert 1 row table log status="success", location = "saveDatabseStaging"
                LogDAO.insertLog("VnExpress","ETL", "success", "saveDatabaseStaging");

            }
        }


    }
}
