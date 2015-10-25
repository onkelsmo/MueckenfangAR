package com.onkelsmo.mueckenfang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class RadarView extends View {
    private float scale;
    private Paint midgeColor = new Paint();
    private Paint strokeColor = new Paint();
    private float angle = 0;
    private FrameLayout container;

    public void setContainer(FrameLayout container) {
        this.container = container;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.transGrey));
        if (container == null) return;

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        canvas.drawCircle(width/2, height/2, radius, strokeColor);
        canvas.drawArc(new RectF(0, 0, width, height), -95, 10, true, strokeColor);

        int number = 0;
        while (number < container.getChildCount()) {
            ImageView midge = (ImageView)container.getChildAt(number);
            float azimut = (Integer)midge.getTag(R.id.azimut);
            canvas.drawArc(new RectF(width * 0.1f, height * 0.1f, width * 0.9f, height * 0.9f),
                    azimut + angle-90, 5, false, midgeColor);
            number++;
        }
    }

    public RadarView(Context context) {
        super(context);
        init();
    }

    private void init() {
        scale = getResources().getDisplayMetrics().density;
        midgeColor.setAntiAlias(true);
        midgeColor.setColor(Color.RED);
        midgeColor.setStyle(Paint.Style.STROKE);
        midgeColor.setStrokeWidth(5 * scale);
        strokeColor.setAntiAlias(true);
        strokeColor.setColor(Color.WHITE);
        strokeColor.setStrokeWidth(1 * scale);
        strokeColor.setStyle(Paint.Style.STROKE);
    }
}
