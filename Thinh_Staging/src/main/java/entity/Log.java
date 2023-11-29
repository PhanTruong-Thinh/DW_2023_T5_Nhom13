package entity;

import java.sql.Timestamp;

public class Log {
    private int id;
    private Timestamp timestamp;
    private String eventType;
    private String status;
    private String location;
    private Timestamp createAt;
    private Timestamp updateAt;

    // Constructors, getters, and setters

    public Log() {
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", eventType='" + eventType + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }

    public Log(int id, Timestamp timestamp, String eventType, String status, String location, Timestamp createAt, Timestamp updateAt) {
        this.id = id;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.status = status;
        this.location = location;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    // Other methods, if needed
}

