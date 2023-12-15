package entity;

import java.time.LocalDateTime;

public class File {
    private int id;
    private int config_id;
    private String name;
    private int row_count;
    private String colum_name;
    private String status;
    private String file_name;
    private String date_format;
    private String file_format;
    private String dir_save;
    private String dir_achive;
    private String note;
    private LocalDateTime create_at;
    private LocalDateTime update_at;
    private String create_by;
    private String update_by;

    public File() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConfig_id() {
        return config_id;
    }

    public void setConfig_id(int config_id) {
        this.config_id = config_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRow_count() {
        return row_count;
    }

    public void setRow_count(int row_count) {
        this.row_count = row_count;
    }

    public String getColum_name() {
        return colum_name;
    }

    public void setColum_name(String colum_name) {
        this.colum_name = colum_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDate_format() {
        return date_format;
    }

    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public String getFile_format() {
        return file_format;
    }

    public void setFile_format(String file_format) {
        this.file_format = file_format;
    }

    public String getDir_save() {
        return dir_save;
    }

    public void setDir_save(String dir_save) {
        this.dir_save = dir_save;
    }

    public String getDir_achive() {
        return dir_achive;
    }

    public void setDir_achive(String dir_achive) {
        this.dir_achive = dir_achive;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public LocalDateTime getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(LocalDateTime update_at) {
        this.update_at = update_at;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", config_id=" + config_id +
                ", name='" + name + '\'' +
                ", row_count=" + row_count +
                ", colum_name='" + colum_name + '\'' +
                ", status='" + status + '\'' +
                ", file_name='" + file_name + '\'' +
                ", date_format='" + date_format + '\'' +
                ", file_format='" + file_format + '\'' +
                ", dir_save='" + dir_save + '\'' +
                ", dir_achive='" + dir_achive + '\'' +
                ", note='" + note + '\'' +
                ", create_at=" + create_at +
                ", update_at=" + update_at +
                ", create_by='" + create_by + '\'' +
                ", update_by='" + update_by + '\'' +
                '}';
    }

    public File(int id, int config_id, String name, int row_count, String colum_name, String status, String file_name, String date_format, String file_format, String dir_save, String dir_achive, String note, LocalDateTime create_at, LocalDateTime update_at, String create_by, String update_by) {
        this.id = id;
        this.config_id = config_id;
        this.name = name;
        this.row_count = row_count;
        this.colum_name = colum_name;
        this.status = status;
        this.file_name = file_name;
        this.date_format = date_format;
        this.file_format = file_format;
        this.dir_save = dir_save;
        this.dir_achive = dir_achive;
        this.note = note;
        this.create_at = create_at;
        this.update_at = update_at;
        this.create_by = create_by;
        this.update_by = update_by;
    }
}
