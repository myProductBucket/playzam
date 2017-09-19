package com.hellcoderz.ashutosh.playzam.helpers;

import android.util.Log;

/**
 * Created by Sid on 6/12/2015.
 */
public class ApiModel {
    public interface OnCustomStateListener {
        void stateChanged();
    }

    private static ApiModel mInstance;
    private OnCustomStateListener mListener;
    private boolean mState;

    private ApiModel() {}

    public static ApiModel getInstance() {
        if(mInstance == null) {
            mInstance = new ApiModel();
        }
        return mInstance;
    }

    public void setListener(OnCustomStateListener listener) {
        mListener = listener;
    }

    public void changeState(boolean state) {

        if(mListener != null) {
            Log.e("hola","not null");
            mState = state;
            notifyStateChange();
        }else {
            Log.e("hola","null");
        }
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
        mListener.stateChanged();
    }
}
