package com.box.libs.web;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.function.IFunc;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.ui.view.WebConfigView;
import com.box.libs.util.ViewKnife;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class IWebFunc<W> implements IFunc, IWebIntercept<W> {
    private final WebController<W> webController;
    private final Class<W> beanClass;
    private W webView;
    private IBoxVisibleListener iBoxVisibleListener;

    public IWebFunc() {
        webController = new WebController<W>();
        ParameterizedType parameterizedType =
                (ParameterizedType) this.getClass().getGenericSuperclass();
        beanClass = (Class<W>) parameterizedType.getActualTypeArguments()[0];

        List<IFunc> iFuncs = getFuncs();
        if (iFuncs != null) {
            for (IFunc iFunc : iFuncs) {
                webController.addFunc(iFunc);
            }
        }
    }

    @Override
    public int getIcon() {
        return R.drawable.pd_browser;
    }

    @Override
    public String getName() {
        return ViewKnife.getString(R.string.pd_name_webview);
    }

    public final void setBoxVisibleListener(IBoxVisibleListener listener) {
        this.iBoxVisibleListener = listener;
    }

    @Override
    public final boolean onClick() {
        webView = null;
        Activity topActivity = DebugBox.get().getTopActivity();
        View decor = ViewKnife.tryGetTheFrontView(topActivity);
        if (decor != null) {
            W web = findWebView(decor);
            if (web != null) {
                webView = web;
                if (iBoxVisibleListener != null) {
                    iBoxVisibleListener.hideBox();
                }
                webController.init(DebugBox.get().getTopActivity(), webView, this);
                webController.onCloseListener(new WebConfigView.OnCloseListener() {
                    @Override
                    public void onClose() {
                        if (iBoxVisibleListener != null) {
                            iBoxVisibleListener.showBox();
                        }
                    }
                });
                webController.open();
            } else {
                if (topActivity instanceof AppCompatActivity) {
                    GeneralDialog.build(-1).title(R.string.pd_help_title).message(
                            R.string.pd_help_no_webview).positiveButton(R.string.pd_ok).show(
                            (AppCompatActivity) topActivity);
                } else {
                    Toast.makeText(topActivity, topActivity.getString(R.string.pd_help_no_webview),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }

    private W findWebView(View view) {
        if (view == null) {
            return null;
        } else if (view.getClass().equals(beanClass)) {
            return (W) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                W webView = findWebView(group.getChildAt(i));
                if (webView != null) {
                    return webView;
                }
            }
        }
        return null;
    }

    protected final W getWebView() {
        return webView;
    }

    public abstract List<IFunc> getFuncs();
}
