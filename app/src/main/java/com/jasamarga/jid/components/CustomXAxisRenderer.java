package com.jasamarga.jid.components;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

public class CustomXAxisRenderer extends XAxisRenderer {

    private ArrayList<Drawable> icons;

    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, ArrayList<Drawable> icons) {
        super(viewPortHandler, xAxis, trans);
        this.icons = icons;
    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {
        float labelRotationAngle = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();
        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {
            int index = i / 2;
            if (index >= 0 && index < icons.size()) {
                Drawable drawable = icons.get(index);
                if (drawable != null) {
                    float x = positions[i];
                    float y = pos;

                    if (labelRotationAngle != 0) {
                        // Rotate the canvas for rotated labels
                        c.save();
                        c.translate(x, y);
                        c.rotate(labelRotationAngle);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        drawable.draw(c);
                        c.restore();
                    } else {
                        drawable.setBounds(
                                (int) (x - drawable.getIntrinsicWidth() / 2f),
                                (int) (y - drawable.getIntrinsicHeight() / 2f),
                                (int) (x + drawable.getIntrinsicWidth() / 2f),
                                (int) (y + drawable.getIntrinsicHeight() / 2f)
                        );
                        drawable.draw(c);
                    }
                }
            }
        }
    }
}


