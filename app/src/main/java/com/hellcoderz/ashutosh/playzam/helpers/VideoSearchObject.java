package com.hellcoderz.ashutosh.playzam.helpers;

import com.google.api.services.youtube.model.ResourceId;

/**
 * Created by Sid on 6/7/2015.
 */
public class VideoSearchObject {
    protected ResourceId videoResourceId;
    protected boolean isVideofound;

    public VideoSearchObject(ResourceId videoResourceId,boolean isVideofound){
        this.videoResourceId = videoResourceId;
        this.isVideofound = isVideofound;
    }

    public ResourceId getVideoResourceId(){
        return this.videoResourceId;
    }

    public boolean getVideoStatus(){
        return this.isVideofound;
    }
}
