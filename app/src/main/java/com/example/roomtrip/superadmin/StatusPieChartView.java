package com.example.roomtrip.superadmin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.example.roomtrip.R;
import java.util.HashMap;
import java.util.Map;

public class StatusPieChartView extends View {
    private Map<String, Integer> estados = new HashMap<>();
    private Paint paintSlice;
    private Paint paintText;
    private int[] colores;
    private final RectF rectF = new RectF();

    public StatusPieChartView(Context context) {
        super(context);
        init(context);
    }

    public StatusPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paintSlice = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSlice.setStyle(Paint.Style.FILL);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(ContextCompat.getColor(context, R.color.texto_principal));
        paintText.setTextSize(32f);
        paintText.setTextAlign(Paint.Align.CENTER);

        colores = new int[]{
                ContextCompat.getColor(context, R.color.turquesa),
                ContextCompat.getColor(context, R.color.verde_exito),
                ContextCompat.getColor(context, R.color.amarillo_estrella),
                ContextCompat.getColor(context, R.color.rojo_alerta_texto),
                0xFF9C27B0
        };
    }

    public void setEstados(Map<String, Integer> estados) {
        this.estados = estados != null ? estados : new HashMap<>();
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        int total = 0;
        for (int count : estados.values()) {
            total += count;
        }

        if (total == 0) {
            paintSlice.setColor(0xFFE0E0E0);
            paintSlice.setStyle(Paint.Style.STROKE);
            paintSlice.setStrokeWidth(30f);
            float cx = w / 2f;
            float cy = h / 2f;
            float radius = Math.min(cx, cy) - 40f;
            canvas.drawCircle(cx, cy, radius, paintSlice);

            paintSlice.setStyle(Paint.Style.FILL);
            paintText.setTextSize(36f);
            canvas.drawText("0 reservas", cx, cy + 12f, paintText);
            return;
        }

        float cx = w / 2f - 80f;
        float cy = h / 2f;
        float radius = Math.min(cx, cy) - 20f;
        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius);

        float startAngle = 0f;
        int colorIdx = 0;
        float legendY = cy - (estados.size() * 20f);

        for (Map.Entry<String, Integer> entry : estados.entrySet()) {
            if (entry.getValue() <= 0) continue;
            float sweepAngle = (float) entry.getValue() / total * 360f;
            paintSlice.setColor(colores[colorIdx % colores.length]);
            paintSlice.setStyle(Paint.Style.FILL);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paintSlice);

            float legendX = w - 180f;
            canvas.drawRect(legendX, legendY, legendX + 24f, legendY + 24f, paintSlice);
            paintText.setTextAlign(Paint.Align.LEFT);
            paintText.setTextSize(26f);
            canvas.drawText(entry.getKey() + " (" + entry.getValue() + ")", legendX + 36f, legendY + 20f, paintText);

            legendY += 40f;
            startAngle += sweepAngle;
            colorIdx++;
        }
    }
}
