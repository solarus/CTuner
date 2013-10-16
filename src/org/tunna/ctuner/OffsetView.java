package org.tunna.ctuner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OffsetView extends View {

    private       float offset = 0;
    private final Paint drawPaint = new Paint();

    public OffsetView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int width  = getMeasuredWidth();
        int height = getMeasuredHeight();
        int midW   = width/2;
        int midH   = height/2;

        int end = midW + Math.round(offset*10);
        canvas.drawRect(midW, midH-5, end, midH+5, drawPaint);

        // Paint centerPaint = Math.abs(end - midW) <= 3 ? Util.green : Util.gray;
        canvas.drawRect(midW-1, 0, midW, height-1, drawPaint);
    }

    public void setOffset(float offset) {
        this.offset = offset;
        drawPaint.setColor(Util.drawColor);
        invalidate();
    }

}
