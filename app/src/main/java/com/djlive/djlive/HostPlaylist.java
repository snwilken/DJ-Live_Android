package com.djlive.djlive;

import android.util.Log;
import static com.djlive.djlive.DJLiveActivity.player;

import java.util.ArrayList;
import java.util.LinkedList;

public class HostPlaylist extends Playlist  {

    private static final String TAG = "HOSTPLAYLIST";

    public HostPlaylist() {
        super();
    }

    /**
     * This method is called from the Playlist Controller. When there is only
     * one song in the playlist then it is passed to the player, otherwise
     * the song is stored in the player queue.
     * @param addedSong Song to be played next
     */
    void playSong(Song addedSong) {
        if(addedSong == null) {
            return;
        }
        Log.d(TAG, "Song Added to Player " + addedSong.toString());
        player.playUri(null, addedSong.getURI(), 0, 0);
    }
}
