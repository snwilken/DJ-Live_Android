package com.djlive.djlive;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

//TODO: find some way to get a context in this class so we can get string resources
//TODO: check the timestamp. This means we have to save the timestamp whenever they connect and make sure
//if it's the same host that the timestamp is the same (only if we automatically connect them)


public class LoginController extends FirebaseController {
    private final String TAG = "LOGIN";

    public enum Result {LOGIN_SUCCESS, LOGIN_NO_SUCH_HOST, LOGIN_INVALID_TIMESTAMP,
        LOGIN_PLAYLIST_FULL, LOGIN_INCORRECT_PIN, LOGIN_DUPLICATE_NAME}

    public LoginController(String hostUsername) {
        super(hostUsername);
    }

    public interface LoginStatusCallback {
        void onLoginStart();
        void onLoginSuccess(Settings settings);
        void onLoginFailure(LoginController.Result result);
        void onLoginCancelled(DatabaseError databaseError);
    }

    //eventually change this to return a Result enum?
    public void joinPlaylist(final String guestUsername, final int pin, final LoginStatusCallback callback) {
        if(refHost == null) {
            Log.d(TAG, "refHost is NULL");
            return;
        }

        if(callback == null) {
            Log.d(TAG, "callback not set");
            return;
        }

        callback.onLoginStart();

        refHost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //grab the settings from the playlist
                    Settings settings = dataSnapshot.child("settings").getValue(Settings.class);

                    DataSnapshot usersSnapshot = dataSnapshot.child("users");

                    //get current number of users
                    long numUsers = usersSnapshot.getChildrenCount();

                    //return a failure if your addition to the playlist would result in the
                    if(numUsers + 1 > settings.getMaxGuests()) {
                        callback.onLoginFailure(Result.LOGIN_PLAYLIST_FULL);
                        return;
                    }

                    //validate the pin is correct
                    if(settings.getPin() != pin) {
                        callback.onLoginFailure(Result.LOGIN_INCORRECT_PIN);
                        return;
                    }

                    //make sure the name is unique in the playlist
                    for(DataSnapshot usernameSnapshot : usersSnapshot.getChildren()) {
                        String username = usernameSnapshot.getValue(String.class);
                        if(username.equals(guestUsername)) {
                            callback.onLoginFailure(Result.LOGIN_DUPLICATE_NAME);
                            return;
                        }
                    }

                    //add them to the playlist
                    String key = refHost.child("users").push().getKey();
                    refHost.child("users").child(key).setValue(guestUsername);

                    callback.onLoginSuccess(settings);
                } else {
                    callback.onLoginFailure(Result.LOGIN_NO_SUCH_HOST);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onLoginCancelled(databaseError);
            }
        });
    }

    public void setHostUsername(String newHostUsername) {
        super.setHostUsername(newHostUsername);
    }
}
