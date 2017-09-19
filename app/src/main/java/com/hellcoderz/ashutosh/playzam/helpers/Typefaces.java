package com.hellcoderz.ashutosh.playzam.helpers;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by ashutosh on 1/17/15.
 */
public class Typefaces {
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public Typeface get(Context c, String path){
        synchronized(cache){
            if(!cache.containsKey(path)){
                Typeface t = Typeface.createFromAsset(c.getAssets(), String.format("fonts/%s",path));
                cache.put(path, t);
            }
            return cache.get(path);
        }
    }
}
