package com.hellcoderz.ashutosh.playzam.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellcoderz.ashutosh.playzam.R;
import com.hellcoderz.ashutosh.playzam.helpers.NavDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sid on 6/9/2015.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private List<Drawable> imgDrawable = new ArrayList<>();

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        imgDrawable.add(context.getResources().getDrawable(R.drawable.ic_home));
        imgDrawable.add(context.getResources().getDrawable(R.drawable.ic_autorenew_white_24dp));
        imgDrawable.add(context.getResources().getDrawable(R.drawable.ic_star_white_24dp));
        imgDrawable.add(context.getResources().getDrawable(R.drawable.ic_version));
        imgDrawable.add(context.getResources().getDrawable(R.drawable.ic_terms));
        imgDrawable.add(context.getResources().getDrawable(R.drawable.ic_security_white_24dp));
        imgDrawable.add(context.getResources().getDrawable(R.drawable.ic_logout));
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);

        holder.title.setText(current.getTitle());
        holder.imgView.setImageDrawable(imgDrawable.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imgView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            imgView = (ImageView) itemView.findViewById(R.id.imgview);
        }
    }
}
