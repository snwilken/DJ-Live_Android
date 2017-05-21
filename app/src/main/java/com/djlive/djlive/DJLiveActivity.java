package com.djlive.djlive;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.djlive.djlive.PlaylistSessionActivity;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.djlive.djlive.R.*;
import static com.spotify.sdk.android.player.PlayerEvent.kSpPlaybackNotifyNext;
import static com.spotify.sdk.android.player.PlayerEvent.kSpPlaybackNotifyPause;
import static com.spotify.sdk.android.player.PlayerEvent.kSpPlaybackNotifyPlay;
import static com.spotify.sdk.android.player.PlayerEvent.kSpPlaybackNotifyTrackDelivered;

/**
 * The DJLiveActivity is a super class that is implemented by all activities. It stores
 * global variables that all other activities can access
 * @see <a href=https://github.com/spotify/android-sdk>Spotify Android SDK</a>
 * **/
public abstract class DJLiveActivity extends AppCompatActivity implements
        ConnectionStateCallback,
        Player.NotificationCallback {
    private static final String TAG = "DJ_LIVE_ACTIVITY";

    private static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;

    protected static User user;
    protected static LoginController loginController;
    protected static PlaylistController playlistController;
    private static String hostUsername;
    protected static UserLocalQueueAdapter userLocalQueueAdapter;
    protected static Player player;
    protected static boolean playerCreated;
    protected static Metadata metadata;
    protected static Typeface typeface;
    protected static Map<Integer, String[]> backgroundImages;

    private static UIUpdateListener uiUpdateListener;


    private final Player.OperationCallback operationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "OK!");
        }

        @Override
        public void onError(Error error) {
            Log.d(TAG, "ERROR:" + error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticateWithFirebase();

        //sets up font for all activities
        setUpTypeFace();

        backgroundImages = createImages();

        Log.d(TAG, player == null ? "Player is null" : "Player is not null");
        Log.d(TAG, user   == null ? "User is null" : "User is not null");

        if (user != null) {

            Log.d(TAG, user.getPlaylist() == null ? "Playlist is null" : "Playlist is not null");
        }
        Log.d(TAG, playlistController == null ? "Playlist controller is null" :
            "Playlist controller is not null");
    }

    private void setUpTypeFace() {
        AssetManager am = this.getApplicationContext().getAssets();

        typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "Montserrat-Regular.ttf"));
    }

    @Override
    public void onStart() {
        super.onStart();
        //re-add the listener
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //remove the listener if it isn't null
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void setUser(User newUser) {
        user = newUser;
    }

    public User getUser() {
        return user;
    }

    public void setHostUsername(String newHostUsername) {
        hostUsername = newHostUsername;

        //only a Guest needs to update the Login Controller
        if(user.isUserGuest()) {
            if(loginController == null) {
                loginController = new LoginController(newHostUsername);
            }
            else {
                loginController.setHostUsername(newHostUsername);
            }
        }

        if(playlistController == null) {
            if(user.isUserGuest()) {
                playlistController = new GuestPlaylistController(newHostUsername);
            }
            else {
                playlistController = new HostPlaylistController(newHostUsername);
            }
        } else {
            playlistController.setHostUsername(newHostUsername);
            Log.d(TAG, "Playlist controller now listening to : " + newHostUsername);
            playlistController.resetCount(); //resets total number of songs added by user in the
                                             //playlist
        }

    }

    public String getHostUsername() {
        return hostUsername;
    }

    private void authenticateWithFirebase() {
        //anonymous Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                //TODO: make sure they actually signed in and are authenticated
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "logged in!");
                } else {
                    // User is signed out
                    Log.d(TAG, "logged out!");
                }
            }
        };

        firebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInAnonymously", task.getException());
                    Toast.makeText(DJLiveActivity.this, "Firebase authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initPlayer(String CLIENT_ID){
        Config playerConfig = new Config(this, ((Host)user).getAuthToken(), CLIENT_ID);
        playerCreated = false;
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                player = spotifyPlayer;
                player.addConnectionStateCallback(DJLiveActivity.this);
                player.addNotificationCallback(DJLiveActivity.this);
                playerCreated = true;
                Log.d(TAG, "Player is initialized");
            }

            @Override
            public void onError(Throwable throwable) {
                playerCreated = false;
            }
        });
    }


    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(int i) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        metadata = player.getMetadata();

        //removes songs upon completion
        if (playerEvent == kSpPlaybackNotifyTrackDelivered || playerEvent == kSpPlaybackNotifyNext) {
            playlistController.removeSong();
        }

        if(uiUpdateListener != null) {
            uiUpdateListener.updateControlsUI();
        }

    }

    @Override
    public void onPlaybackError(Error error) {

    }

    public void setUIUpdateListener(UIUpdateListener listener) {
        uiUpdateListener = listener;
    }

    private Map<Integer, String[]> createImages()
    {
        Map<Integer, String[]> map = new TreeMap<>();

        //stores drawable id for backgrounds and array of rgb int values
        map.put((R.drawable.hexc62828), new String[]{"#c62828","#b71c1c"});
        map.put((R.drawable.hexad1457), new String[]{"#AD1457","#880E4F"});
        map.put((R.drawable.hex6a1b9a), new String[]{"#6A1B9A","#4A148C"});
        map.put((R.drawable.hex4527a0), new String[]{"#4527A0","#311B92"});
        map.put((R.drawable.hex283593), new String[]{"#283593","#1A237E"});
        map.put((R.drawable.hex1565c0), new String[]{"#1565C0","#0D47A1"});
        map.put((R.drawable.hex0277bd), new String[]{"#0277BD","#01579B"});
        map.put((R.drawable.hex00838f), new String[]{"#00838F","#006064"});
        map.put((R.drawable.hex00695c), new String[]{"#00695C","#004D40"});
        map.put((R.drawable.hex2e7d32), new String[]{"#2E7D32","#1B5E20"});
        map.put((R.drawable.hex558b2f), new String[]{"#558B2F","#33691E"});
        map.put((R.drawable.hex9e9d24), new String[]{"#9E9D24","#827717"});
        map.put((R.drawable.hexf9a825), new String[]{"#F9A825","#F57F17"});
        map.put((R.drawable.hexff8f00), new String[]{"#FF8F00","#FF6F00"});
        map.put((R.drawable.hexef6c00), new String[]{"#EF6C00","#E65100"});
        map.put((R.drawable.hexd84315), new String[]{"#D84315","#BF360C"});
        map.put((R.drawable.hex4e342e), new String[]{"#4E342E","#3E2723"});
        map.put((R.drawable.hex424242), new String[]{"#424242","#212121"});
        map.put((R.drawable.hex37474f), new String[]{"#37474F","#263238"});
        map.put((R.drawable.hexe0e0e0), new String[]{"#E0E0E0","#BDBDBD"});

        return map;
    }


    /**
     * This class destroys the player object
     */
    void destroyPlayer() {
        Spotify.destroyPlayer(this);
        Log.d(TAG, player != null ? "Status : " + player.getPlaybackState() : "");
    }
}