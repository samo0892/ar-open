package com.example.samo.aropen.Class;

public class Event {

    private String title;
    private String subtitle;
    private String date;
    private String text;

    public Event(String title, String subtitle, String date, String text) {
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
