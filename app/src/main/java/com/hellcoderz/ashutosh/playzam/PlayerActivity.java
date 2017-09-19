package com.hellcoderz.ashutosh.playzam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

/**
 * Created by Sid on 6/9/2015.
 */
public class PlayerActivity extends ActionBarActivity {
    private String TAG = "Playzam Player";
    private String videoID = "SDNVy57jFXQ";

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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.search_layout);
        /*
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        */
        fileHelper = new FileHelper(getApplicationContext());
     //   shuffleBtn = (ImageButton) findViewById(R.id.shuffle);
    //    prevBtn = (ImageButton) findViewById(R.id.prev);
    //    playBtn = (ImageButton) findViewById(R.id.play);
    //    nextBtn = (ImageButton) findViewById(R.id.next);
      //  repeatBtn = (ImageButton) findViewById(R.id.repeat);
      //  pauseBtn = (ImageButton) findViewById(R.id.pause);
        listView = (ListView) findViewById(R.id.list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("youtubeid")) {
                videoID = extras.getString("youtubeid");
            }else{
                Toast.makeText(getApplicationContext(),"No videoID found",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        adapter = new PlaylistPlayerAdapter(PlayerActivity.this, songList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            //    Log.e("a", "scrolling..."+firstVisibleItem);
                if(curposition >= firstVisibleItem &&curposition <= (firstVisibleItem+visibleItemCount)){
//                    listView.getChildAt(curposition).setBackgroundColor(Color.parseColor("#cccccc"));
                }
            }


            public void onScrollStateChanged(AbsListView view, int scrollState) {
            //    Log.e(TAG,"state:"+scrollState);
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
              //      Log.e("a", "scrolling stopped...");
                }
            }
        });
        loadShazamPlaylist();
        /*
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        JSInterface = new JavaScriptInterface(this,webView);
        webView.addJavascriptInterface(JSInterface, "JSInterface");
        webView.setWebChromeClient(new WebChromeClient() {
        });
    //    webView.loadUrl("file:///android_asset/test.html");
        webView.loadUrl("http://playzam.com/demo/youtube/index.html");
        */

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
                        playBtn.se          tVisibility(View.GONE);
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
                Log.e(TAG,"orig:"+playlistArray.toString());
                String color = (shuffle)?"#00ffffff":"#cccccc";
                shuffleBtn.setBackgroundColor(Color.parseColor(color));
                if(shuffle){
                    playlistArray = unshufflePlaylist();
                }else{
                    playlistArray = shufflePlaylist(playlistArray);
                }
                Log.e(TAG,"new:"+playlistArray.toString());
                shuffle = !shuffle;
            }
        });

        int[] solutionArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };



        for (int i = 0; i < solutionArray.length; i++)
        {
            Log.e(TAG,""+solutionArray[i] + " ");
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayerHelper sg = songList.get(position);
                String videoid = sg.getVideoid();
                updatePlaylistView(videoid);
                webView.loadUrl("javascript:PlayVideo('"+videoid+"')");
            }
        });
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

    @Override
    protected void onDestroy() {
        resetWebview();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5 && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            resetWebview();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void scrollItem(){
        int pos = origplaylistArray.indexOf(videoID);
        if (listView.getChildAt(pos) == null) {
            curposition = pos;
            listView.setItemChecked(pos, true);
            listView.smoothScrollToPosition(pos);
        }else{
            listView.getChildAt(pos).setBackgroundColor(Color.parseColor("#cccccc"));
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        resetWebview();
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void loadShazamPlaylist(){
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
                song.setVideoid(obj.getString("videoid") boolean isplaying = obj.getString("video);\n" +
                        "               id").equals(videoID);
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
            int position = playlistArray.indexOf(videoID);
            final String videoid = playlistArray.get(position);
            wv.post(new Runnable() {
                public void run() {
                    wv.loadUrl("javascript:PlayVideo('"+videoid+"')");
                }
            });
        }

        @JavascriptInterface
        public void videoPlaying(final String videoid)
        {
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
            int position = playlistArray.indexOf(videoID);
            if(!repeat && position == (playlistArray.size() - 1)){
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
