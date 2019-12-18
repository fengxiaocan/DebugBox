package com.box.libs.web;

import android.webkit.JavascriptInterface;

import com.box.libs.util.BoxUtils;

public final class InJavaScriptObj<W> {
    private IWebIntercept<W> intercept;
    private W webView;

    public InJavaScriptObj(IWebIntercept<W> intercept, W webView) {
        this.intercept = intercept;
        this.webView = webView;
    }

    @JavascriptInterface
    public void showSource(String html) {
        BoxUtils.toastSafe("正在格式化...");
        final String allHtml = "<xmp>" + new HtmlParser(html).parser() + "</xmp>";

        if (intercept != null && webView != null) {
            BoxUtils.post(new Runnable() {
                @Override
                public void run() {
                    if (intercept != null && webView != null) {
                        intercept.loadData(webView, allHtml);
                    }
                }
            });
        }
    }

    @JavascriptInterface
    public void showBody(String html) {
        BoxUtils.toastSafe("正在格式化...");
        final String bodyHtml = "<xmp>" + new HtmlParser(html).parser() + "</xmp>";
        if (intercept != null && webView != null) {
            BoxUtils.post(new Runnable() {
                @Override
                public void run() {
                    if (intercept != null && webView != null) {
                        intercept.loadData(webView, bodyHtml);
                    }
                }
            });
        }
    }
}
