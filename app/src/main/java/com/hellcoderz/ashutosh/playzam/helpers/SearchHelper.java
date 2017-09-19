package com.hellcoderz.ashutosh.playzam.helpers;

/**
 * Created by Sid on 6/12/2015.
 */
public class SearchHelper {
    private String title, thumbnailUrl;
    private String date;
    private String artist;
    private String videoid;
    private int isinplaylist;

    public SearchHelper(){

    }

    public SearchHelper(String title, String thumbnailUrl, String date, String artist,String videoid,int isinplaylist){
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.date = date;
        this.artist = artist;
        this.videoid = videoid;
        this.isinplaylist = isinplaylist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int ifisinPlaylist(){
        return isinplaylist;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setIsinplaylist(int isinplaylist) {
        this.isinplaylist = isinplaylist;
    }

    public String getAddedon() {
        return date;
    }

    public String getDate(){
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

    public void setVideoid(String videoid){
        this.videoid = videoid;
    }
}
