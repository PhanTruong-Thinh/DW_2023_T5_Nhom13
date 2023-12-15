package entity;

import java.time.LocalDateTime;
import java.util.Date;

public class Config {
    private int id;
    private String name;
    private String description;
    private String source_path_varchar;
    private String location;
    private String separators;
    private String format;
    private String columns;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String createBy;

    public Config() {
        // Default constructor
    }

    public Config(int id, String name, String description, String source_path_varchar, String location, String separators, String format, String columns, LocalDateTime createAt, LocalDateTime updateAt, String createBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.source_path_varchar = source_path_varchar;
        this.location = location;
        this.separators = separators;
        this.format = format;
        this.columns = columns;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", source_path_varchar='" + source_path_varchar + '\'' +
                ", location='" + location + '\'' +
                ", separators='" + separators + '\'' +
                ", format='" + format + '\'' +
                ", columns='" + columns + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                ", createBy='" + createBy + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource_path_varchar() {
        return source_path_varchar;
    }

    public void setSource_path_varchar(String source_path_varchar) {
        this.source_path_varchar = source_path_varchar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeparators() {
        return separators;
    }

    public void setSeparators(String separators) {
        this.separators = separators;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}