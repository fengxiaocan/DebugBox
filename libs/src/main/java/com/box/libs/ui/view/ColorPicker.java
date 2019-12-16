package com.box.libs.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.IntRange;

import com.box.libs.R;

public class ColorPicker implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Dialog dialog;

    private TextView mTvCancel;
    private TextView mTvSure;
    private SeekBar mAlphaSeekBar;
    private SeekBar mRedSeekBar;
    private SeekBar mGreenSeekBar;
    private SeekBar mBlueSeekBar;
    private TextView mTvColor;
    private OnColorSelectListener onColorSelectListener;


    public ColorPicker(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.pd_item_color_picker, null);
        dialog = new Dialog(context);

        mTvCancel = inflate.findViewById(R.id.tv_cancel);
        mTvSure = inflate.findViewById(R.id.tv_sure);
        mAlphaSeekBar = inflate.findViewById(R.id.alpha_seek_bar);
        mRedSeekBar = inflate.findViewById(R.id.red_seek_bar);
        mGreenSeekBar = inflate.findViewById(R.id.green_seek_bar);
        mBlueSeekBar = inflate.findViewById(R.id.blue_seek_bar);
        mTvColor = inflate.findViewById(R.id.tv_color);
        dialog.setContentView(inflate);

        mTvCancel.setOnClickListener(this);
        mTvSure.setOnClickListener(this);

        mAlphaSeekBar.setOnSeekBarChangeListener(this);
        mRedSeekBar.setOnSeekBarChangeListener(this);
        mGreenSeekBar.setOnSeekBarChangeListener(this);
        mBlueSeekBar.setOnSeekBarChangeListener(this);

        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        dialog.getWindow().setBackgroundDrawable(null);
        //一定要在setContentView之后调用，否则无效
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    public ColorPicker setOnDismissListener(DialogInterface.OnDismissListener listener) {
        dialog.setOnDismissListener(listener);
        return this;
    }

    public ColorPicker setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    public ColorPicker setCanceledOnTouchOutside(boolean cancelable) {
        dialog.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public ColorPicker setColor(@IntRange(from = 0,
            to = 255) int alpha, @IntRange(from = 0,
            to = 255) int red, @IntRange(from = 0,
            to = 255) int green, @IntRange(from = 0,
            to = 255) int blue)
    {
        mAlphaSeekBar.setProgress(alpha);
        mRedSeekBar.setProgress(red);
        mGreenSeekBar.setProgress(green);
        mBlueSeekBar.setProgress(blue);
        return this;
    }


    public ColorPicker setColor(float alpha, float red, float green, float blue)
    {
        int color = ((int) (alpha * 255.0f + 0.5f) << 24) | ((int) (red * 255.0f + 0.5f) << 16) |
                    ((int) (green * 255.0f + 0.5f) << 8) | (int) (blue * 255.0f + 0.5f);
        mAlphaSeekBar.setProgress(Color.alpha(color));
        mRedSeekBar.setProgress(Color.red(color));
        mGreenSeekBar.setProgress(Color.green(color));
        mBlueSeekBar.setProgress(Color.blue(color));
        return this;
    }

    public ColorPicker setColor(int color)
    {
        mAlphaSeekBar.setProgress(Color.alpha(color));
        mRedSeekBar.setProgress(Color.red(color));
        mGreenSeekBar.setProgress(Color.green(color));
        mBlueSeekBar.setProgress(Color.blue(color));
        return this;
    }

    public ColorPicker setColor(String color)
    {
        setColor(Color.parseColor(color));
        return this;
    }

    public void dissmiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void show() {
        dialog.show();
    }

    public ColorPicker setOnColorSelectListener(OnColorSelectListener onColorSelectListener)
    {
        this.onColorSelectListener = onColorSelectListener;
        return this;
    }

    /**
     * @param v
     * @hide
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            dissmiss();
        } else if (v.getId() == R.id.tv_sure) {
            if (onColorSelectListener != null) {
                onColorSelectListener.onSelectColor(
                        mTvColor.getText().toString(),
                        mTvColor.getCurrentTextColor());
            }
            dissmiss();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int alpha = mAlphaSeekBar.getProgress();
        int red = mRedSeekBar.getProgress();
        int green = mGreenSeekBar.getProgress();
        int blue = mBlueSeekBar.getProgress();
        int color = Color.argb(alpha, red, green, blue);
        mTvColor.setTextColor(color);
        mTvColor.setText(toHexFromColor(color));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * Color对象转换成字符串
     *
     * @param color Color对象
     * @return 16进制颜色字符串
     */
    public static String toHexFromColor(int color) {
        String a, r, g, b;
        StringBuilder su = new StringBuilder();
        a = Integer.toHexString(Color.alpha(color));
        r = Integer.toHexString(Color.red(color));
        g = Integer.toHexString(Color.green(color));
        b = Integer.toHexString(Color.blue(color));
        a = a.length() == 1 ? "0" + a : a;
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;
        a = a.toUpperCase();
        r = r.toUpperCase();
        g = g.toUpperCase();
        b = b.toUpperCase();
        su.append("0x");
        su.append(a);
        su.append(r);
        su.append(g);
        su.append(b);
        //0xFF0000FF
        return su.toString();
    }

    public interface OnColorSelectListener {
        void onSelectColor(String colorString, int color);
    }
}
