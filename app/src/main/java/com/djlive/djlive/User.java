package com.djlive.djlive;

public abstract class User {
    /**
     * The actual playlist that will be updated from Firebase that hold a clone/copy of what is
     * in Firebase under the correct playlist node
     */
    private Playlist playlist;

    /**
     * List of songs that is only specific to the users device
     */
    private LocalQueue localQueue;

    /**
     * If this User is a Host this field will be their Spotify username. If this User is a Guest
     * this field will be the name they specified in the GuestSetupActivity
     */
    private String username;

    /**
     * Constructor for a User object
     */
    public User() {
        this.username = null;
        localQueue = new LocalQueue();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public LocalQueue getLocalQueue() {
        return localQueue;
    }

    /**
     * Helper function that returns true if user is a host
     * @return true if user is a host
     */
    public boolean isUserHost() {
        return (this instanceof Host);
    }

    /**
    * Helper function that returns true if user is a guest
    * @return true if user is a guest
    */
    public boolean isUserGuest() {
        return !isUserHost();
    }

    /**
    * Helper function that determines if user is a goat
    * @return false
    **/
     public boolean isUserGoat() {
        return username != null && username.contains("goat");
    }
}
