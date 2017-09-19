package com.hellcoderz.ashutosh.playzam.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Sid on 6/16/2015.
 */
public class TextViewPlayzam extends TextView {
    public TextViewPlayzam(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewPlayzam(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewPlayzam(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface cfont = new Typefaces().get(getContext(),Constants.TYPEFACE_Geogrotesque_regular);
        setTypeface(cfont);
    }
}
