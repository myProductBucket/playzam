package com.hellcoderz.ashutosh.playzam;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.hellcoderz.ashutosh.playzam.helpers.AppController;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SessionManager;
import com.hellcoderz.ashutosh.playzam.helpers.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sid on 6/9/2015.
 */
public class SyncSongsActivity extends Activity {
    UrlHelper urlHelper;
    SessionManager sessionManager;
    FileHelper fileHelper;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_progress);

        urlHelper = new UrlHelper();
        fileHelper = new FileHelper(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setPref(Constants.PREF_FB);
        mUserId = sessionManager.getFbUserid();
        fetchLocalSongs();//----------------------------------------fetching local songs
    }

    public String formatDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        return formatter.format(date);
    }

    public void syncShazamPlaylist(){//-------------------------------------------------------------syncing shazam playlist
        String cookie = CookieManager.getInstance().getCookie(urlHelper.ENDPOINT_SHAZAM_TAGS);
        if (cookie != null){
            if(cookie.substring(cookie.length() - 1).equals(";")){
                cookie += ";";
            }
            cookie += " social-session=true;";
            saveFile(urlHelper.ENDPOINT_SHAZAM_TAGS,cookie);//---------------------saving ???
        }else {
            Intent intent = new Intent(SyncSongsActivity.this,LoginShazamActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void saveFile(final String url, final String cookie) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                InputStream is;
                try {
                    URL u = new URL(url);
                    Log.e("sync",cookie);
                    HttpURLConnection con = (HttpURLConnection) u.openConnection();
                    con.setRequestProperty("cookie", cookie);
                    con.setRequestMethod("GET");
//                    con.setDoOutput(true);
                    con.connect();

                    int code = con.getResponseCode();
                    if(code == 401){
                        sessionManager.setPref(Constants.PREF_SHAZAM);
                        sessionManager.setLogin(false);
                        return null;
                    }
                    is = con.getInputStream();


                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }

                    String content = total.toString();
                    parseTags(content);
                    is.close();

                }catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                sessionManager.setPref(Constants.PREF_SHAZAM);
                if(!sessionManager.isLoggedIn()){
                    Intent intent = new Intent(SyncSongsActivity.this,LoginShazamActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.execute((Void) null);
    }

    private void parseTags(String html) {//---------------------------------------------------------saving shazam playlist
        Document doc = Jsoup.parse(html);
        Elements trs = doc.select("tr");
        JSONArray jsonArray = new JSONArray();
        for( Element element : trs )
        {
            Elements tds = element.select("td");
            String title = tds.eq(0).text();
            String artist = tds.eq(1).text();
            String dater = tds.eq(2).text();
            String trackid = tds.eq(0).select("a").attr("href");

            JSONObject trackJSON = new JSONObject();
            if(title != null && !title.isEmpty()) {
                try {
                    URI uri = new URI(trackid);
                    String path = uri.getPath();
                    String idStr = path.substring(path.lastIndexOf('/') + 1);
                    trackid = idStr.substring(1);
                    trackJSON.put("trackid",trackid);
                    trackJSON.put("artist",artist);
                    trackJSON.put("date",dater);
                    trackJSON.put("title",title);
                    jsonArray.put(trackJSON);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        //Save JSON Settings
        fileHelper.StoreFile(Constants.APP_SETTING_FILE,jsonArray);
        //Also do Sync on server
        syncSongServer(jsonArray);//----------------syncing songs with server
    }

    private void fetchLocalSongs() {
        if(fileHelper.doesFileExist(Constants.APP_SETTING_FILE)){
            JSONArray responseSettings = fileHelper.readFile(Constants.APP_SETTING_FILE);
            syncSongServer(responseSettings);//------------------syncing songs with server
        }
    }

    public void syncSongServer(final JSONArray songArray) {
        StringRequest strReq = new StringRequest(com.android.volley.Request.Method.POST,
                urlHelper.ENDPOINT_SONG_SYNC, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("respSvr",songArray.toString());
                    JSONObject obj = new JSONObject(response);
                    if (obj.getBoolean("updated")) {
                        Toast.makeText(getApplicationContext(), "All data synched", Toast.LENGTH_SHORT).show();
                        fileHelper.StoreFile(Constants.APP_SHAZAM_PLAYLIST_FILE, obj.getJSONArray("shazam"));
                        fileHelper.StoreFile(Constants.APP_PLAYZAM_PLAYLIST_FILE, obj.getJSONArray("playzam"));
                        Intent intent = new Intent(SyncSongsActivity.this,TestActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in sync", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SyncSongsActivity.this,SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                sessionManager.setPref(Constants.PREF_SHAZAM);
                Date date = new Date();
                String lastupdate = formatDate(date);
                sessionManager.setLastUpdate(lastupdate);
                sessionManager.commit();
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d("Event attend", "Error: " + error.getMessage());
                Intent intent = new Intent(SyncSongsActivity.this,SplashActivity.class);
                startActivity(intent);
                finish();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
               ;
                params.put("userid", mUserId); Map<String, String> params = new HashMap<String, String>();
                params.put("songs", songArray.toString())
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq);
    }
}
