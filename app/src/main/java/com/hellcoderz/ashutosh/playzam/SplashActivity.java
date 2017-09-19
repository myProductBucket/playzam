package com.hellcoderz.ashutosh.playzam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SessionManager;

/**
 * Created by Sid on 6/8/2015.
 */
public class SplashActivity extends Activity {

    SessionManager sessionManager;
    FileHelper fileHelper;

    Button syncshazamBtn, continuePlayerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        sessionManager = new SessionManager(getApplicationContext());
        fileHelper = new FileHelper(getApplicationContext());
        sessionManager.setPref(Constants.PREF_SHAZAM);

        syncshazamBtn = (Button) findViewById(R.id.syncshazam);
        continuePlayerBtn = (Button) findViewById(R.id.continueplayer);

        syncshazamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if(fileHelper.doesFileExist(Constants.APP_SETTING_FILE)){
                    //Sync songs first
                    Intent intent = new Intent(SplashActivity.this,SyncSongsActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    //Go to login shazam screen
                    Intent intent = new Intent(SplashActivity.this,LoginShazamActivity.class);
                    startActivity(intent);
                    finish();
                }
                */
                Intent intent = new Intent(SplashActivity.this,LoginShazamActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continuePlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileHelper.doesFileExist(Constants.APP_SHAZAM_PLAYLIST_FILE)){
                    //Go to playlist screen
                    Intent intent = new Intent(SplashActivity.this,TestActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        if(sessionManager.isLoggedIn()){
            //Go to login shazam screen
            Intent intent = new Intent(SplashActivity.this,TestActivity.class);
            startActivity(intent);
            finish();
        }else{
            if(fileHelper.doesFileExist(Constants.APP_SETTING_FILE)){
                continuePlayerBtn.setVisibility(View.VISIBLE);
            }
        }
    }
}
