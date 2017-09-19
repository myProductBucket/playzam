package com.hellcoderz.ashutosh.playzam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hellcoderz.ashutosh.playzam.R;

/**
 * Created by Sid on 6/10/2015.
 */
public class AgreementFragment extends Fragment {

    View rootView;
    WebView webView;

    public AgreementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_agreement, container, false);
        final RadioGroup radioButtonGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
        webView = (WebView) rootView.findViewById(R.id.webview);
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioButtonGroup.findViewById(radioButtonID);
        final int idx = radioButtonGroup.indexOfChild(radioButton);
        switch (idx){
            case 0: loadTOS(); break;
            case 1: loadPriacyPolicy(); break;
            default: break;
        }
        radioButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton) {
                    loadTOS();
                } else if(checkedId == R.id.radioButton2) {
                    loadPriacyPolicy();
                }
            }
        });
        return rootView;
    }

    private void loadTOS(){
        webView.loadUrl("file:///android_asset/terms.html");
    }

    private void loadPriacyPolicy(){
        webView.loadUrl("file:///android_asset/privacy.html");
    }
}
