package model;
import com.opencsv.bean.CsvBindByPosition;

public class Staging {

    @CsvBindByPosition(position = 0)
    private String id;

    @CsvBindByPosition(position = 1)
    private String title;

    @CsvBindByPosition(position = 2)
    private String content;

    @CsvBindByPosition(position = 3)
    private String description;

    @CsvBindByPosition(position = 4)
    private String link;

    @CsvBindByPosition(position = 5)
    private String dateTime;

    @CsvBindByPosition(position = 6)
    private String imageUrl;

    @CsvBindByPosition(position = 7)
    private String author;

    @CsvBindByPosition(position = 8)
    private String category;

    @CsvBindByPosition(position = 9)
    private String source;

    @Override
    public String toString() {
        return "Staging{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
// Các getter và setter
}

