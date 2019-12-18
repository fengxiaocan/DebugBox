package com.box.libs;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.box.libs.function.IFunc;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.util.ViewKnife;
import com.box.libs.web.IFuncOnClickListener;
import com.box.libs.web.IWebIntercept;
import com.box.libs.web.InJavaScriptObj;

import java.util.List;

import static com.box.libs.WebController.JAVA_SCRIPT_NAME;

public abstract class IWebFunc<W> implements IFunc, IWebIntercept<W> {

    private W webView;
    private IFuncOnClickListener listener;

    @Override
    public int getIcon() {
        return R.drawable.pd_browser;
    }

    @Override
    public String getName() {
        return ViewKnife.getString(R.string.pd_name_webview);
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
                if (listener != null) {
                    listener.onOpen();
                }
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

    final void findWebView(Activity activity) {
        View decor = ViewKnife.tryGetTheFrontView(activity);
        if (decor != null) {
            W web = findWebView(decor);
            if (web != null) {
                onIntercept(web);
                addJavascriptInterface(web, new InJavaScriptObj<W>(this, web), JAVA_SCRIPT_NAME);
            }
        }
    }

    private W findWebView(View view) {
        if (view == null) {
            return null;
        } else if (isWebView(view)) {
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

    protected abstract boolean isWebView(View view);

    protected final W getWebView() {
        return webView;
    }

    public abstract List<IFunc> getFuncs();

    final void setOnOpenListener(IFuncOnClickListener iFuncOnClickListener) {
        this.listener = iFuncOnClickListener;
    }

    void addJs(String js) {
        if (webView != null) {
            addJs(webView, js);
        }
    }

    void goForward() {
        if (webView != null) {
            goForward(webView);
        }
    }

    void reload() {
        if (webView != null) {
            reload(webView);
        }
    }

    void goBack() {
        if (webView != null) {
            goBack(webView);
        }
    }

    void loadUrl(String url) {
        if (webView != null) {
            loadUrl(webView, url);
        }
    }

    void loadData(String content) {
        if (webView != null) {
            loadData(webView, content);
        }

    }

    void findAllAsync(String val) {
        if (webView != null) {
            findAllAsync(webView, val);
        }
    }

    void findAll(String val) {
        if (webView != null) {
            findAll(webView, val);
        }
    }

    void findNext() {
        if (webView != null) {
            findNext(webView);
        }
    }

    void clearCache() {
        if (webView != null) {
            clearCache(webView);
        }
    }
    void zoomInFont() {
        if (webView != null) {
            zoomInFont(webView);
        }
    }
    void zoomOutFont() {
        if (webView != null) {
            zoomOutFont(webView);
        }
    }
}
