package com.hellcoderz.ashutosh.playzam.helpers;

/**
 * Created by Sid on 6/6/2015.
 */
public class SongHelper {
    private String title, thumbnailUrl;
    private String date;
    private String artist;
    private String videoid;
    private String trackid;
    private String duration;

    public SongHelper(){

    }

    public SongHelper(String name, String thumbnailUrl, String date, String artist, String trackid, String duration, String videoid){
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.date = date;
        this.artist = artist;
        this.videoid = videoid;
        this.trackid = trackid;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }
    public String getDuration() {
        return duration;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAddedon() {
        return date;
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
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setVideoid(String videoid){
        this.videoid = videoid;
    }
}
