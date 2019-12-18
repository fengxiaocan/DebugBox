package com.box.libs.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.box.libs.R;
import com.box.libs.ui.item.FuncItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.Config;
import com.box.libs.util.ViewKnife;

/**
 * Created by linjiang on 2019/3/4.
 */

public class WebConfigView extends LinearLayout {

    private UniversalAdapter adapter;
    private float lastY;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            Config.setDragY(lastY);
        }
    };
    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    WindowManager.LayoutParams params =
                            (WindowManager.LayoutParams) getLayoutParams();
                    params.y += event.getRawY() - lastY;
                    params.y = Math.max(0, params.y);
                    BoxUtils.updateViewLayoutInWindow(WebConfigView.this, params);
                    lastY = event.getRawY();
                    BoxUtils.cancelTask(task);
                    BoxUtils.postDelayed(task, 200);
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private OnCloseListener onCloseListener;

    @SuppressLint("ClickableViewAccessibility")
    public WebConfigView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.pd_shadow_131124);
        ImageView moveView = new ImageView(context);
        RecyclerView recyclerView = new RecyclerView(context);
        ImageView closeView = new ImageView(context);

        moveView.setImageResource(R.drawable.pd_drag);
        moveView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        moveView.setOnTouchListener(touchListener);
        closeView.setImageResource(R.drawable.pd_close);
        closeView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter = new UniversalAdapter());

        addView(moveView,
                new LayoutParams(ViewKnife.dip2px(30), ViewGroup.LayoutParams.MATCH_PARENT));
        addView(recyclerView, new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        addView(closeView,
                new LayoutParams(ViewKnife.dip2px(50), ViewGroup.LayoutParams.MATCH_PARENT));

    }

    //    @Override
    //    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //        int maxWidth;
    //        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    //            // drag + close + 5*func + 0.5*func
    //            maxWidth = ViewKnife.dip2px(64) + ViewKnife.dip2px(50) * 5 + ViewKnife.dip2px(24);
    //        } else {
    //            maxWidth = MeasureSpec.getSize(widthMeasureSpec);
    //        }
    //        super.onMeasure(MeasureSpec.makeMeasureSpec(
    //                Math.min(MeasureSpec.getSize(widthMeasureSpec), maxWidth),
    //                MeasureSpec.getMode(widthMeasureSpec)), heightMeasureSpec);
    //    }

    public void addItem(@DrawableRes int icon, String name) {
        adapter.insertItem(new FuncItem(icon, name));
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        adapter.setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                boolean selected = listener.onItemClick(position);
                ((FuncItem) item).setSelected(selected);
                adapter.notifyItemChanged(position);
            }
        });
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public boolean open() {
        if (ViewCompat.isAttachedToWindow(this)) {
            return true;
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = ViewKnife.dip2px(62);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = (int) Config.getDragY();
        return BoxUtils.addViewToWindow(this, params);
    }

    public void close() {
        if (ViewCompat.isAttachedToWindow(this)) {
            BoxUtils.removeViewFromWindow(this);
        }
        if (onCloseListener != null) {
            onCloseListener.onClose();
        }
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    public interface OnItemClickListener {
        boolean onItemClick(int index);
    }

    public interface OnCloseListener {
        void onClose();
    }
}
