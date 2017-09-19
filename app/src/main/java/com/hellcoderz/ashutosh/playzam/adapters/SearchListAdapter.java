package com.hellcoderz.ashutosh.playzam.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.toolbox.ImageLoader;
import com.hellcoderz.ashutosh.playzam.PlayerActivity;
import com.hellcoderz.ashutosh.playzam.R;
import com.hellcoderz.ashutosh.playzam.SearchResultsActivity;
import com.hellcoderz.ashutosh.playzam.helpers.AppController;
import com.hellcoderz.ashutosh.playzam.helpers.CircularNetworkImageView;
import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.SearchHelper;
import com.hellcoderz.ashutosh.playzam.helpers.TextViewPlayzamMedium;

import java.util.List;

/**
 * Created by Sid on 6/12/2015.
 */
public class SearchListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<SearchHelper> searchItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public SearchListAdapter(Activity activity, List<SearchHelper> songItems) {
        this.activity = activity;
        this.searchItems = songItems;
    }

    @Override
    public int getCount() {
        return searchItems.size();
    }

    @Override
    public Object getItem(int location) {
        return searchItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.search_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextViewPlayzamMedium title = (TextViewPlayzamMedium) convertView.findViewById(R.id.title);
        TextViewPlayzamMedium author = (TextViewPlayzamMedium) convertView.findViewById(R.id.author);
        TextViewPlayzamMedium addedOn = (TextViewPlayzamMedium) convertView.findViewById(R.id.date);
        final Button playlistBtn = (Button) convertView.findViewById(R.id.genre);
        ImageButton shareBtn = (ImageButton) convertView.findViewById(R.id.shareBtn);

        // getting song data for the row
        final SearchHelper m = searchItems.get(position);
        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        thumbNail.setErrorImageResId(R.drawable.ic_profile);
        thumbNail.setDefaultImageResId(R.drawable.ic_profile);

        switch (m.ifisinPlaylist()){
            case Constants.VIDEO_CHECKING_PLAYLIST: playlistBtn.setEnabled(false);
                playlistBtn.getBackground().setAlpha(30);
                playlistBtn.setText("Please wait");
                break;
            case Constants.VIDEO_IN_PLAYLIST: playlistBtn.setEnabled(false);
                playlistBtn.getBackground().setAlpha(30);
                playlistBtn.setText("Added to playlist");
                break;
            case Constants.VIDEO_NOTIN_PLAYLIST: playlistBtn.setEnabled(true);
                playlistBtn.getBackground().setAlpha(255);
                playlistBtn.setText("Add to playlist");
                break;
            default:break;

        }
        //Song title
        title.setText(m.getTitle());

        //Song author
        author.setText(String.valueOf(m.getArtist()));

        //Song date server
        addedOn.setText(String.valueOf(m.getAddedon()));

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistBtn.setEnabled(false);
                playlistBtn.setText("Adding to playlist..");
                if(activity instanceof SearchResultsActivity){
                    ((SearchResultsActivity)activity).testMethod(searchItems,position);
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSong("http://youtu.be/"+m.getVideoid());
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("youtubeid", m.getVideoid());
                activity.startActivity(intent);
            }
        });

        return convertView;
    }

    public void ShareSong(String url){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = url;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this cool video!");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
