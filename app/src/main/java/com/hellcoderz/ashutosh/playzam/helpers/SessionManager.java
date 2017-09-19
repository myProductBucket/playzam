package com.hellcoderz.ashutosh.playzam.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sid on 6/8/2015.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_FB_USERID = "fbuserid";
    private static final String KEY_GPLUS_USERID = "gplusUserid";
    private static final String KEY_GPLUS_EMAIL = "gplusEmail";
    private static final String KEY_GPLUS_PHOTO = "gplusThumb";
    private static final String KEY_GPLUS_PROFILEURI = "gplusProfile";
    private static final String KEY_GPLUS_NAME = "gplusPersonName";
    private static final String KEY_YOUTUBE_PLAYLISTID = "ytPlaylistId";
    private static final String KEY_LAST_UPDATE = "lastupdate";

    public SessionManager(Context context) {
        this._context = context;
    }

    public void setPref(String Pref) {
        pref = _context.getSharedPreferences(Pref, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setPlaylistId(String playlistId){
        editor.putString(KEY_YOUTUBE_PLAYLISTID,playlistId);
    }

    public void setLastUpdate(String updateTime){
        editor.putString(KEY_LAST_UPDATE,updateTime);
    }

    public String getPlaylistId(){
        return pref.getString(KEY_YOUTUBE_PLAYLISTID,"");
    }

    public void setUser(String userid,String email, String photo, String profileuri, String name){
        editor.putString(KEY_GPLUS_USERID,userid);
        editor.putString(KEY_GPLUS_EMAIL,email);
        editor.putString(KEY_GPLUS_PHOTO,photo);
        editor.putString(KEY_GPLUS_PROFILEURI,profileuri);
        editor.putString(KEY_GPLUS_NAME,name);
    }

    public void setFbUser(String userid){
        editor.putString(KEY_FB_USERID,userid);
    }

    public String getUserid(){
        return pref.getString(KEY_GPLUS_USERID,"");
    }
    public String getFbUserid(){
        return pref.getString(KEY_FB_USERID,"");
    }

    public String getEmail(){
        return pref.getString(KEY_GPLUS_EMAIL,"");
    }

    public String getUserProfilepic() {return pref.getString(KEY_GPLUS_PHOTO,"");}

    public String getUserName() {return pref.getString(KEY_GPLUS_NAME,"");}

    public String getUserProfileurl() {return pref.getString(KEY_GPLUS_PROFILEURI,"");}

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
    }

    public void commit(){
        editor.apply();
    }

    public String getLastUpdate(){
        return pref.getString(KEY_LAST_UPDATE,"");
    }

    public void logoutAll() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
