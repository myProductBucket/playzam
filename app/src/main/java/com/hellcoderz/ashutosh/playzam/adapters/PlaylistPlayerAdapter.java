package com.hellcoderz.ashutosh.playzam.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.hellcoderz.ashutosh.playzam.R;
import com.hellcoderz.ashutosh.playzam.fragments.HomeFragment;
import com.hellcoderz.ashutosh.playzam.helpers.AppController;
import com.hellcoderz.ashutosh.playzam.helpers.CircularNetworkImageView;
import com.hellcoderz.ashutosh.playzam.helpers.PlayerHelper;

import java.util.List;

/**
 * Created by Sid on 6/14/2015.
 */
public class PlaylistPlayerAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<PlayerHelper> songItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PlaylistPlayerAdapter(Activity activity, List<PlayerHelper> songItems) {
        this.activity = activity;
        this.songItems = songItems;
    }

    @Override
    public int getCount() {
        return songItems.size();
    }

    @Override
    public Object getItem(int location) {
        return songItems.get(location);
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
            convertView = inflater.inflate(R.layout.player_row, null);

    //    if (imageLoader == null)
    //        imageLoader = AppController.getInstance().getImageLoader();

        CircularNetworkImageView thumbNail = (CircularNetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        TextView addedOn = (TextView) convertView.findViewById(R.id.genre);
        ImageButton shareBtn = (ImageButton) convertView.findViewById(R.id.shareBtn);

        // getting song data for the row
        final PlayerHelper m = songItems.get(position);
        // thumbnail image
    //    thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        thumbNail.setErrorImageResId(R.drawable.ic_profile);
        thumbNail.setDefaultImageResId(R.drawable.ic_profile);

        if(m.getSelected()){
            convertView.setBackgroundColor(Color.parseColor("#ffcccccc"));
        }else{
            convertView.setBackgroundColor(Color.parseColor("#00000000"));
        }

        //Song title
        title.setText(m.getTitle());

        //Song author
        author.setText(String.valueOf(m.getArtist()));

        //Song date server
        addedOn.setText(String.valueOf(m.getDuration()));

        convertView.setTag(m.getVideoid());



        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSong("http://youtu.be/"+m.getVideoid());
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
