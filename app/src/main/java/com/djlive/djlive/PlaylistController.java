package com.djlive.djlive;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.djlive.djlive.DJLiveActivity.user;
import static com.djlive.djlive.DJLiveActivity.userLocalQueueAdapter;

/**
 * The playlist controller adds and removes data from firebase and passes songs to the
 * player in the Playlist class
 * @see <a href=https://github.com/spotify/android-sdk>Spotify Android SDK</a>
 * **/
public abstract class PlaylistController extends FirebaseController implements LocalQueue.LocalQueueStatusListener
{
    //keeps a reference to the number of songs this person has added to the "queue" node
    private static final String TAG = PlaylistController.class.getSimpleName();
    private int localUserSongsAdded;
    private UIUpdateListener uiUpdateListener;

    PlaylistController(String hostUsername) {
        super(hostUsername);
        this.localUserSongsAdded = 0;
    }

    public void setupRefListeners() {
        refPlayed.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Song removedSong = dataSnapshot.getValue(Song.class);

                if(removedSong.getAddedBy().equals(user.getUsername())) {
                    localUserSongsAdded--;
                    if(user.getLocalQueue().removeSong(removedSong.getURI()) != null) {
                        userLocalQueueAdapter.notifyDataSetChanged();
                    }
                    int index = user.getPlaylist().getSettings().getSongsPerUser()-1;
                    addSong(user.getLocalQueue().getSongAt(index));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set up listeners when a child (song) is pushed to the queue node
        refQueue.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Song addedSong = dataSnapshot.getValue(Song.class);

                //pushes added pushkey to the end of the list
                user.getPlaylist().addSong(addedSong);

                //check if we were the ones to add this song
                if (addedSong.getAddedBy().equals(user.getUsername())) {
                    localUserSongsAdded++;
                }

                //passes the songs to the playlist to be played by the dj player
                if (user.isUserHost() && user.getPlaylist().size() == 1) {

                    Log.d(TAG, "Passes song to be played");
                    HostPlaylist hostPlaylist = (HostPlaylist) user.getPlaylist();
                    hostPlaylist.playSong(addedSong);
                }

                if(uiUpdateListener != null) {
                    uiUpdateListener.updateAlbumUI();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "Changed: " + dataSnapshot.getValue(Song.class).toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO: make sure the song we are removing is this same song
                Song removedSong = dataSnapshot.getValue(Song.class);

                //TODO: this only removes the first song. Fix if implementing delete song
                user.getPlaylist().removeSong();

                //passes the songs to the playlist to be played by the dj player
                if (user.isUserHost()) {
                    HostPlaylist hostPlaylist = (HostPlaylist) user.getPlaylist();
                    hostPlaylist.playSong(user.getPlaylist().getFirstSong());
                }

                if(uiUpdateListener != null) {
                    uiUpdateListener.updateAlbumUI();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    void addSong(Song song) {
        if(song == null) {
            return;
        }

        //just push it to Firebase. By this point the song has been validated and we know the
        //user won't go over the limit if they add this song
        String key = refQueue.push().getKey();
        song.setPushKey(key);
        refQueue.child(key).setValue(song);
    }

    public void setHostUsername(String newHostUsername) {
        super.setHostUsername(newHostUsername);
    }

    void initInFirebase(HostPlaylist hostPlaylist, String hostUsername) {
        //TODO: make sure we are authenticated with Firebase?

        refSettings.setValue(hostPlaylist.getSettings());

        //push the host to the users node!
        String key = refUsers.push().getKey();
        refUsers.child(key).setValue(hostUsername);
    }

    @Override
    public void onSongAdded(Song song) {
        if (localUserSongsAdded < user.getPlaylist().getSettings().getSongsPerUser()) {
            addSong(song);
        }
    }

    @Override
    public void onSongAddedFailure(LocalQueue.Result result) {

    }

    @Override
    public void onSongRemoved(Song song) {

    }

    /**
     * Called when the first song in the queue is skipped or finishes playing
     */
    void removeSong() {
        refQueue.orderByKey().limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //not sure how else to access the children other than through Iterable interface
                for(DataSnapshot song : dataSnapshot.getChildren()) {
                    Song songToRemove = song.getValue(Song.class);

                    //add it to the "played" node
                    refPlayed.child(song.getKey()).setValue(songToRemove);
                    //remove it from the "queue" node
                    refQueue.child(song.getKey()).removeValue();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * When rejoining the same playlist multiple times, need to reset the localUserSongsAdded
     * count.
     */
    //TODO: dont think we need this. When a guest tries to reenter they get a new PlaylistController
    //so the localUserSongsAdded will get set to zero in the new instance
    void resetCount() {
        this.localUserSongsAdded = 0;
    }

    public void setUIUpdateListener(UIUpdateListener listener) {
        uiUpdateListener = listener;
    }

}
