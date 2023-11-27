package entity;

import java.sql.Timestamp;

public class Log {
    private int id;
    private String name;
    private String event_type;
    private String status;
    private String location;
    private Timestamp create_at;

    public Log() {
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", event_type='" + event_type + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", create_at=" + create_at +
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

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
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

    public Timestamp getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Timestamp create_at) {
        this.create_at = create_at;
    }

    public Log(int id, String name, String event_type, String status, String location, Timestamp create_at) {
        this.id = id;
        this.name = name;
        this.event_type = event_type;
        this.status = status;
        this.location = location;
        this.create_at = create_at;
    }
}