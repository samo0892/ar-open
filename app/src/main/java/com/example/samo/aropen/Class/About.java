package com.example.samo.aropen.Class;

public class About {

    private String title;
    private String subtitle;
    private String text;
    private String contact;

    public About(String contact, String title, String subtitle, String text) {
        this.title = title;
        this.subtitle = subtitle;
        this.text = text;
        this.contact = contact;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
