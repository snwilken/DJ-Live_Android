package com.djlive.djlive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Splash Screen. The background is applied through Style.xml
 * @author Summer Wilken
 */

public class SplashScreenActivity extends DJLiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, UserSetupActivity.class);
        startActivity(intent);
        finish();
    }

    public void updatePlayerView(){}
}
