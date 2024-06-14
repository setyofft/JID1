package com.jasamarga.jid.components;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SpeedometerView extends View {
    private Paint speedometerPaint;
    private Paint needlePaint;
    private float currentValue;
    private float maxValue = 100f;
    private int[] zoneColors = {
            Color.RED, Color.YELLOW, Color.GREEN,
            Color.CYAN, Color.BLUE, Color.MAGENTA
    };

    public SpeedometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        speedometerPaint = new Paint();
        speedometerPaint.setColor(Color.GRAY);
        speedometerPaint.setStyle(Paint.Style.STROKE);
        speedometerPaint.setStrokeWidth(20);

        needlePaint = new Paint();
        needlePaint.setStyle(Paint.Style.STROKE);
        needlePaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Gambar speedometer
        canvas.drawCircle(centerX, centerY, getWidth() / 2f, speedometerPaint);

        // Gambar jarum
        float angle = 150 + (currentValue / maxValue) * 240;
        float needleLength = getWidth() / 2.5f;
        float needleX = centerX + (float) Math.cos(Math.toRadians(angle)) * needleLength;
        float needleY = centerY + (float) Math.sin(Math.toRadians(angle)) * needleLength;
        needlePaint.setColor(getSpeedColor(currentValue));
        canvas.drawLine(centerX, centerY, needleX, needleY, needlePaint);
    }

    public void setCurrentValue(float value) {
        if (value >= 0 && value <= maxValue) {
            currentValue = value;
            invalidate();
        }
    }

    private int getSpeedColor(float value) {
        float sectionValue = maxValue / zoneColors.length;
        int section = (int) (value / sectionValue);
        return (section >= 0 && section < zoneColors.length) ? zoneColors[section] : zoneColors[0];
    }
}

