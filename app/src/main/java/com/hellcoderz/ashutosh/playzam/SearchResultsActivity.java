package com.hellcoderz.ashutosh.playzam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.hellcoderz.ashutosh.playzam.adapters.SearchListAdapter;
import com.hellcoderz.ashutosh.playzam.helpers.AppController;
import com.hellcoderz.ashutosh.playzam.helpers.Auth;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.PlayerHelper;
import com.hellcoderz.ashutosh.playzam.helpers.PlaylistHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SearchHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SessionManager;
import com.hellcoderz.ashutosh.playzam.helpers.UrlHelper;
import com.hellcoderz.ashutosh.playzam.helpers.VideoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.HttpConnection;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sid on 6/11/2015.
 */
public class SearchResultsActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    SessionManager sessionManager;
    private String TAG = "Playzam Songlistactivity";
    private  boolean PLAYLIST_PLAYZAM_EXISTS = false;
    private String PLAYLISTID = "";
    private List<String> playzamVideoIds = new ArrayList<>();
    private List<String> queryVideoIds = new ArrayList<>();

    private ProgressDialog pDialog;
    private List<SearchHelper> songList = new ArrayList<>();
    private ListView listView;
    private SearchListAdapter adapter;
    UrlHelper urlHelper;
    private String mUserId;
    FileHelper fileHelper;
    private String intentquery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        urlHelper = new UrlHelper();
        fileHelper = new FileHelper(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView)findViewById(R.id.list);

        adapter = new SearchListAdapter(SearchResultsActivity.this, songList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(SearchResultsActivity.this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        sessionManager.setPref(Constants.PREF_FB);
        mUserId = sessionManager.getFbUserid();
        handleIntent(getIntent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog.cancel();
            pDialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void searchSong(final String songtitle) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(SearchResultsActivity.this);
        }
        pDialog.setMessage("Searching...");
        pDialog.setCancelable(false);
        pDialog.show();
        //Server logic get songs youtube api
        String url = urlHelper.ENDPOINT_SONG_SEARCH+songtitle+"?userid="+mUserId;
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    songList.clear();
                    Log.e("resp",response.toString());
                    for(int i=0; i<response.length();i++){
                        try {
                            JSONObject songObj = response.getJSONObject(i);
                            /*
                            SearchHelper sh = new SearchHelper();
                            sh.setTitle(songObj.getString("title"));
                            sh.setAddedon(songObj.getString("date"));
                            sh.setArtist(songObj.getString("artist"));
                            sh.setThumbnailUrl(songObj.getString("thumbnail"));
                            sh.setVideoid(songObj.getString("videoid"));
                            sh.setIsinplaylist(inplaylist);
                            */
                            int inplaylist = songObj.getBoolean("inplaylist") ? Constants.VIDEO_IN_PLAYLIST : Constants.VIDEO_NOTIN_PLAYLIST;
                            SearchHelper sh = new SearchHelper(
                                    songObj.getString("title"),
                                    songObj.getString("thumbnail"),
                                    songObj.getString("date"),
                                    songObj.getString("artist"),
                                    songObj.getString("videoid"),
                                    inplaylist
                            );
                            songList.add(sh);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                pDialog.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Log.e("Error", "Error: " + error.networkResponse);
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }


    public void testMethod(List<SearchHelper> searchHelpers,int position){
        SearchHelper dh = searchHelpers.get(position);
        Log.e(TAG,"Pos:"+position);
        Log.e(TAG,"Btn clicked.Videoid:"+dh.getTitle()+",playlist"+PLAYLISTID+",exist="+PLAYLIST_PLAYZAM_EXISTS);
        addVideoPlaylist(dh.getVideoid(),searchHelpers,position);
    }

    //Add selected song to playlsit as well as save on server
    public void addVideoPlaylist(final String videoId, final List<SearchHelper> searchHelpers,final int position){
        final SearchHelper ss = searchHelpers.get(position);
        final JSONObject songObj = new JSONObject();
        try {
            songObj.put("title", ss.getTitle());
            songObj.put("videoid",ss.getVideoid());
            songObj.put("artist",ss.getArtist());
            songObj.put("thumbnail",ss.getThumbnailUrl());
            songObj.put("date",ss.getDate());

            addSongServer(songObj,ss,position); //Add to server
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }
    }

    public void addSongServer(final JSONObject songObj, final SearchHelper searchHelper,final int position){

        StringRequest strReq = new StringRequest(com.android.volley.Request.Method.POST,
                urlHelper.ENDPOINT_SONG_NEW, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("updated").equals("true")) {
                        Toast.makeText(getApplicationContext(),"Added to playlist",Toast.LENGTH_LONG).show();
                        SearchHelper sh = new SearchHelper(
                                searchHelper.getTitle(),
                                searchHelper.getThumbnailUrl(),
                                searchHelper.getDate(),
                                searchHelper.getArtist(),
                                searchHelper.getVideoid(),
                                Constants.VIDEO_IN_PLAYLIST
                        );
                        songList.set(position,sh);
                        if(fileHelper.doesFileExist(Constants.APP_PLAYZAM_PLAYLIST_FILE))
                            fileHelper.UpdateFile(Constants.APP_PLAYZAM_PLAYLIST_FILE, obj.getJSONObject("song"));
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in sync", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d("Event attend", "Error: " + error.getMessage());
                showGeneralError(300,error.getMessage());
                adapter.notifyDataSetChanged();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("song", songObj.toString());
                params.put("userid", mUserId);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void showGeneralError(int code, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PlayZam Error: "+code);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intentquery = intent.getStringExtra(SearchManager.QUERY);
            searchSong(intentquery);
        }
    }
}
