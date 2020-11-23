package com.example.samo.aropen.Class;

public class Artist {

    private String id;
    private String firstname;
    private String lastname;
    private String exibition;
    private String artwork;
    private String text;
    private String link;


    public Artist() {

    }

    public Artist(String id, String firstname, String lastname) {//, String exibition, String artwork){
        this.firstname = firstname;
        this.lastname = lastname;
        this.exibition = exibition;
        this.artwork = artwork;
        this.text = text;
        this.link = link;
        this.id = id;
    }

    public String getLink() {

        return link;
    }

    public void setLink(String link) {

        this.link = link;
    }

    public String getFirstname() {

        return firstname;
    }

    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }

    public String getLastname() {

        return lastname;
    }

    public void setLastname(String lastname) {

        this.lastname = lastname;
    }

    public String getExibition() {

        return exibition;
    }

    public void setExibition(String exibition) {

        this.exibition = exibition;
    }

    public String getArtwork() {

        return artwork;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {

        this.text = text;
    }

    public void setArtwork(String artwork) {

        this.artwork = artwork;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }
}
