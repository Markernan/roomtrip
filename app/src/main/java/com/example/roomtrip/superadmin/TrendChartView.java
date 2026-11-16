package com.example.roomtrip.superadmin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.example.roomtrip.R;
import java.util.LinkedHashMap;
import java.util.Map;

public class TrendChartView extends View {
    private Map<String, Double> datos = new LinkedHashMap<>();
    private Paint paintBar;
    private Paint paintText;
    private Paint paintAxis;

    public TrendChartView(Context context) {
        super(context);
        init(context);
    }

    public TrendChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paintBar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBar.setColor(ContextCompat.getColor(context, R.color.turquesa));
        paintBar.setStyle(Paint.Style.FILL);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(ContextCompat.getColor(context, R.color.texto_principal));
        paintText.setTextSize(32f);

        paintAxis = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintAxis.setColor(ContextCompat.getColor(context, R.color.texto_secundario));
        paintAxis.setStrokeWidth(3f);
    }

    public void setDatos(Map<String, Double> datos) {
        this.datos = datos != null ? datos : new LinkedHashMap<>();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        if (datos.isEmpty()) {
            paintText.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Sin ingresos registrados en este período", w / 2f, h / 2f, paintText);
            return;
        }

        // Draw axes
        canvas.drawLine(50, h - 60, w - 50, h - 60, paintAxis);

        double maxVal = 1.0;
        for (Double val : datos.values()) {
            if (val > maxVal) maxVal = val;
        }

        int size = datos.size();
        float barWidth = Math.max(20f, (w - 100f - (size * 20f)) / Math.max(1, size));
        float startX = 70f;
        float chartHeight = h - 120f;

        paintText.setTextSize(26f);
        paintText.setTextAlign(Paint.Align.CENTER);

        int i = 0;
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            float valRatio = (float) (entry.getValue() / maxVal);
            float barH = Math.max(8f, valRatio * chartHeight);
            float left = startX + i * (barWidth + 20f);
            float top = (h - 60f) - barH;
            float right = left + barWidth;
            float bottom = h - 60f;

            canvas.drawRoundRect(new RectF(left, top, right, bottom), 8f, 8f, paintBar);
            canvas.drawText(entry.getKey(), left + barWidth / 2f, h - 20f, paintText);
            i++;
        }
    }
}
