package com.hellcoderz.ashutosh.playzam;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.hellcoderz.ashutosh.playzam.helpers.Constants;

import me.kiip.sdk.Kiip;
import me.kiip.sdk.KiipFragmentCompat;
import me.kiip.sdk.Poptart;


/**
 * Created by Sid on 7/6/2015.
 */
public class KiipActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private final static String KIIP_TAG = "kiip_fragment_tag";
    private KiipFragmentCompat mKiipFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.kiip_activity);
        // Create or re-use KiipFragment.
        if (savedInstanceState != null) {
            mKiipFragment = (KiipFragmentCompat) getSupportFragmentManager().findFragmentByTag(KIIP_TAG);
        } else {
            mKiipFragment = new KiipFragmentCompat();
            getSupportFragmentManager().beginTransaction().add(mKiipFragment, KIIP_TAG).commit();
        }

        Kiip.getInstance().saveMoment(Constants.KIIP_MOMENT_ID, new Kiip.Callback() {

            @Override
            public void onFinished(Kiip kiip, Poptart reward) {
                if (reward == null) {
                    Log.d(KIIP_TAG, "Successful moment but no reward to give.");
                }
                else {
                    onPoptart(reward);
                }
            }

            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
                Log.e(TAG,"failed KIIP");
                exception.printStackTrace();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Kiip.getInstance().startSession(new Kiip.Callback() {
            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
                Log.e(TAG,"failed KIIP");
                exception.printStackTrace();
            }

            @Override
            public void onFinished(Kiip kiip, Poptart poptart) {
                onPoptart(poptart);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Kiip.getInstance().endSession(new Kiip.Callback() {
            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
                Log.e(TAG,"failed KIIP");
                exception.printStackTrace();
            }

            @Override
            public void onFinished(Kiip kiip, Poptart poptart) {
                onPoptart(poptart);
            }
        });
    }

    public void onPoptart(Poptart poptart) {
        mKiipFragment.showPoptart(poptart);
    }

    // Session Listeners from extending BaseFragmentActivity




}
