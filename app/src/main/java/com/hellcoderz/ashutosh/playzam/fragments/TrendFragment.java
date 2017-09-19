package com.hellcoderz.ashutosh.playzam.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.hellcoderz.ashutosh.playzam.R;
import com.hellcoderz.ashutosh.playzam.adapters.SearchListAdapter;
import com.hellcoderz.ashutosh.playzam.adapters.TrendListAdapter;
import com.hellcoderz.ashutosh.playzam.helpers.AppController;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SearchHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SessionManager;
import com.hellcoderz.ashutosh.playzam.helpers.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sid on 8/1/2015.
 */
public class TrendFragment extends Fragment {
    View rootView;
    SessionManager sessionManager;
    private String TAG = "Playzam Songlistactivity";
    private List<String> queryVideoIds = new ArrayList<>();

    private ProgressDialog pDialog;
    private List<SearchHelper> songList = new ArrayList<>();
    private ListView listView;
    private TrendListAdapter adapter;
    UrlHelper urlHelper;
    private String mUserId;
    FileHelper fileHelper;

    public TrendFragment() {
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
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_trend, container, false);
            urlHelper = new UrlHelper();
            fileHelper = new FileHelper(getActivity());
            sessionManager = new SessionManager(getActivity());
            pDialog = new ProgressDialog(getActivity());

            listView = (ListView) rootView.findViewById(R.id.list);
            adapter = new TrendListAdapter(getActivity(), songList, this);
            listView.setAdapter(adapter);

            sessionManager.setPref(Constants.PREF_FB);
            mUserId = sessionManager.getFbUserid();

            loadTrends();
        }
        return rootView;
    }

    public void addToPlaylistClick(List<SearchHelper> searchHelpers,int position){
        SearchHelper dh = searchHelpers.get(position);
        Log.e(TAG,"Pos:"+position);
        addVideoPlaylist(dh.getVideoid(),searchHelpers,position);
    }

    private void loadTrends() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(getActivity());
        }
        pDialog.setMessage("Getting Trends...");
        pDialog.setCancelable(false);
        pDialog.show();
        //Server logic get songs youtube api
        String url = urlHelper.ENDPOINT_TREND+"?userid="+mUserId;
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    songList.clear();
                    Log.e("trend resp", response.toString());
                    for(int i=0; i<response.length();i++){
                        try {
                            JSONObject songObj = response.getJSONObject(i);
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
                        Toast.makeText(getActivity(), "Added to playlist", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Error in sync", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("PlayZam Error: "+code);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
