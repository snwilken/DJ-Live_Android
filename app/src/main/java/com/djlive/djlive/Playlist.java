package com.djlive.djlive;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Playlist {
    private static final String TAG = Playlist.class.getSimpleName();
    protected Settings settings;
    private LinkedList<Song> playlistQueue;
    private String playlistName;

    //default constructor
    public Playlist() {
        this.playlistQueue = new LinkedList<>();
        Log.d(TAG, "Playlist Queue created");
    }


    //TODO: we may not need the songURI if we store this in the Song object this would result in duplicate data in Firebase
    void addSong(Song song) {
        this.playlistQueue.addLast(song);
    }

    void removeSong() {

        if (!playlistQueue.isEmpty()) {
            this.playlistQueue.removeFirst();
        }
    }

    LinkedList<Song> getPlaylistQueue() {
        return playlistQueue;
    }

    int size() {

        if (this.playlistQueue != null) {
            return this.playlistQueue.size();
        }

        return 0;
    }

    Song getFirstSong() {

        if (!playlistQueue.isEmpty()) {
            return playlistQueue.getFirst();
        }

        return null;
    }

    boolean isEmpty() {

        return this.playlistQueue.isEmpty();
    }

    /**
     * Sets the settings for this Playlist.
     * @param settings The settings to store with this Playlist
     */
    void setSettings(Settings settings) {
        this.settings = settings;
    }

    Settings getSettings() {
        return settings;
    }

    void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    String getPlaylistName() {
        return playlistName;
    }

}
