package com.example;

public class SearchWebSite {
    private String name;
    private String query;
    private String xpath;
    private String url;

    public SearchWebSite(String name, String query, String xpath, String url) {
        this.name = name;
        this.query = query;
        this.xpath = xpath;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return query;
    }

    public String getXpath() {
        return xpath;
    }

    public String getUrl() {
        return url;
    }
}

