package com.hellcoderz.ashutosh.playzam;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.widget.Toast;

import com.hellcoderz.ashutosh.playzam.fragments.AgreementFragment;
import com.hellcoderz.ashutosh.playzam.fragments.FragmentDrawer;
import com.hellcoderz.ashutosh.playzam.fragments.HomeFragment;
import com.hellcoderz.ashutosh.playzam.fragments.PremiumFragment;
import com.hellcoderz.ashutosh.playzam.fragments.TrendFragment;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SessionManager;
import com.hellcoderz.ashutosh.playzam.helpers.UrlHelper;


/**
 * Created by Sid on 6/9/2015.
 */
public class TestActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {
    private String TAG = "Playzam Songlistactivity";

    private Toolbar mToolbar;

    SessionManager sessionManager;
    private FragmentDrawer drawerFragment;
    ActionBarDrawerToggle mDrawerToggle;
    Fragment fragment = null;
    Fragment homefragment = new HomeFragment();
    Fragment trendFragment = new TrendFragment();
    OnVolumeChangeListener mVolumeChangeListener;
    private AudioManager audioManager = null;

    public interface OnVolumeChangeListener {
        public void onVolumeChanged(int direction);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        sessionManager = new SessionManager(getApplicationContext());

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            mVolumeChangeListener = (OnVolumeChangeListener) homefragment;
        } catch (ClassCastException e) {
            throw new ClassCastException("Homefrag must implement VolumeChangeListener");
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.app_name,
                R.string.app_name);
        drawerFragment.setDrawerListener(this);
        displayView(0);


    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE,0);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER,0);
                //    mVolumeChangeListener.onVolumeChanged(2);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void displayView(int position) {

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = homefragment;
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = trendFragment;
                title = getString(R.string.title_home);
                break;
            case 2:
                fragment = new PremiumFragment();
                title = "Premium";
                break;
            case 3:
                fragment = new AgreementFragment();
                title = getString(R.string.title_friends);
                break;
            case 4:
                sendEmail();
                break;
            case 5:
                showVersionInfo();
                break;
            case 6:
                logoutUser();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }

    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure about it?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clearSession();
                        clearCookies();
                        Intent intent = new Intent(TestActivity.this,SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void clearCookies() {
        CookieSyncManager.createInstance(this);
        if(Build.VERSION.SDK_INT >= 21){
            CookieManager.getInstance().removeAllCookies(null);
        }else{
            CookieManager.getInstance().removeAllCookie();
        }
    }

    private void clearSession() {
        sessionManager.setPref(Constants.PREF_FB);
        sessionManager.logoutAll();
        sessionManager.setPref(Constants.PREF_SHAZAM);
        sessionManager.logoutAll();
    }

    private void showVersionInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PlayZam info");
        builder.setMessage("Version 1.1.0")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendEmail()
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@playzam.zendesk.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Playzam Android support request");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(TestActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
