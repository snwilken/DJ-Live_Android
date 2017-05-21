package com.djlive.djlive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSetupActivity extends DJLiveActivity {

    private final static String TAG = UserSetupActivity.class.getSimpleName();
    private Button btnHost;
    private Button btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);

        btnHost = (Button) findViewById(R.id.btnHost);
        btnHost.setTypeface(typeface);

        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setTypeface(typeface);
    }

    public void onHostButtonClicked(View view) {
        //Since we don't know the hostUsername yet, just set the user to the correct type.
        //Once they are authenticated then we can set the playlistController and loginController
        //up with their specific username
        setUser(new Host());
        userLocalQueueAdapter = new UserLocalQueueAdapter(user.getLocalQueue());

        Intent startHostSetup = new Intent(this, PlaylistSetupActivity.class);
        startActivity(startHostSetup);

        playlistController = null;
    }

    public void onGuestButtonClicked(View view) {
        //Since we don't know the hostUsername yet, just set the user to the correct type.
        //Once they are authenticated then we can set the playlistController and loginController
        //up with their specific username
        setUser(new Guest());
        userLocalQueueAdapter = new UserLocalQueueAdapter(user.getLocalQueue());

        Intent startGuestSetup = new Intent(this, GuestSetupActivity.class);
        startActivity(startGuestSetup);

        playlistController = null;
    }
}


