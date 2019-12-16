package com.box.libs.inspector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

import com.box.libs.util.Config;
import com.box.libs.util.Utils;
import com.box.libs.util.ViewKnife;

/**
 * 网格线
 */
public class GridLineView extends View {

    private int LINE_INTERVAL;
    private Paint paint = new Paint() {
        {
            setAntiAlias(true);
            setStrokeWidth(1);
        }
    };

    public GridLineView(Context context) {
        super(context);
    }

    private void open() {

        LINE_INTERVAL = ViewKnife.dip2px(Config.getUI_GRID_INTERVAL());
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                       WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;

        paint.setColor(Config.getUI_GRID_COLOR());

        Utils.addViewToWindow(this, params);
    }

    private void close() {
        Utils.removeViewFromWindow(this);
    }

    public void toggle() {
        if (isOpen()) {
            close();
        } else {
            open();
        }
    }

    public boolean isOpen() {
        return ViewCompat.isAttachedToWindow(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int startX = 0;
        while (startX < getMeasuredWidth()) {
            canvas.drawLine(startX, 0, startX, getMeasuredHeight(), paint);
            startX = startX + LINE_INTERVAL;
        }

        int startY = 0;
        while (startY < getMeasuredHeight()) {
            canvas.drawLine(0, startY, getMeasuredWidth(), startY, paint);
            startY = startY + LINE_INTERVAL;
        }
    }
}
