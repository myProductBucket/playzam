package com.hellcoderz.ashutosh.playzam.helpers;

import java.util.HashMap;

/**
 * Created by Sid on 6/7/2015.
 */
public class Constants {
    public static final String PREF_SHAZAM = "shayzamLogin";
    public static final String PREF_FB = "fbLogin";
    public static final String YOUTUBE_API_SERVER_KEY = "AIzaSyBO7Nf72BCGdZLBmUCl0bSvDtLYxJNwN6I";
    public static final String YOUTUBE_API_ANDROID_KEY = "AIzaSyAsVh3fkmZVe8ZeGFrmHe0vylA_Wr8Rqb8";
    public static final String KIIP_KEY = "7399c991e509bbf61a12d49836e365e5";
    public static final String KIIP_SECRET = "f688e45c75b0bec0b90779c42cf8b409";
    public static final String KIIP_MOMENT_ID = "your_reward";
    public static final String PREF_GPLUS = "googleLogin";
    public static final String PREF_YOUTUBE = "youtubePref";
    public static final int VIDEO_CHECKING_PLAYLIST = 0;
    public static final int VIDEO_NOTIN_PLAYLIST = 2;
    public static final int VIDEO_IN_PLAYLIST = 1;
    public static final int IMG_DEF_WIDTH = 160;
    public static final int IMG_DEF_HEIGHT = 160;
    public static final String APP_NAME = "playzam";
    public static final String SHAZAM_PACKAGE = "com.shazam.android";
    public static final String PLAYLIST_NAME = "PlayZam";
    public static final String APP_SETTING_FILE = "settings.json";
    public static final String APP_SHAZAM_PLAYLIST_FILE = "shazam.json";
    public static final String APP_PLAYZAM_PLAYLIST_FILE = "playzam.json";
    public static final String APP_IMG_PLACEHOLDER = "http://placehold.it/150x150";
    public static final String PLAYZAM_TOS_URL = "http://playzam.com";
    public static final String ENDPOINT_SHAZAM_TAGS = "https://www.shazam.com/myshazam/download-history";
    public static final String ENDPOINT_SHAZAMHOME = "http://www.shazam.com/myshazam";
    public static final String TYPEFACE_Geogrotesque_thin = "Geogrotesque/Geogrotesque-Thin_gdi.ttf";
    public static final String TYPEFACE_Geogrotesque_regular = "Geogrotesque/Geogrotesque-Regular_gdi.ttf";
    public static final String TYPEFACE_Geogrotesque_light = "Geogrotesque/Geogrotesque-Light_gdi.ttf";
    public static final String TYPEFACE_Geogrotesque_medium = "Geogrotesque/Geogtq-Md.ttf";
    public static final String PLAYLIST_DESCRIPTION = "This awesome playlist was created by http://playzam.com. Easily sync your Shazam tracks to a Youtube playlist. It's free!";

    //Error Codes
    public static final HashMap<Integer,String> PLAYZAM_ERRORS = new HashMap<>();
    public static final int PLAYZAM_ERR_LIMIT = 1;

    public Constants(){
        PLAYZAM_ERRORS.put(PLAYZAM_ERR_LIMIT,"Go premium! *Unlimited Syncs *No Ads *Low one time $1 Fee.");
    }

    public String getError(int code){
        return PLAYZAM_ERRORS.containsKey(code)?PLAYZAM_ERRORS.get(code):"";
    }
}
