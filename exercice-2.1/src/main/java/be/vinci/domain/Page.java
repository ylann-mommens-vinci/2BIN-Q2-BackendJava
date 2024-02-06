package be.vinci.domain;

import java.util.Arrays;

public class Page {
    private int id;
    private String title;
    private String url;
    private String content;
    private String author;
    private final static String[] STATUS_PUBLICATION_POSSIBLE = {"hidden", "published"};
    private String statusPublication;
    private int authorId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatusPublication() {
        return statusPublication;
    }

    public void setStatusPublication(String publicationStatus) {
        statusPublication = Arrays.stream(STATUS_PUBLICATION_POSSIBLE)
                .filter(possibleStatus -> possibleStatus.equals(publicationStatus))
                .findFirst()
                .orElse(null);
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return id == page.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Page [id=" + id
                + ", title=" + title
                + ", uri=" + url
                + ", content=" + content
                + ", publicationStatus=" + statusPublication
                + ", authorId=" + authorId
                + "]";
    }
}
