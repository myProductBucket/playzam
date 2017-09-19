package com.hellcoderz.ashutosh.playzam.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Sid on 6/19/2015.
 */
public class TextViewPlayzamLight extends TextView {
    public TextViewPlayzamLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewPlayzamLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewPlayzamLight(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface cfont = new Typefaces().get(getContext(),Constants.TYPEFACE_Geogrotesque_light);
        setTypeface(cfont);
    }
}
