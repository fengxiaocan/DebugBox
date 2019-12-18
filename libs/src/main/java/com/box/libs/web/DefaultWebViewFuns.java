package com.box.libs.web;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;

import com.box.libs.IWebFunc;
import com.box.libs.function.IFunc;

import java.util.List;

public class DefaultWebViewFuns extends IWebFunc<WebView> {

    @Override
    protected boolean isWebView(View view) {
        return view instanceof WebView;
    }

    @Override
    public List<IFunc> getFuncs() {
        return null;
    }

    public void onIntercept(WebView web) {
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(WebView web, Object var1, String var2) {
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(var1, var2);
    }

    @Override
    public void addJs(WebView web, String js) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web.evaluateJavascript(js, null);
        } else {
            web.loadUrl(js);
        }
    }

    @Override
    public void goForward(WebView web) {
        web.goForward();
    }

    @Override
    public void reload(WebView web) {
        web.reload();
    }

    @Override
    public void goBack(WebView web) {
        web.goBack();
    }

    @Override
    public void loadUrl(WebView web, String url) {
        web.loadUrl(url);
    }

    @Override
    public void loadData(WebView web, String content) {
        web.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    @Override
    public void findAllAsync(WebView web, String val) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            web.findAllAsync(val);
        }
    }

    @Override
    public void findAll(WebView web, String val) {
        web.findAll(val);
    }

    @Override
    public void findNext(WebView web) {
        web.findNext(true);
    }

    @Override
    public void clearCache(WebView web) {
        web.clearCache(true);
    }

}
