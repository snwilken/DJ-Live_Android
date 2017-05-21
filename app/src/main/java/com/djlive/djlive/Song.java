package com.djlive.djlive;

public class Song {

    //abstract variables
    private String uri;
    private String artist;
    private String album;
    private String addedBy;
    private String name;
    private boolean isExplicit;
    private Long duration_ms;
    private String albumImageURL;
    private String pushKey;
    private int aggregateLikes;

    public Song() {

    }

    public Song(String uri, String artist, String album, String addedBy,
                String name, boolean isExplicit, Long duration_ms, String albumImageURL) {
        this();
        this.uri     = uri;
        this.artist  = artist;
        this.album   = album;
        this.addedBy = addedBy;
        this.name    = name;
        this.isExplicit = isExplicit;
        this.duration_ms = duration_ms;
        this.albumImageURL = albumImageURL;
    }

    String getURI() { return uri; }

    void setURI(String uri) { this.uri = uri; }

    String getArtist() { return artist; }

    void setArtist(String artist) { this.artist = artist; }

    String getAlbum() { return album; }

    void setAlbum(String album) { this.album = album; }

    String getAddedBy() { return addedBy; }

    void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    String getName() { return name; }

    void setName(String name) { this.name = name; }

    boolean getIsExplicit() { return isExplicit; }

    void setIsExplicit(boolean isExplicit) {this.isExplicit = isExplicit;}

    Long getDuration_ms() { return duration_ms; }

    void setDuration_ms(Long duration_ms) { this.duration_ms = duration_ms; }

    String getAlbumImageURL() { return albumImageURL; }

    void setAlbumImageURL(String imageURL) { this.albumImageURL = imageURL; }

    String getPushKey() { return this.pushKey; }

    void setPushKey(String pushKey) { this.pushKey = pushKey; }

    public int getAggregateLikes() { return aggregateLikes; }

    public void setAggregateLikes(int aggregateLikes) { this.aggregateLikes = aggregateLikes; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getName());
        sb.append(", ");
        sb.append(getArtist());
        sb.append(", ");
        sb.append(getURI());
        sb.append(", ");
        sb.append(getPushKey());

        return sb.toString();
    }

}
