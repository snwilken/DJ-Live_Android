package com.djlive.djlive;

/**
 * Container class for all the settings associated with a single Playlist
 */
public class Settings {
    /**
     * The name the host set for the Playlist
     */
    private String name;

    /**
     * The pin/passcode set by the host to allow users to connect to the playlist
     */
    private int pin;

    /**
     * Upper limit on how many songs a User can add to the Playlist. There is no limit on songs they
     * can add to their local queue.
     */
    private int songsPerUser;

    /**
     * Max length of a song allowed in the playlist in milliseconds
     */
    private int maxLength;

    /**
     * The maximum number of people allowed to be connected to a Playlist
     */
    private int maxGuests;

    /**
     * The UNIX timestamp when this Playlist was created. We need this in case a user attempts to
     * automatically reconnect to a playlist that no longer exists but finds a playlist with the same
     * hostName.
     */
    private long timeStamp;

    /**
     * Whether or not this Playlist will allow explicit songs as determined by Spotify
     */
    private boolean allowExplicit;

    public Settings() {
        //default constructor
    }

    public Settings(int pin, String name, int songsPerUser, boolean allowExplicit, int maxLength, int maxGuests, long timeStamp) {
        this.pin = pin;
        this.name = name;
        this.songsPerUser = songsPerUser;
        this.allowExplicit = allowExplicit;
        this.maxLength = maxLength;
        this.maxGuests = maxGuests;
        this.timeStamp = timeStamp;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getSongsPerUser() {
        return songsPerUser;
    }

    public void setSongsPerUser(int songsPerUser) {
        this.songsPerUser = songsPerUser;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean getAllowExplicit() {
        return allowExplicit;
    }

    public void setAllowExplicit(boolean allowExplicit) {
        this.allowExplicit = allowExplicit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
