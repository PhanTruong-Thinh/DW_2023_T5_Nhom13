package entity;

import java.sql.Timestamp;

public class Control {
    private int id;
    private String name;
    private String description;
    private String status;
    private Timestamp createAt;
    private Timestamp updateAt;

    // Constructors, getters, and setters

    public Control() {
    }

    public Control(int id, String name, String description, String status, Timestamp createAt, Timestamp updateAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    // Getters and setters...

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "Control{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
