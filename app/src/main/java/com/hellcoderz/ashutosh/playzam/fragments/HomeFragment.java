package com.hellcoderz.ashutosh.playzam.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hellcoderz.ashutosh.playzam.R;
import com.hellcoderz.ashutosh.playzam.TestActivity;
import com.hellcoderz.ashutosh.playzam.adapters.PlaylistPlayerAdapter;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.PlayerHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeFragment extends Fragment implements TestActivity.OnVolumeChangeListener{

    private String TAG = "Playzam Player";
    private String videoID = null;

    private List<PlayerHelper> songList = new ArrayList<>();
    private ListView listView;
    private WebView webView;
    private PlaylistPlayerAdapter adapter;
    FileHelper fileHelper;
    private Toolbar mToolbar;
    JavaScriptInterface JSInterface;
    ArrayList<String> playlistArray = new ArrayList<>();
    ArrayList<String> origplaylistArray = new ArrayList<>();
    ImageButton shuffleBtn,prevBtn,pauseBtn,playBtn,nextBtn,repeatBtn;
    boolean repeat = false;
    boolean shuffle = false;
    int curposition = 0;
    View rootView;
    public int DIRECTION_UP = 1;
    public int DIRECTION_DOWN = 2;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onVolumeChanged(int direction) {
        Log.e(TAG,"Volume chaged,code:"+direction);
        if(direction == DIRECTION_UP){
            webView.loadUrl("javascript:increaseVolume()");
        }else if(direction == DIRECTION_DOWN){
            webView.loadUrl("javascript:decreaseVolume()");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null) {
            rootView = inflater.inflate(R.layout.player_activity, container, false);
            AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            fileHelper = new FileHelper(getActivity());
            shuffleBtn = (ImageButton) rootView.findViewById(R.id.shuffle);
            prevBtn = (ImageButton) rootView.findViewById(R.id.prev);
            playBtn = (ImageButton) rootView.findViewById(R.id.play);
            nextBtn = (ImageButton) rootView.findViewById(R.id.next);
            repeatBtn = (ImageButton) rootView.findViewById(R.id.repeat);
            pauseBtn = (ImageButton) rootView.findViewById(R.id.pause);
            listView = (ListView) rootView.findViewById(R.id.list);

            adapter = new PlaylistPlayerAdapter(getActivity(), songList);

            listView.setAdapter(adapter);
            listView.setItemsCanFocus(true);

            loadShazamPlaylist();
            if(songList.size() > 0){
                videoID = songList.get(0).getVideoid();
            }
            webView = (WebView) rootView.findViewById(R.id.webview);
            webView.setBackgroundColor(Color.parseColor("#ff000000"));
            webView.getSettings().setJavaScriptEnabled(true);
            JSInterface = new JavaScriptInterface(getActivity(), webView);
            webView.addJavascriptInterface(JSInterface, "JSInterface");
            webView.setWebChromeClient(new WebChromeClient() {

            });
            webView.loadUrl("http://playzam.com/demo/youtube/index.html");
        //    webView.loadUrl("javascript:PlayVideo('"+videoID+"')");
        }



        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.post(new Runnable() {
                    public void run() {
                        webView.loadUrl("javascript:PauseVideo()");
                        pauseBtn.setVisibility(View.GONE);
                        playBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.post(new Runnable() {
                    public void run() {
                        webView.loadUrl("javascript:ResumeVideo()");
                        playBtn.setVisibility(View.GONE);
                        pauseBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String color = (repeat)?"#00ffffff":"#cccccc";
                repeatBtn.setBackgroundColor(Color.parseColor(color));
                repeat = !repeat;
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = playlistArray.indexOf(videoID);
                if(position != (playlistArray.size() - 1)){
                    String videoid = playlistArray.get((position+1));
                    videoID = videoid;
                    updatePlaylistView(videoid);
                    webView.loadUrl("javascript:PlayVideo('"+videoid+"')");
                }
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = playlistArray.indexOf(videoID);
                if(position != 0){
                    String videoid = playlistArray.get((position-1));
                    videoID = videoid;
                    updatePlaylistView(videoid);
                    webView.loadUrl("javascript:PlayVideo('"+videoid+"')");
                }
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String color = (shuffle)?"#00ffffff":"#cccccc";
                shuffleBtn.setBackgroundColor(Color.parseColor(color));
                if(shuffle){
                    playlistArray = unshufflePlaylist();
                }else{
                    playlistArray = shufflePlaylist(playlistArray);
                }

                shuffle = !shuffle;
            }
        });


        int[] solutionArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG,"List item clicked: "+position);
                PlayerHelper sg = songList.get(position);
                String videoid = sg.getVideoid();
                videoID = videoid;
                updatePlaylistView(videoid);
                webView.loadUrl("javascript:PlayVideo('"+videoid+"')");
            }
        });

        listView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(),"Long click",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
        Log.e(TAG,"SaveState: Instance state Saved");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            webView.restoreState(savedInstanceState);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView.getParent() != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadShazamPlaylist();
    }

    public void resetWebview(){
        if(webView != null) {
            webView.clearCache(true);
            webView.loadUrl("about:blank");
        }
    }

    static ArrayList<String> shufflePlaylist(ArrayList<String> playlist)
    {
        Random rnd = new Random();
        for (int i = playlist.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            String a = playlist.get(index);
            String b = playlist.get(i);
            playlist.set(i,a);
            playlist.set(index,b);
        }
        return playlist;
    }

    public ArrayList<String> unshufflePlaylist()
    {
        ArrayList<String> playlist = new ArrayList<>();
        for(int i=0;i< songList.size();i++){
            PlayerHelper sh = songList.get(i);
            playlist.add(sh.getVideoid());
        }
        return playlist;
    }

    private void loadShazamPlaylist(){
        songList.clear();
        playlistArray.clear();
        origplaylistArray.clear();

        JSONArray response = fileHelper.readFile(Constants.APP_SHAZAM_PLAYLIST_FILE);
        JSONArray playzamresp = fileHelper.readFile(Constants.APP_PLAYZAM_PLAYLIST_FILE);
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                PlayerHelper song = new PlayerHelper();
                song.setTitle(obj.getString("title"));
                song.setThumbnailUrl(obj.getString("thumbnail"));
                song.setArtist(obj.getString("artist"));
                song.setAddedon(obj.getString("date"));
                song.setTrackid(obj.getString("trackid"));
                song.setDuration(obj.getString("duration"));
                song.setVideoid(obj.getString("videoid"));
                boolean isplaying = obj.getString("videoid").equals(videoID);
                song.setSelected(isplaying);
                playlistArray.add(obj.getString("videoid"));
                origplaylistArray.add(obj.getString("videoid"));
                // adding songs to song array
                songList.add(song);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < playzamresp.length(); i++) {
            try {
                JSONObject obj = playzamresp.getJSONObject(i);
                PlayerHelper song = new PlayerHelper();
                song.setTitle(obj.getString("title"));
                song.setThumbnailUrl(obj.getString("thumbnail"));
                song.setArtist(obj.getString("artist"));
                song.setAddedon(obj.getString("date"));
                song.setTrackid(obj.getString("trackid"));
                song.setDuration(obj.getString("duration"));
                song.setVideoid(obj.getString("videoid"));
                boolean isplaying = obj.getString("videoid").equals(videoID);
                song.setSelected(isplaying);
                playlistArray.add(obj.getString("videoid"));
                origplaylistArray.add(obj.getString("videoid"));
                // adding songs to song array
                songList.add(song);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        //    scrollItem();
    }

    public void updatePlaylistView(String videoId) {
        int currpos = -1;
        for(int i=0; i< songList.size();i++){
            PlayerHelper ph = songList.get(i);
            PlayerHelper song = new PlayerHelper();
            song.setTitle(ph.getTitle());
            song.setThumbnailUrl(ph.getThumbnailUrl());
            song.setArtist(ph.getArtist());
            song.setAddedon(ph.getAddedon());
            song.setTrackid(ph.getAddedon());
            song.setVideoid(ph.getVideoid());
            song.setDuration(ph.getDuration());
            if(ph.getVideoid().equals(videoId)){
                currpos = i;
                song.setSelected(true);
            }else{
                song.setSelected(false);
            }
            songList.set(i,song);
        }
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(currpos);
    }

    public class JavaScriptInterface {
        Context mContext;
        WebView wv;

        JavaScriptInterface(Context c, WebView wv) {
            mContext = c;
            this.wv = wv;
        }

        @JavascriptInterface
        public void changeActivity()
        {
            Toast.makeText(mContext,"Hii",Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void playerReady()
        {
            Log.e(TAG,"Player ready");

            if(videoID != null){
                int position = playlistArray.indexOf(videoID);
                final String videoid = playlistArray.get(position);
                wv.post(new Runnable() {
                    public void run() {
                        wv.loadUrl("javascript:PlayVideo('"+videoid+"')");
                    }
                });
            }else{
                Log.e(TAG,"Videoid null");
            }

        }

        @JavascriptInterface
        public void videoPlaying(final String videoid)
        {
            Log.e(TAG,"video playing");
            videoID = videoid;
            wv.post(new Runnable() {
                public void run() {
                    updatePlaylistView(videoid);
                    playBtn.setVisibility(View.GONE);
                    pauseBtn.setVisibility(View.VISIBLE);
                    //       listView.setSelection(10);
                    //    listView.getChildAt(10).setBackgroundColor(Color.parseColor("#cccccc"));
                }
            });
        }

        @JavascriptInterface
        public void videoEnd(String videoID)
        {
            Log.e(TAG,"video end");
            int position = playlistArray.indexOf(videoID);
            if(!repeat && position == (playlistArray.size() - 1)){
                Log.e(TAG,"reached the end");
                return;
            }else {
                if(repeat && position == (playlistArray.size() - 1)) position = -1;
            }
            final String videoid = playlistArray.get((position + 1));
            wv.post(new Runnable() {
                public void run() {
                    wv.loadUrl("javascript:PlayVideo('" + videoid + "')");
                }
            });
        }
    }
}
