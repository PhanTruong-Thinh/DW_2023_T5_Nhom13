package entity;

public class Staging {
    private String id;
    private String title;
    private String content;
    private String description;
    private String url;
    private String date;
    private String image;
    private String author;
    private String category;
    private String source;

    public Staging(String id, String title, String content, String description, String url, String date, String image, String author, String category, String source) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.description = description;
        this.url = url;
        this.date = date;
        this.image = image;
        this.author = author;
        this.category = category;
        this.source = source;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    @Override
    public String toString() {
        return "Staging{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", image='" + image + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
