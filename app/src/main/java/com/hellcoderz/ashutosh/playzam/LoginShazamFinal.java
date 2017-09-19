package com.hellcoderz.ashutosh.playzam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hellcoderz.ashutosh.playzam.adapters.SliderFinalAdapter;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by Sid on 6/28/2015.
 */
public class LoginShazamFinal extends Activity {
    private static int SHAZAM_REQ_CODE = 1;
    public static String SHAZAM_REQ_KEY = "loggedin";
    Button nextBtn,loginShazamBtn;
    int[] mResources = {
            R.drawable.slider3,
            R.drawable.slider2
    };
    int currentPosition = 0;

    SliderFinalAdapter sliderFinalAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shazam_login_final);
        sliderFinalAdapter = new SliderFinalAdapter(this,mResources);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(sliderFinalAdapter);
        CirclePageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageSelected(int pos) {
                currentPosition = pos;
                if(pos == 1){
                    nextBtn.setVisibility(View.GONE);
                    loginShazamBtn.setVisibility(View.VISIBLE);
                }else{
                    nextBtn.setVisibility(View.VISIBLE);
                    loginShazamBtn.setVisibility(View.GONE);
                }
            }

        });

        nextBtn = (Button) findViewById(R.id.next);
        loginShazamBtn = (Button) findViewById(R.id.shazamcreate);

        nextBtn.setVisibility(View.VISIBLE);
        loginShazamBtn.setVisibility(View.GONE);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(currentPosition+1);
            }
        });

        loginShazamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginShazamFinal.this, ShazamWebviewActivity.class);
                startActivityForResult(i, SHAZAM_REQ_CODE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SHAZAM_REQ_CODE) {
            boolean result = false;
            if(resultCode == RESULT_OK){
                result=data.getBooleanExtra(SHAZAM_REQ_KEY,false);
                if(result) {
                    Toast.makeText(getApplicationContext(), "Logged in to shazam", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginShazamFinal.this,SyncSongsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        }
    }
}
