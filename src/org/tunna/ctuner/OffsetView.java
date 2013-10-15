package org.tunna.ctuner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OffsetView extends View {

    private static Paint green = new Paint();
    private static Paint gray  = new Paint();

    static {
        green.setColor(0xff20ff20);
        gray.setColor(0xffa0a0a0);
    }

    private float ratio = 0;

    public OffsetView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int width  = getMeasuredWidth();
        int height = getMeasuredHeight();
        int midW   = width/2;
        int midH   = height/2;

        int end = midW + Math.round(ratio * 100);
        canvas.drawRect(midW, midH-5, end, midH+5, green);

        Paint centerPaint = Math.abs(end) < 3 ? green : gray;
        canvas.drawRect(midW-1, 0, midW+1, height-1, centerPaint);
    }

    public void setOffsetRatio(float ratio) {
        this.ratio = ratio;
        invalidate();
    }

}
