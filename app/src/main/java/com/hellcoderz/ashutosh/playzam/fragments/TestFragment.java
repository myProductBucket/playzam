package com.hellcoderz.ashutosh.playzam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hellcoderz.ashutosh.playzam.R;
import com.hellcoderz.ashutosh.playzam.adapters.Songlistadapter;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SongHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sid on 6/11/2015.
 */
public class TestFragment extends Fragment {

    FileHelper fileHelper;
    ListView listView;
    Button syncButton;
    Songlistadapter adapter;
    private List<SongHelper> songList = new ArrayList<SongHelper>();

    public TestFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.songslist, container, false);
        fileHelper = new FileHelper(getActivity());
        if(!fileHelper.doesFileExist(Constants.APP_SHAZAM_PLAYLIST_FILE)){
            Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
            return null;
        }

        listView = (ListView) rootView.findViewById(R.id.list);
        syncButton = (Button) rootView.findViewById(R.id.sync);
        adapter = new Songlistadapter(getActivity(), songList);
        listView.setAdapter(adapter);

        loadShazamPlaylist();
        return rootView;
    }

    private void loadShazamPlaylist(){
        JSONArray response = fileHelper.readFile(Constants.APP_SHAZAM_PLAYLIST_FILE);
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                SongHelper song = new SongHelper();
                song.setTitle(obj.getString("title"));
                song.setThumbnailUrl(obj.getString("thumbnail"));
                song.setArtist(obj.getString("artist"));
                song.setAddedon(obj.getString("date"));
                song.setTrackid(obj.getString("trackid"));
                song.setVideoid(obj.getString("videoid"));
                // adding songs to song array
                songList.add(song);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        adapter.notifyDataSetChanged();
    }
}
