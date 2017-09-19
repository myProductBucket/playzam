package com.hellcoderz.ashutosh.playzam.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.hellcoderz.ashutosh.playzam.PlayerActivity;
import com.hellcoderz.ashutosh.playzam.R;
import com.hellcoderz.ashutosh.playzam.helpers.AppController;
import com.hellcoderz.ashutosh.playzam.helpers.CircularNetworkImageView;
import com.hellcoderz.ashutosh.playzam.helpers.SongHelper;
import com.hellcoderz.ashutosh.playzam.helpers.TextViewPlayzamMedium;

import java.util.List;

/**
 * Created by Sid on 6/6/2015.
 */
public class Songlistadapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<SongHelper> songItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public Songlistadapter(Activity activity, List<SongHelper> songItems) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.songs_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextViewPlayzamMedium title = (TextViewPlayzamMedium) convertView.findViewById(R.id.title);
        TextViewPlayzamMedium author = (TextViewPlayzamMedium) convertView.findViewById(R.id.author);
        TextViewPlayzamMedium duration = (TextViewPlayzamMedium) convertView.findViewById(R.id.duration);

        // getting song data for the row
        final SongHelper m = songItems.get(position);
        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        thumbNail.setErrorImageResId(R.drawable.ic_profile);
        thumbNail.setDefaultImageResId(R.drawable.ic_profile);

        //Song title
        title.setText(m.getTitle());

        //Song author
        author.setText(String.valueOf(m.getArtist()));

        duration.setText(m.getDuration());

        //Song date server
   //     addedOn.setText(String.valueOf(m.getAddedon()));

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
}
