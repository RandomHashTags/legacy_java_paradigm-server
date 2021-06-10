package me.randomhashtags.worldlaws;

public final class NewsArticle {

    private String author, title, description, url, urlToImage;

    public NewsArticle(String author, String title, String description, String url, String urlToImage) {
        this.author = author;
        title = LocalServer.fixEscapeValues(title);
        final String articleSite = title.split(" - ")[1];
        this.title = title.split(" - " + articleSite)[0];
        this.description = LocalServer.fixEscapeValues(description);
        this.url = url;
        this.urlToImage = urlToImage;
    }

    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getURL() {
        return url;
    }
    private String getURLToImage() {
        return urlToImage;
    }

    @Override
    public String toString() {
        return "{" +
                "\"author\":\"" + author + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"url\":\"" + url + "\"," +
                "\"urlToImage\":\"" + urlToImage + "\"" +
                "}";
    }
}