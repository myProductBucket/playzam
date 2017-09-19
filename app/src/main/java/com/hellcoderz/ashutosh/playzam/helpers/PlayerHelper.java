package com.hellcoderz.ashutosh.playzam.helpers;

/**
 * Created by Sid on 6/16/2015.
 */
public class PlayerHelper {
    private String title, thumbnailUrl;
    private String date;
    private String artist;
    private String videoid;
    private String trackid;
    private boolean isselected;
    private String duration;

    public PlayerHelper(){

    }

    public PlayerHelper(String name, String thumbnailUrl, String date, String artist, String trackid, String videoid, String duration, boolean iscurrent){
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.date = date;
        this.artist = artist;
        this.videoid = videoid;
        this.trackid = trackid;
        this.isselected = iscurrent;
        this.duration = duration;
    }

    public boolean getSelected(){
        return isselected;
    }

    public void setSelected(boolean isselected){
        this.isselected = isselected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getTrackId(){
        return trackid;
    }

    public void setTrackid(String trackid){
        this.trackid = trackid;
    }
    public void setDuration(String duration){
        this.duration = duration;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAddedon() {
        return date;
    }
    public String getDuration() {
        return duration;
    }

    public void setAddedon(String date) {
        this.date = date;
    }

    public String getArtist() {
        return artist;
    }

    public String getVideoid(){
        return videoid;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setVideoid(String videoid){
        this.videoid = videoid;
    }
}
