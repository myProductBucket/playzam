package com.hellcoderz.ashutosh.playzam;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hellcoderz.ashutosh.playzam.adapters.SliderAdapter;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by Sid on 6/8/2015.
 */
public class LoginShazamActivity extends FragmentActivity{
    private static int SHAZAM_REQ_CODE = 1;
    public static String SHAZAM_REQ_KEY = "loggedin";
    Button nextBtn,gotitBtn;
    int currentPosition = 0;
    int[] mResources = {
            R.drawable.slider1,
            R.drawable.slider2,
            R.drawable.slider3
    };
    SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shazam_login);
        sliderAdapter = new SliderAdapter(this,mResources);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(sliderAdapter);

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
                if(pos == 2){
                    nextBtn.setVisibility(View.GONE);
                    gotitBtn.setVisibility(View.VISIBLE);
                }else{
                    nextBtn.setVisibility(View.VISIBLE);
                    gotitBtn.setVisibility(View.GONE);
                }
            }

        });

        CirclePageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);

        nextBtn = (Button) findViewById(R.id.nextbtn);
        gotitBtn = (Button) findViewById(R.id.gotitbtn);

        nextBtn.setVisibility(View.VISIBLE);
        gotitBtn.setVisibility(View.GONE);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(currentPosition+1);
            }
        });

        gotitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginShazamActivity.this,LoginShazamFinal.class);
                startActivity(intent);
            }
        });
    }
}
