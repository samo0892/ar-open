package com.example.samo.aropen.Class;

import java.util.Date;

public class Site {

    private String title;
    private String subtitle;
    private String text;
    private String adress;
    private String artworkId;
    private String city;
    private Date date;
    private double endHour;
    private double startHour;


    private String latitude;
    private String longitude;
    private double size;
    private double direction;
    private double distanceFromGround;
    private String radius;
    private String id;


    public Site(String latitude, String longitude, String id, String radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.radius = radius;
    }

    public String getArtworkId() {
        return artworkId;
    }

    public void setArtworkId(String artworkId) {
        this.artworkId = artworkId;
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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getDistanceFromGround() {
        return distanceFromGround;
    }

    public void setDistanceFromGround(double distanceFromGround) {
        this.distanceFromGround = distanceFromGround;
    }
}
