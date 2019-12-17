package com.box.libs.web;

public interface IWebIntercept<W> {

    void onIntercept(W web);

    void addJavascriptInterface(W web, Object var1, String var2);

    /**
     * 添加js
     *
     * @param js
     */
    void addJs(W web, String js);

    /**
     * 前进
     */
    void goForward(W web);

    void reload(W web);

    /**
     * 后退
     */
    void goBack(W web);

    void loadUrl(W web, String url);

    void loadData(W web, String content);

    void findAllAsync(W web, String val);

    void findAll(W web, String val);

    void findNext(W web);

    void clearCache(W web);
}
