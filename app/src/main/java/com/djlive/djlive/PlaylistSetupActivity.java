package com.djlive.djlive;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Handles Spotify Authentication and setting the settings for the Playlist.
 */
public class PlaylistSetupActivity extends DJLiveActivity {

    private PlaylistSettingsFragment playlistSettingsFragment;
    private Settings settings;

    private boolean loggedInToSpotify = false;

    private TextView loggedInAs;
    private Button loginBtn;
    protected Button btnBack;
    protected Button btnCreate;

    private static final String REDIRECT_URI = "authactivity://callback";
    private static final String CLIENT_ID = "078936db7b7a4ce3993fa61cfa39cbaf";
    private static final int REQUEST_CODE = 1337;
    private static final String USER_URL = "https://api.spotify.com/v1/me";
    private static final String TAG = "PLAYLIST_SETUP_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_setup);
        loggedInAs = (TextView)findViewById(R.id.loggedInAs);
        loggedInAs.setTypeface(typeface);

        loginBtn = (Button)findViewById(R.id.login);
        loginBtn.setText("Login");
        loginBtn.setTypeface(typeface);

        btnBack = (Button) findViewById(R.id.btnSettingsBack);
        btnBack.setTypeface(typeface);
        btnCreate = (Button) findViewById(R.id.btnSettingsCreate);
        btnCreate.setTypeface(typeface);

        //get the FragmentManager and FragmentTransaction to put into the FrameLayout container
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //change the fragment in the fragment container
        playlistSettingsFragment = new PlaylistSettingsFragment();
        fragmentTransaction.add(R.id.playlistSettingsContainer, playlistSettingsFragment, "PLAYLIST_SETTINGS_FRAGMENT");
        fragmentTransaction.commit();

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming", "user-read-private"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void spotifyLoginClick(View view){
        if(loggedInToSpotify){
            AuthenticationClient.clearCookies(this);
            loggedInToSpotify = false;
            // Update button and texts
            loggedInAs.setText("Log in to Spotify Premium");
            loginBtn.setText("Login");
        } else{
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d(TAG, "Spotify Auth Response Type: Valid Token!");
                    // Handle successful response
                    String accessToken = response.getAccessToken();

                    new GetUserInformation().execute(accessToken);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d(TAG, "Spotify Auth Response Type: Error, Result Code = " + resultCode);
                    // Handle error response
                    Toast.makeText(this, "Login error, you do need a Spotify " +
                            "Premium account to proceed.", Toast.LENGTH_LONG).show();
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG, "Spotify Auth Response Type: Other");
                    // Handle other cases
                    Toast.makeText(this, "Login error, you do need a Spotify " +
                            "Premium account to proceed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Makes sure the user is logged into a Spotify Premium account before proceeding. Creates a new
     * Playlist for the Host and sets the settings from the PreferenceFragment. Sends the settings to
     * Firebase and registers the playlistController as a LocalQueueStatusListener
     * @param view The Create button
     */
    public void onSettingsCreateButtonClicked(View view) {
        settings = playlistSettingsFragment.getSettings();

        Log.d(TAG, settings.getName() + "\n" +
                settings.getPin() + "\n" +
                settings.getAllowExplicit() + "\n" +
                settings.getSongsPerUser() + "\n" +
                settings.getMaxLength() + "\n" +
                settings.getMaxGuests() + "\n" +
                settings.getTimeStamp() + "\n");

        //user will never be null here (famous last words)
        if (loggedInToSpotify && playerCreated) {
            //create a new playlist for the user and set the settings that were just created
            user.setPlaylist(new HostPlaylist());
            user.getPlaylist().setSettings(settings);

            Log.d(TAG, "Playlist Created");
            //send the settings node and initialize the users node to contain the hosts name
            playlistController.initInFirebase((HostPlaylist) user.getPlaylist(), getHostUsername());

            //the localQueue needs to validate the songs can be added so send it the settings
            user.getLocalQueue().setSettings(settings);

            //make sure the playlist controller knows when stuff is added to the localQueue
            user.getLocalQueue().registerLocalQueueStatusListener(playlistController);

            //navigation
            Intent startPlaylistSessionActivity = new Intent(this, PlaylistSessionActivity.class);
            startActivity(startPlaylistSessionActivity);
            finish();
        } else {
            Toast.makeText(this, "Please log in with a Spotify Premium account.", Toast.LENGTH_LONG).show();
        }
    }


    public void onSettingsBackButtonClicked(View view) {
        //TODO: make sure this goes back to the right activity
        finish();
    }

    /**
     * Fired when Spotify user information lookup completes. This takes the JSON response and
     * parses it to find if they are a Premium member and gets their username
     * @param lookupResult lookupResult[0] = accessToken, lookupResult[1] = JSON string from Spotify
     */
    private void onLookupComplete(String[] lookupResult) {
        String accessToken = lookupResult[0];
        String jsonUserInformationString = lookupResult[1];

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonUserInformationString);

            if(!jsonObject.has("product") || !jsonObject.getString("product").equals("premium")){
                Toast.makeText(this, "Login error, you do need a Spotify " +
                        "Premium account to proceed.", Toast.LENGTH_LONG).show();
                // Log them out
                AuthenticationClient.clearCookies(this);

                loggedInToSpotify = false;
            } else{
                user.setUsername(jsonObject.getString("id"));
                setHostUsername(user.getUsername());
                ((Host) getUser()).setAuthToken(accessToken);
                initPlayer(CLIENT_ID);

                loggedInToSpotify = true;

                // update login button and text fields
                loginBtn.setText("Logout");
                loggedInAs.setText("Logged in as: " + user.getUsername());
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronous Task to get the user information from Spotify. Requires the access token
     * to get user information
     */
    private class GetUserInformation extends AsyncTask<String, String, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {
            //strings is the authToken.
            StringBuilder builder = new StringBuilder();
            String accessToken = strings[0];

            try {
                URLConnection connection = new URL(USER_URL).openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                //build a JSON string from the response
                Scanner scanner = new Scanner(connection.getInputStream());

                while(scanner.hasNext()) {
                    builder.append(scanner.next());
                }

            } catch(IOException e) {
                e.printStackTrace();
            }

            String[] result = new String[2];
            result[0] = accessToken;
            result[1] = builder.toString();
            return result;
        }

        @Override
        protected void onPostExecute(String[] userInformationString) {
            onLookupComplete(userInformationString);
        }
    }

    /**
     * This anonymous inner class represents the fragment that holds the list of settings for the playlist.
     */
    public static class PlaylistSettingsFragment extends PreferenceFragment {
        public static Settings settings;

        /**
         * An object that updates the summary field of the preference layout.
         */
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Preference preference = findPreference(key);

                //Always update the summary of the EditTextPreference
                if(preference instanceof EditTextPreference) {
                    EditTextPreference etPreference = (EditTextPreference) preference;
                    preference.setSummary(etPreference.getText());
                }

            }
        };

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.playlist_settings);

            settings = new Settings();

            setupValidations();
            initSummaries();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        }

        public Settings getSettings() {
            //get the shared preferences for this fragment
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            //get the values from the shard prefs. Second argument is the default value just in case
            String playlistName = sp.getString(getString(R.string.playlist_name), "Playlist");
            String pinString = sp.getString(getString(R.string.pin), "-1");
            boolean allowExplicit = sp.getBoolean(getString(R.string.allow_explicit), false);

            ///NOTE: All integer values (songsPerUser, maxLength, maxGuests, etc.) are stored as Strings by the XML
            String songsPerUserString = sp.getString(getString(R.string.songs_per_user), "5");
            String maxLengthString = sp.getString(getString(R.string.max_length), "5");
            String maxGuestsString = sp.getString(getString(R.string.max_guests), "10");

            //convert the strings to ints
            int songsPerUser = Integer.parseInt(songsPerUserString);
            int maxLength = Integer.parseInt(maxLengthString);
            int maxGuests = Integer.parseInt(maxGuestsString);
            int pin = Integer.parseInt(pinString);

            //Convert maxLength from minutes to milliseconds
            maxLength = maxLength * 60 * 1000;

            //set the settings object with these properties
            settings.setName(playlistName);
            settings.setPin(pin);
            settings.setAllowExplicit(allowExplicit);
            settings.setSongsPerUser(songsPerUser);
            settings.setMaxLength(maxLength);
            settings.setMaxGuests(maxGuests);

            //set the timestamp for this Settings object
            settings.setTimeStamp(System.currentTimeMillis());

            return settings;
        }

        /**
         * Gets references to all Preference objects and validates them before the values are saved to the
         * SharedPreferences file.
         */
        private void setupValidations() {
            EditTextPreference etPrefPlaylistName = (EditTextPreference) findPreference(getString(R.string.playlist_name));
            etPrefPlaylistName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    return o.toString().length() != 0;
                }
            });

            //SongsPerUser >0 and <3 digits
            EditTextPreference etPrefSongsPerUser = (EditTextPreference) findPreference(getString(R.string.songs_per_user));
            etPrefSongsPerUser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    return o.toString().length() != 0 && Integer.parseInt(o.toString()) > 0;
                }
            });

            //MaxLength >0 minutes and <4 digits
            EditTextPreference etPrefMaxLength = (EditTextPreference) findPreference(getString(R.string.max_length));
            etPrefMaxLength.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override

                //TODO parse into a datetime and update other references in song
                public boolean onPreferenceChange(Preference preference, Object o) {
                    return o.toString().length() != 0 && Integer.parseInt(o.toString()) > 0;
                }
            });

            //MaxGuests >=1 and <3 digits
            EditTextPreference etPrefMaxGuests = (EditTextPreference) findPreference(getString(R.string.max_guests));
            etPrefMaxGuests.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    return o.toString().length() != 0 && Integer.parseInt(o.toString()) >= 1;
                }
            });

            //Pin between 3 and 6 digits
            EditTextPreference etPrefPin = (EditTextPreference) findPreference(getString(R.string.pin));
            etPrefPin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    return o.toString().length() >= 3;
                }
            });
        }

        /**
         * Make sure we update the summary views for each Preference
         */
        private void initSummaries() {
            EditTextPreference etPrefPlaylistName = (EditTextPreference) findPreference(getString(R.string.playlist_name));
            etPrefPlaylistName.setSummary(etPrefPlaylistName.getText());

            EditTextPreference etPrefPin = (EditTextPreference) findPreference(getString(R.string.pin));
            etPrefPin.setSummary(etPrefPin.getText());

            EditTextPreference etPrefSongsPerUser = (EditTextPreference) findPreference(getString(R.string.songs_per_user));
            etPrefSongsPerUser.setSummary(etPrefSongsPerUser.getText());

            EditTextPreference etPrefMaxLength = (EditTextPreference) findPreference(getString(R.string.max_length));
            etPrefMaxLength.setSummary(etPrefMaxLength.getText());

            EditTextPreference etPrefMaxGuests = (EditTextPreference) findPreference(getString(R.string.max_guests));
            etPrefMaxGuests.setSummary(etPrefMaxGuests.getText());
        }

    }
}
