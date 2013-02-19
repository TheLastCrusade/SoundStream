package com.lastcrusade.fanclub.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class VerticalTextView extends TextView {

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
     }
    
    @Override
    protected void onDraw(Canvas canvas){
        canvas.save();
       
        canvas.rotate(-90,getLeft(), getTop());
        canvas.translate(-getHeight()/2.0f,0);
        super.onDraw(canvas);
        canvas.restore();
    }

}
