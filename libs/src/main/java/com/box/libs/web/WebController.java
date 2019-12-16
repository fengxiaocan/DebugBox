package com.box.libs.web;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.box.libs.R;
import com.box.libs.function.IFunc;
import com.box.libs.ui.Dispatcher;
import com.box.libs.ui.connector.Type;
import com.box.libs.ui.view.WebConfigView;
import com.box.libs.util.Utils;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2019/3/4.
 */

class WebController
        implements WebConfigView.OnItemClickListener, Application.ActivityLifecycleCallbacks {

    private final WebConfigView webConfigView;
    private WebView webView;
    private Activity currentActivity;
    private final List<IFunc> functions = new ArrayList<>();

    WebController(Activity app,WebView web) {
        currentActivity = app;
        webView = web;
        webConfigView = new WebConfigView(app);
        webConfigView.setOnItemClickListener(this);
        app.getApplication().registerActivityLifecycleCallbacks(this);
        addDefaultFunctions();
    }

    void addFunc(IFunc func) {
        if (!functions.contains(func)) {
            functions.add(func);
            webConfigView.addItem(func.getIcon(), func.getName());
        }
    }
    void onCloseListener(WebConfigView.OnCloseListener onCloseListener) {
        webConfigView.setOnCloseListener(onCloseListener);
    }

    void open() {
        if (webConfigView.isVisible()) {
            boolean succeed = webConfigView.open();
            if (!succeed) {
                Dispatcher.start(Utils.getApplication(), Type.PERMISSION);
            }
        }
    }

    void close() {
        webConfigView.close();
    }

    private void showOverlay() {
        webConfigView.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        webConfigView.setVisibility(View.GONE);
    }

    @Override
    public boolean onItemClick(int index) {
        return functions.get(index).onClick();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity == currentActivity) {
            showOverlay();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity == currentActivity) {
            hideOverlay();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity == currentActivity) {
            showOverlay();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity == currentActivity) {
            hideOverlay();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity == currentActivity) {
            if (currentActivity != null) {
                close();
                currentActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
                currentActivity = null;
                webView = null;
            }
        }
    }

    private void addDefaultFunctions() {
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_browser;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_webview);
            }

            @Override
            public boolean onClick() {
                hideOverlay();
                new WebConfigView(webConfigView.getContext()).open();
                return false;
            }
        });
    }

    static WebView findWebView(View view) {
        if (view == null) {
            return null;
        }
        if (view instanceof WebView) {
            return (WebView) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                WebView webView = findWebView(group.getChildAt(i));
                if (webView != null) {
                    return webView;
                }
            }
        }
        return null;
    }

}
