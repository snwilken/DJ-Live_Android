package com.djlive.djlive;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

public class LocalQueue {
    private ArrayList<Song> localQueue;
    private ArrayList<LocalQueueStatusListener> callbacks;
    private Settings playlistSettings;

    //result code to let the caller know if the song was added or not and if not why
    public enum Result {LOCAL_QUEUE_DUPLICATE, SETTINGS_SONG_TOO_LONG,
        SETTINGS_EXPLICIT_FILTER, SETTINGS_SUCCESS, NULL_SONG}

    //allows for another class to listen to when songs are added and removed from the local queue
    public interface LocalQueueStatusListener {
        void onSongAdded(Song song);
        void onSongAddedFailure(LocalQueue.Result result);
        void onSongRemoved(Song song);
    }

    LocalQueue() {
        localQueue = new ArrayList<>();
        callbacks = new ArrayList<>();
    }

    /**
     * Gets the item at the top of the list. Does NOT remove the item from the list.
     * @return The song at the first position in the list. null if the list is empty.
     */
    Song getSongAt(int index) {
        return localQueue.size() - 1 < index ? null : localQueue.get(index);
    }

    /**
     * Removes the song with the given URI from the local queue. If the song doesn't exist with that
     * URI it will return null.
     * @param uri The Spotify URI of the song
     * @return The song that was removed from the list. null if the song didn't exist.
     */
    Song removeSong(String uri) {
        for(Song s : localQueue) {

            if(s.getURI().equals(uri)) {
                //actually remove the song...
                localQueue.remove(s);

                //then notify the callbacks
                for(LocalQueueStatusListener callback : callbacks) {
                    callback.onSongRemoved(s);
                }
                return s;
            }
        }
        return null;
    }

    /**
     *
     * @param song The song to be added to the users localQueue
     * @return Whether or not this song was added to the localQueue
     */
    Result addSong(Song song) {
        //make sure the song passes the settings and return if it doesn't
        Result result = validateSong(song);
        if (result != Result.SETTINGS_SUCCESS) {
            return result;
        }

        //see if the song exists already (check the unique spotify URI). If so, return duplicate
        //TODO: Override the toString method of Song class to just return URI?
        for(Song s : localQueue) {

            if(s.getURI().equals(song.getURI())) {

                //notify the callbacks
                for (LocalQueueStatusListener callback : callbacks) {
                    callback.onSongAddedFailure(Result.LOCAL_QUEUE_DUPLICATE);
                }

                return Result.LOCAL_QUEUE_DUPLICATE;
            }
        }

        //it doesn't exist so add the song and return success
        localQueue.add(song);

        for(LocalQueueStatusListener callback : callbacks) {
            callback.onSongAdded(song);
        }

        return Result.SETTINGS_SUCCESS;
    }

    void registerLocalQueueStatusListener(LocalQueueStatusListener callback) {
        this.callbacks.add(callback);
    }

    public void unregisterLocalQueueStatusListener(LocalQueueStatusListener callback) {
        this.callbacks.remove(callback);
    }

    ArrayList<Song> getLocalQueue(){
        return this.localQueue;
    }

    public void setSettings(Settings settings) {
        this.playlistSettings = settings;
    }

    private Result validateSong(Song song) {
        if(song == null) {
            return Result.NULL_SONG;
        }
        if(!playlistSettings.getAllowExplicit() && song.getIsExplicit()) {
            return Result.SETTINGS_EXPLICIT_FILTER;
        }
        if(song.getDuration_ms() >  playlistSettings.getMaxLength()) {
            return Result.SETTINGS_SONG_TOO_LONG;
        }

        return Result.SETTINGS_SUCCESS;
    }

    boolean validateTrack(Track track) {

        if(track == null) {
            return false;
        }
        if(!playlistSettings.getAllowExplicit() && track.explicit) {
            return false;
        }
        if(track.duration_ms >  playlistSettings.getMaxLength()) {
            return false;
        }

        return true;
    }
}
