package org.tunna.ctuner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * A class representing a horizontal bar. The bar will have its origin
 * in the middle of the canvas and grow towards the left or the right
 * edge depending on the current sign of the <code>length</code>.
 */
public class HorizontalBarView extends View {

    private       float length = 0;
    private final Paint drawPaint = new Paint();

    public HorizontalBarView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int width  = getMeasuredWidth();
        int height = getMeasuredHeight();
        int midW   = width/2;
        int midH   = height/2;
        int end    = midW + Math.round(length*midW);

        // Draw the middle vertical line
        canvas.drawRect(midW-1, 0, midW, height-1, drawPaint);
        // Draw the horizontal bar
        canvas.drawRect(midW, midH-5, end, midH+5, drawPaint);
    }

    /**
     * Set the relative length of the bar. The view will be
     * invalidated if the length changed since the last update.
     *
     * @param length The relative length of the horizontal bar. From
     * -1 to 1. -1 corresponds to the bar ranging from the leftmost
     * point to the middle. 1 Corresponds to the bar ranging from the
     * middle to the rightmost point. If the parameter is outside the
     * range [-1,1], it will be automatically truncated.
     */
    public void setLength(float length) {
        length = truncateLength(length);

        if (this.length == length) {
            return;
        }

        this.length = length;
        drawPaint.setColor(Util.drawColor);
        invalidate();
    }

    /**
     * Truncates the size of the parameter <code>length</code> to be inside the
     * range [-1.0,1.0]
     */
    private float truncateLength(float length) {
        if (Math.abs(length) > 1) {
            return Math.signum(length);
        }
        else {
            return length;
        }
    }

}
