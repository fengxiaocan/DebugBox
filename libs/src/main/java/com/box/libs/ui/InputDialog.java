package com.box.libs.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.box.libs.R;

public class InputDialog {
    private Context context;
    private AlertDialog dialog;
    private TextView txt_title;
    private EditText txt_input;
    //addView 父容器
    private LinearLayout linearLayoutMain;
    private LinearLayout linearLayoutGroup;
    private TextView btn_left;
    private TextView btn_middle;
    private TextView btn_right;
    private View mViewLine;
    private View mViewLineRight;
    private View mViewLineHorizontal;
    private boolean showTitle = false;
    private boolean showRightBtn = false;
    private boolean showLeftBtn = false;
    private boolean showMiddleBtn = false;
    private InputResultListener mInputResultListener;
    /**
     * 是否自定义了button样式
     */
    private boolean isCustomButtonStyle = false;

    private int gravity = Gravity.LEFT;
    private Window window;
    private WindowManager.LayoutParams lp;

    /**
     * Instantiates a new Ios dialog.
     *
     * @param context the context
     */
    public InputDialog(Context context) {
        this.context = context;
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.layout_input_alert_view, null);
        // 获取自定义Dialog布局中的控件
        txt_title = (TextView) view.findViewById(R.id.tv_titleAlertView);
        txt_title.setVisibility(View.GONE);
        txt_input = (EditText) view.findViewById(R.id.et_input);
        btn_left = (TextView) view.findViewById(R.id.tv_leftAlertView);
        btn_left.setVisibility(View.GONE);
        btn_middle = (TextView) view.findViewById(R.id.tv_middleAlertView);
        btn_middle.setVisibility(View.GONE);
        btn_right = (TextView) view.findViewById(R.id.tv_rightAlertView);
        btn_right.setVisibility(View.GONE);
        mViewLine = view.findViewById(R.id.v_lineAlertView);
        mViewLine.setVisibility(View.GONE);
        mViewLineHorizontal = view.findViewById(R.id.v_lineHorizontalAlertView);
        mViewLineHorizontal.setVisibility(View.GONE);
        mViewLineRight = view.findViewById(R.id.v_lineRightAlertView);
        mViewLineRight.setVisibility(View.GONE);
        linearLayoutGroup = (LinearLayout) view.findViewById(R.id.lLayout_groupAlertView);
        linearLayoutMain = (LinearLayout) view.findViewById(R.id.lLayout_mainAlertView);
        // 定义Dialog布局和参数
        dialog = new AlertDialog.Builder(context, R.style.AlertViewDialogStyle).create();
        dialog.show();
        dialog.setContentView(view);
        window = dialog.getWindow();
        lp = window.getAttributes();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setLeftButton(R.string.cancel, new DialogCancelListener());
        setRightButton(R.string.sure, new IDialogListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                if (mInputResultListener != null) {
                    mInputResultListener.onResult(txt_input.getText().toString());
                }
            }
        });
        dialog.dismiss();
    }

    /**
     * Builder ios dialog.
     *
     * @return the ios dialog
     */
    public InputDialog builder() {
        return this;
    }

    /**
     * Get dialog alert dialog.
     *
     * @return the alert dialog
     */
    public AlertDialog getDialog() {
        return dialog;
    }

    /**
     * 设置窗口透明度
     *
     * @param alpha the alpha
     * @return ios dialog
     */
    public InputDialog setAlpha(float alpha) {
        lp.alpha = alpha;// 透明度
        window.setAttributes(lp);
        return this;
    }

    /**
     * 设置背景黑暗度
     *
     * @param dimAmount the dim amount
     * @return ios dialog
     */
    public InputDialog setDimAmount(float dimAmount) {
        lp.dimAmount = dimAmount;// 黑暗度
        window.setAttributes(lp);
        return this;
    }

    /**
     * Set content view ios dialog.
     *
     * @param layoutResID the layout res id
     * @return the ios dialog
     */
    public InputDialog setContentView(int layoutResID) {
        dialog.show();
        dialog.setContentView(layoutResID);
        return this;
    }

    /**
     * Set background color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setBackgroundColor(int color) {
        linearLayoutMain.setBackgroundColor(color);
        return this;
    }

    /**
     * Set background resource ios dialog.
     *
     * @param resId the res id
     * @return the ios dialog
     */
    public InputDialog setBackgroundResource(@DrawableRes int resId) {
        linearLayoutMain.setBackgroundResource(resId);
        return this;
    }

    /**
     * Set background ios dialog.
     *
     * @param background the background
     * @return the ios dialog
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public InputDialog setBackground(Drawable background) {
        linearLayoutMain.setBackground(background);
        return this;
    }

    /**
     * 是否设置点击dialog区域外，dialog消失
     *
     * @param cancel the cancel
     */
    public InputDialog setCanceled(boolean cancel) {
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }

    /**
     * 设置标题
     *
     * @param title the title
     * @return ios dialog
     */
    public InputDialog setTitle(String title) {
        if (!title.isEmpty()) {
            showTitle = true;
            txt_title.setText(Html.fromHtml(title));
        }
        return this;
    }

    /**
     * Set title ios dialog.
     *
     * @param title the title
     * @return the ios dialog
     */
    public InputDialog setTitle(int title) {
        return setTitle(context.getString(title));
    }

    /**
     * Set title text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setTitleTextColor(int color) {
        txt_title.setTextColor(color);
        return this;
    }

    /**
     * Set title text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setTitleTextColor(ColorStateList color) {
        txt_title.setTextColor(color);
        return this;
    }

    /**
     * 设置title textSize参考 TextView.setTextSize(unit, textSize)方法
     *
     * @param unit     the unit
     * @param textSize the text size
     * @return ios dialog
     */
    public InputDialog setTitleTextSize(int unit, float textSize) {
        txt_title.setTextSize(unit, textSize);
        return this;
    }

    /**
     * 设置提示语
     *
     * @param msg the msg
     * @return ios dialog
     */
    public InputDialog setInputMessage(String msg) {
        if (msg == null) {
            return this;
        }
        txt_input.setText(msg);
        if (msg.contains("<br />")) {
            txt_input.setText(Html.fromHtml(msg));
        }
        txt_input.post(new Runnable() {
            @Override
            public void run() {
                if (txt_input.getLineCount() > 4) {
                    txt_input.setMaxWidth(
                            (int) context.getResources().getDimension(R.dimen.alert_max_width));
                }
                {
                    txt_input.setMaxWidth(
                            (int) context.getResources().getDimension(R.dimen.alert_max_width_));
                }
            }
        });
        int padding = (int) context.getResources().getDimension(R.dimen.alert_dp_padding);
        txt_input.setPadding(padding, padding, padding, padding);
        txt_input.setGravity(gravity);
        return this;
    }


    /**
     * Set message ios dialog.
     *
     * @param gravity the gravity
     */
    public InputDialog setInputGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * Set message ios dialog.
     */
    public InputDialog setInputHint(CharSequence hint) {
        txt_input.setHint(hint);
        return this;
    }

    /**
     * Set message ios dialog.
     */
    public InputDialog setInputMaxLines(int lines) {
        txt_input.setMaxLines(lines);
        return this;
    }

    /**
     * Set message ios dialog.
     */
    public InputDialog setInputMaxLenght(int lenght) {
        InputFilter[] filters = {new InputFilter.LengthFilter(lenght)};
        txt_input.setFilters(filters);
        return this;
    }

    /**
     * Set message ios dialog.
     */
    public InputDialog setInputType(int type) {
        txt_input.setInputType(type);
        return this;
    }

    /**
     * Set message ios dialog.
     */
    public InputDialog setInputHintColor(@ColorInt int color) {
        txt_input.setHintTextColor(color);
        return this;
    }

    /**
     * Set message ios dialog.
     */
    public InputDialog setInputHintColor(ColorStateList color) {
        txt_input.setHintTextColor(color);
        return this;
    }

    /**
     * Set message ios dialog.
     */
    public InputDialog setInputHint(@StringRes int resid) {
        txt_input.setHint(resid);
        return this;
    }

    /**
     * Set message ios dialog.
     *
     * @param msg the msg
     * @return the ios dialog
     */
    public InputDialog setInputMessage(int msg) {
        return setInputMessage(context.getString(msg));
    }


    /**
     * Set message text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setInputTextColor(int color) {
        txt_input.setTextColor(color);
        return this;
    }

    /**
     * Set message text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setInputTextColor(ColorStateList color) {
        txt_input.setTextColor(color);
        return this;
    }


    /**
     * Set message text color ios dialog.
     *
     * @return the ios dialog
     */
    public InputDialog setInputBackgroundResource(@DrawableRes int resid) {
        txt_input.setBackgroundResource(resid);
        return this;
    }

    /**
     * Set message text color ios dialog.
     *
     * @return the ios dialog
     */
    public InputDialog setInputBackgroundColor(@ColorInt int color) {
        txt_input.setBackgroundColor(color);
        return this;
    }

    public EditText getInput() {
        return txt_input;
    }

    /**
     * 设置Message textSize参考 TextView.setTextSize(unit, textSize)方法
     *
     * @param unit     the unit
     * @param textSize the text size
     * @return ios dialog
     */
    public InputDialog setInputTextSize(int unit, float textSize) {
        txt_input.setTextSize(unit, textSize);
        return this;
    }

    public InputResultListener getInputResultListener() {
        return mInputResultListener;
    }

    public InputDialog setInputResultListener(InputResultListener inputResultListener) {
        mInputResultListener = inputResultListener;
        return this;
    }

    /**
     * 设置message最低高度
     *
     * @param minHeight the min height
     * @return ios dialog
     */
    public InputDialog setInputMinHeight(final int minHeight) {
        txt_input.setMinimumHeight(minHeight);
        return this;
    }

    /**
     * Set min height ios dialog.
     *
     * @param minHeight the min height
     * @return ios dialog
     */
    public InputDialog setMinHeight(final int minHeight) {
        linearLayoutMain.setMinimumHeight(minHeight);
        linearLayoutGroup.setMinimumHeight(minHeight);
        return this;
    }


    /**
     * Set min width ios dialog.
     *
     * @param minWidth the min width
     * @return the ios dialog
     */
    public InputDialog setMinWidth(final int minWidth) {
        linearLayoutMain.setMinimumWidth(minWidth);
        linearLayoutGroup.setMinimumWidth(minWidth);
        return this;
    }

    /**
     * 设置Button textSize参考 TextView.setTextSize(unit, textSize)方法
     *
     * @param unit     the unit
     * @param textSize the text size
     * @return ios dialog
     */
    public InputDialog setButtonTextSize(int unit, float textSize) {
        btn_left.setTextSize(unit, textSize);
        btn_right.setTextSize(unit, textSize);
        return this;
    }


    /**
     * 师傅返回键弹框可消失
     *
     * @param cancel the cancel
     * @return ios dialog
     */
    public InputDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    /**
     * 设置左边按钮
     *
     * @param text     the text
     * @param listener the listener
     * @return ios dialog
     */
    public InputDialog setLeftButton(String text, final IDialogListener listener) {
        showLeftBtn = true;
        btn_left.setText(Html.fromHtml(text));
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(dialog);
                }
                dialog.dismiss();
            }
        });
        return this;
    }


    /**
     * 设置左边按钮
     *
     * @param text the text
     * @return ios dialog
     */
    public InputDialog setLeftButton(String text) {
        showLeftBtn = true;
        btn_left.setText(Html.fromHtml(text));
        return this;
    }


    /**
     * 修改左边button背景
     *
     * @param resId the res id
     * @return ios dialog
     */
    public InputDialog setLeftButtonBackgroundResource(@DrawableRes int resId) {
        isCustomButtonStyle = true;
        btn_left.setBackgroundResource(resId);
        return this;
    }

    /**
     * Set negative button background color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setLeftButtonBackgroundColor(@ColorInt int color) {
        isCustomButtonStyle = true;
        btn_left.setBackgroundColor(color);
        return this;
    }

    /**
     * Set Left button background ios dialog.
     *
     * @param background the background
     * @return the ios dialog
     */
    public InputDialog setLeftButtonBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            isCustomButtonStyle = true;
            btn_left.setBackground(background);
        }
        return this;
    }

    /**
     * Set Left button text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setLeftButtonTextColor(int color) {
        btn_left.setTextColor(color);
        return this;
    }

    /**
     * Set Left button text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setLeftButtonTextColor(ColorStateList color) {
        btn_left.setTextColor(color);
        return this;
    }

    /**
     * Set Middle button ios dialog.
     *
     * @param text     the text
     * @param listener the listener
     * @return the ios dialog
     */
    public InputDialog setMiddleButton(String text, final IDialogListener listener) {
        showMiddleBtn = true;
        btn_middle.setText(Html.fromHtml(text));
        btn_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(dialog);
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    /**
     * Set Middle button ios dialog.
     *
     * @param text the text
     * @return the ios dialog
     */
    public InputDialog setMiddleButton(String text) {
        showMiddleBtn = true;
        btn_middle.setText(Html.fromHtml(text));
        btn_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return this;
    }


    /**
     * Set Middle button ios dialog.
     *
     * @param text the text
     * @return the ios dialog
     */
    public InputDialog setMiddleButton(@StringRes int text) {
        return setMiddleButton(context.getString(text));
    }

    /**
     * Set Middle button ios dialog.
     *
     * @param text     the text
     * @param listener the listener
     * @return the ios dialog
     */
    public InputDialog setMiddleButton(int text, final IDialogListener listener) {
        return setMiddleButton(context.getString(text), listener);
    }

    /**
     * Set Middle button text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setMiddleButtonTextColor(int color) {
        btn_middle.setTextColor(color);
        return this;
    }

    /**
     * Set Middle button text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setMiddleButtonTextColor(ColorStateList color) {
        btn_middle.setTextColor(color);
        return this;
    }

    /**
     * 设置中间按钮样式
     *
     * @param background the background
     * @return ios dialog
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public InputDialog setMiddleButtonBackground(Drawable background) {
        isCustomButtonStyle = true;
        btn_middle.setBackground(background);
        return this;
    }

    /**
     * Set Middle button background resource ios dialog.
     *
     * @param resId the res id
     * @return the ios dialog
     */
    public InputDialog setMiddleButtonBackgroundResource(@DrawableRes int resId) {
        isCustomButtonStyle = true;
        btn_middle.setBackgroundResource(resId);
        return this;
    }

    /**
     * Set Middle button background color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setMiddleButtonBackgroundColor(@ColorInt int color) {
        isCustomButtonStyle = true;
        btn_middle.setBackgroundColor(color);
        return this;
    }


    /**
     * 设置右边按钮样式
     *
     * @param background the background
     * @return ios dialog
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public InputDialog setRightButtonBackground(Drawable background) {
        isCustomButtonStyle = true;
        btn_right.setBackground(background);
        return this;
    }

    /**
     * 设置右边button背景样式
     *
     * @param resId the res id
     * @return ios dialog
     */
    public InputDialog setRightButtonBackgroundResource(@DrawableRes int resId) {
        isCustomButtonStyle = true;
        btn_right.setBackgroundResource(resId);
        return this;
    }

    /**
     * Set Right button background color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setRightButtonBackgroundColor(@ColorInt int color) {
        isCustomButtonStyle = true;
        btn_right.setBackgroundColor(color);
        return this;
    }

    /**
     * Set on key listener ios dialog.
     *
     * @param onKeyListener the on key listener
     * @return the ios dialog
     */
    public InputDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        dialog.setOnKeyListener(onKeyListener);
        return this;
    }

    /**
     * Set on dismiss listener ios dialog.
     *
     * @param onDismissListener the on dismiss listener
     * @return the ios dialog
     */
    public InputDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        dialog.setOnDismissListener(onDismissListener);
        return this;
    }

    /**
     * Set Left button ios dialog.
     *
     * @param text     the text
     * @param listener the listener
     * @return the ios dialog
     */
    public InputDialog setLeftButton(int text, final IDialogListener listener) {
        return setLeftButton(context.getString(text), listener);
    }

    /**
     * Set Left button ios dialog.
     *
     * @param text the text
     * @return the ios dialog
     */
    public InputDialog setLeftButton(int text) {
        return setLeftButton(context.getString(text));
    }

    /**
     * Set Right button text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setRightButtonTextColor(int color) {
        btn_right.setTextColor(color);
        return this;
    }

    /**
     * Set Right button text color ios dialog.
     *
     * @param color the color
     * @return the ios dialog
     */
    public InputDialog setRightButtonTextColor(ColorStateList color) {
        btn_right.setTextColor(color);
        return this;
    }

    /**
     * Set Right button ios dialog.
     *
     * @param text     the text
     * @param listener the listener
     * @return the ios dialog
     */
    public InputDialog setRightButton(String text, final IDialogListener listener) {
        return setRightButton(text, listener, true);
    }

    /**
     * Set Right button ios dialog.
     *
     * @param text      the text
     * @param listener  the listener
     * @param isDismiss the is dismiss
     * @return the ios dialog
     */
    public InputDialog setRightButton(String text, final IDialogListener listener,
            final boolean isDismiss)
    {
        showRightBtn = true;
        btn_right.setText(Html.fromHtml(text));
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(dialog);
                }
                if (isDismiss)
                    dialog.dismiss();
            }
        });
        return this;
    }

    /**
     * Set Right button ios dialog.
     *
     * @param text the text
     * @return the ios dialog
     */
    public InputDialog setRightButton(String text) {
        showRightBtn = true;
        btn_right.setText(Html.fromHtml(text));
        return this;
    }

    /**
     * Set Right button ios dialog.
     *
     * @param text the text
     * @return the ios dialog
     */
    public InputDialog setRightButton(@StringRes int text) {
        return setRightButton(context.getString(text));
    }


    /**
     * 设置右边按钮
     *
     * @param text     the text
     * @param listener the listener
     * @return ios dialog
     */
    public InputDialog setRightButton(int text, final IDialogListener listener) {
        return setRightButton(context.getString(text), listener);
    }

    /**
     * Set Right button ios dialog.
     *
     * @param text      the text
     * @param listener  the listener
     * @param isDismiss the is dismiss
     * @return the ios dialog
     */
    public InputDialog setRightButton(int text, final IDialogListener listener,
            final boolean isDismiss)
    {
        return setRightButton(context.getString(text), listener, isDismiss);
    }

    private void setLayout() {
        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }
        linearLayoutGroup.setGravity(gravity);
        txt_input.setGravity(gravity);
        linearLayoutGroup.setGravity(gravity);
        if (showRightBtn || showLeftBtn || showMiddleBtn) {//都没有
            mViewLineHorizontal.setVisibility(View.VISIBLE);
        }
        if (isCustomButtonStyle) {//设置过自定义样式不再控制
            if (showLeftBtn) {
                btn_left.setVisibility(View.VISIBLE);
            }
            if (showMiddleBtn) {
                btn_middle.setVisibility(View.VISIBLE);
            }
            if (showRightBtn) {
                btn_right.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (!showRightBtn && showLeftBtn && !showMiddleBtn) {//左一个
            btn_left.setVisibility(View.VISIBLE);
            btn_left.setBackgroundResource(R.drawable.alert_btn_single_selector);
            mViewLine.setVisibility(View.GONE);
            mViewLineRight.setVisibility(View.GONE);
        } else if (showRightBtn && !showLeftBtn & !showMiddleBtn) {//右一个
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setBackgroundResource(R.drawable.alert_btn_single_selector);
            mViewLine.setVisibility(View.GONE);
            mViewLineRight.setVisibility(View.GONE);
        } else if (!showRightBtn && !showLeftBtn & showMiddleBtn) {//中一个
            btn_middle.setVisibility(View.VISIBLE);
            btn_middle.setBackgroundResource(R.drawable.alert_btn_single_selector);
            mViewLine.setVisibility(View.GONE);
            mViewLineRight.setVisibility(View.GONE);
        } else if (showRightBtn && showLeftBtn && !showMiddleBtn) {//左右两个
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setBackgroundResource(R.drawable.alert_btn_right_selector);
            btn_left.setVisibility(View.VISIBLE);
            btn_left.setBackgroundResource(R.drawable.alert_btn_left_selector);
            mViewLine.setVisibility(View.VISIBLE);
            mViewLineRight.setVisibility(View.GONE);
        } else if (!showRightBtn && showLeftBtn && showMiddleBtn) {//左中两个
            btn_middle.setVisibility(View.VISIBLE);
            btn_middle.setBackgroundResource(R.drawable.alert_btn_right_selector);
            btn_left.setVisibility(View.VISIBLE);
            btn_left.setBackgroundResource(R.drawable.alert_btn_left_selector);
            mViewLine.setVisibility(View.VISIBLE);
            mViewLineRight.setVisibility(View.GONE);
        } else if (showRightBtn && !showLeftBtn && showMiddleBtn) {//中右两个
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setBackgroundResource(R.drawable.alert_btn_right_selector);
            btn_middle.setVisibility(View.VISIBLE);
            btn_middle.setBackgroundResource(R.drawable.alert_btn_left_selector);
            mViewLine.setVisibility(View.VISIBLE);
            mViewLineRight.setVisibility(View.GONE);
        } else if (showRightBtn && showLeftBtn && showMiddleBtn) {//三个
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setBackgroundResource(R.drawable.alert_btn_right_selector);
            btn_left.setVisibility(View.VISIBLE);
            btn_left.setBackgroundResource(R.drawable.alert_btn_left_selector);
            btn_middle.setVisibility(View.VISIBLE);
            btn_middle.setBackgroundResource(R.drawable.alert_btn_middle_selector);
            mViewLine.setVisibility(View.VISIBLE);
            mViewLineRight.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Show.
     */
    public void show() {
        setLayout();
        if (!dialog.isShowing())
            dialog.show();
    }

    /**
     * Dismiss.
     */
    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.cancel();
            dialog.dismiss();
        }
    }
}
