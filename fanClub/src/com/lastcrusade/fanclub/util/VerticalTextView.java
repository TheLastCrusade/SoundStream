package com.lastcrusade.fanclub.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class VerticalTextView extends TextView {

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void onDraw(Canvas canvas){
        canvas.save();
        
        canvas.translate(getWidth(),0);
        canvas.rotate(90);
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        
        getLayout().draw(canvas);
        canvas.restore();
    }

}
