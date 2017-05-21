package com.djlive.djlive;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

/**
 * This activity prompts the guest for information about connecting to a playlist.
 * They are asked to enter a custom name, the host's name, and the playlist passcode.
 */
public class GuestSetupActivity extends DJLiveActivity implements LoginController.LoginStatusCallback {
    private static final String TAG = GuestSetupActivity.class.getSimpleName();
    private TextInputLayout tilHostUsername, tilPasscode, tilGuestUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_setup);

        tilHostUsername = (TextInputLayout) findViewById(R.id.tilHostName);
        tilHostUsername.setTypeface(typeface);
        tilPasscode = (TextInputLayout) findViewById(R.id.tilPasscode);
        tilPasscode.setTypeface(typeface);
        tilGuestUsername = (TextInputLayout) findViewById(R.id.tilGuestUsername);
        tilGuestUsername.setTypeface(typeface);

        Button btnJoinPlaylist = (Button) findViewById(R.id.btnJoin);
        btnJoinPlaylist.setTypeface(typeface);


        //add listeners to clear the error message when the user over writes the text
        tilHostUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilHostUsername.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        tilPasscode.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilPasscode.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        tilGuestUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilGuestUsername.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    /**
     * Simple navigation to the playlist view. This is just for demonstration purposes.
     * @param view The view object that was clicked
     */
    public void onJoinPlaylistButtonClicked(View view) {
        String requiredField = "Required field";

        //ensure none of the fields are empty
        if(tilHostUsername.getEditText().getText() != null && tilHostUsername.getEditText().getText().length() <= 0) {
            tilHostUsername.setError(requiredField);
            return;
        }

        if(tilPasscode.getEditText().getText() != null && tilPasscode.getEditText().getText().length() <= 0) {
            tilPasscode.setError(requiredField);
            return;
        }

        String trimmedEnteredName = tilGuestUsername.getEditText().getText().toString().trim();
        tilGuestUsername.getEditText().setText(trimmedEnteredName);

        if(tilGuestUsername.getEditText().getText() != null && trimmedEnteredName.length() <= 0) {
            tilGuestUsername.setError(requiredField);
            return;
        }

        //Set host username. This also sets up the login controller and playlist controller
        setHostUsername(tilHostUsername.getEditText().getText().toString());
        user.setUsername(trimmedEnteredName);

        loginController.joinPlaylist(user.getUsername(), Integer.parseInt(tilPasscode.getEditText().getText().toString()), this);
    }

    @Override
    public void onLoginStart() {
        //not used
    }

    @Override
    public void onLoginSuccess(Settings settings) {
        //set the playlist to a new GuestPlaylist in the user object. Update the settings in that playlist
        user.setPlaylist(new GuestPlaylist());
        user.getPlaylist().setSettings(settings);

        //set the settings in the local queue to validate song additions
        user.getLocalQueue().setSettings(settings);

        //make sure the playlistController knows when songs get added to their local queue
        user.getLocalQueue().registerLocalQueueStatusListener(playlistController);
        Log.d(TAG, "Added a local queue status listener");

        Intent startPlaylistSessionActivity = new Intent(this, PlaylistSessionActivity.class);
        startActivity(startPlaylistSessionActivity);
        finish();
    }

    @Override
    public void onLoginFailure(LoginController.Result result) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        switch(result) {
            case LOGIN_NO_SUCH_HOST:
                tilHostUsername.setError("Host does not exist");
                tilHostUsername.startAnimation(shake);
                break;

            case LOGIN_INCORRECT_PIN:
                tilPasscode.setError("Incorrect pin");
                tilPasscode.startAnimation(shake);
                break;

            case LOGIN_DUPLICATE_NAME:
                tilGuestUsername.setError("Name already exists in this playlist");
                tilGuestUsername.startAnimation(shake);
                break;

            case LOGIN_PLAYLIST_FULL:
                Toast.makeText(this, "This playlist is full", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoginCancelled(DatabaseError databaseError) {
        Toast.makeText(this, "Database Error: " + databaseError.getCode(), Toast.LENGTH_LONG).show();
    }
}
