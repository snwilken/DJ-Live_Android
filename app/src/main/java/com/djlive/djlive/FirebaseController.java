package com.djlive.djlive;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A base class that provides basic functionality in communicating with Firebase. This
 * class must be extended.
 */
public abstract class FirebaseController {
    private FirebaseDatabase database;

    /** Root of the entire database. No child should have access to this so it's private */
    private DatabaseReference refRoot;

    /** Children will have access to the host's node in Firebase */
    protected DatabaseReference refHost;

    /** Children will have access to the upcoming songs in the playlist*/
    protected DatabaseReference refQueue;

    /** Children will have access to the users joined in the playlist*/
    protected DatabaseReference refUsers;

    /**Children will have access to the settings in the playlist*/
    protected DatabaseReference refSettings;

    /**Children will have access to the played songs in the playlist*/
    protected DatabaseReference refPlayed;

    /**Children will have access to the currently playing song in the playlist*/
    protected DatabaseReference refCurrentlyPlaying;

    /** The Spotify username of the host. */
    private String hostUsername;

    /** Default constructor. Shouldn't be used in application code */
    public FirebaseController() {
        hostUsername = null;
        init();
    }

    /** Main constructor that sets up references to the database endpoints
     * @param hostUsername The Spotify username of the host
     */
    public FirebaseController(String hostUsername) {
        this.hostUsername = hostUsername;
        init();
    }

    /** Initialization for the Firebase references */
    private void init() {
        database = FirebaseDatabase.getInstance();
        refRoot = database.getReference();

        updateReferences();
    }

    public void setHostUsername(String newHostUsername) {
        this.hostUsername = newHostUsername;

        updateReferences();
    }

    private void updateReferences() {
        refHost = refRoot.child(this.hostUsername);
        refQueue = refHost.child("queue");
        refUsers = refHost.child("users");
        refSettings = refHost.child("settings");
        refPlayed = refHost.child("played");
        refCurrentlyPlaying = refHost.child("currentlyPlaying");
    }
}
