package com.artsovalov.theguardiannews;


public class News {

    private String mDate;
    private String mTitle;
    private String mUrl;
    private String mMiniature;

    public News(String date, String title, String url, String miniature) {
        mDate = date;
        mTitle = title;
        mMiniature = miniature;
        mUrl = url;
    }

    public String getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMiniature() {
        return mMiniature;
    }

    public String getUrl() {
        return mUrl;
    }

}
