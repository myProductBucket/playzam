package com.hellcoderz.ashutosh.playzam.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Sid on 6/19/2015.
 */
public class TextViewPlayzamMedium extends TextView {
    public TextViewPlayzamMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewPlayzamMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewPlayzamMedium(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface cfont = new Typefaces().get(getContext(),Constants.TYPEFACE_Geogrotesque_medium);
        setTypeface(cfont);
    }
}
