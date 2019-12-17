package com.box.libs.web;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.box.libs.R;
import com.box.libs.function.IFunc;
import com.box.libs.ui.Dispatcher;
import com.box.libs.ui.InputDialog;
import com.box.libs.ui.InputResultListener;
import com.box.libs.ui.connector.Type;
import com.box.libs.ui.view.WebConfigView;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2019/3/4.
 */

class WebController<W>
        implements WebConfigView.OnItemClickListener, Application.ActivityLifecycleCallbacks {
    private static final String JAVA_SCRIPT_NAME = "java_obj";
    private static final String SHOW_SOURCE_JS =
            "javascript:window.java_obj.showSource(document.documentElement.outerHTML);";
    private static final String SHOW_BODY_JS =
            "javascript:window.java_obj.showBody(document.body.innerHTML);";
    private final WebConfigView webConfigView;
    private final List<IFunc> functions = new ArrayList<>();
    private Activity currentActivity;
    private W webView;
    private IWebIntercept<W> iWebIntercept;
    private String url;
    private String search;

    WebController() {
        Application app = BoxUtils.getApplication();
        app.registerActivityLifecycleCallbacks(this);
        webConfigView = new WebConfigView(app);
        webConfigView.setOnItemClickListener(this);
        addDefaultFunctions();
    }

    void init(Activity activity, W view, IWebIntercept<W> intercept) {
        this.currentActivity = activity;
        this.webView = view;
        this.iWebIntercept = intercept;
        if (iWebIntercept != null) {
            iWebIntercept.onIntercept(webView);
            iWebIntercept.addJavascriptInterface(webView, new InJavaScriptObj(), JAVA_SCRIPT_NAME);
            //            iWebIntercept.reload(webView);
        }
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
                Dispatcher.start(BoxUtils.getApplication(), Type.PERMISSION);
            }
        }
    }

    private void close() {
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
            close();
            currentActivity = null;
            webView = null;
        }
    }

    private void addDefaultFunctions() {
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_back;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_go_back);
            }

            @Override
            public boolean onClick() {
                if (iWebIntercept != null && webView != null) {
                    iWebIntercept.goBack(webView);
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_forward;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_go_forward);
            }

            @Override
            public boolean onClick() {
                if (iWebIntercept != null && webView != null) {
                    iWebIntercept.goForward(webView);
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_refresh;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_go_refresh);
            }

            @Override
            public boolean onClick() {
                if (iWebIntercept != null && webView != null) {
                    iWebIntercept.reload(webView);
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_clear;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_clear);
            }

            @Override
            public boolean onClick() {
                if (iWebIntercept != null && webView != null) {
                    iWebIntercept.clearCache(webView);
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_input_url;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_add_url);
            }

            @Override
            public boolean onClick() {
                if (currentActivity != null) {
                    new InputDialog(currentActivity).setInputMessage(url).setInputResultListener(
                            new InputResultListener() {
                                @Override
                                public void onResult(String result) {
                                    url = result;
                                    if (iWebIntercept != null && webView != null) {
                                        iWebIntercept.loadUrl(webView, result);
                                    }
                                }
                            }).show();
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_js;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_add_js);
            }

            @Override
            public boolean onClick() {
                if (currentActivity != null) {
                    new InputDialog(currentActivity).setInputResultListener(
                            new InputResultListener() {
                                @Override
                                public void onResult(String result) {
                                    if (iWebIntercept != null && webView != null) {
                                        iWebIntercept.addJs(webView, result);
                                    }
                                }
                            }).show();
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_html;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_html);
            }

            @Override
            public boolean onClick() {
                if (iWebIntercept != null && webView != null) {
                    iWebIntercept.addJs(webView, SHOW_SOURCE_JS);
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_html5;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_body_html);
            }

            @Override
            public boolean onClick() {
                if (iWebIntercept != null && webView != null) {
                    iWebIntercept.addJs(webView, SHOW_BODY_JS);
                }
                return false;
            }
        });

        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_find1;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_body_find_all);
            }

            @Override
            public boolean onClick() {
                if (currentActivity != null) {
                    new InputDialog(currentActivity).setInputMessage(search).setInputResultListener(
                            new InputResultListener() {
                                @Override
                                public void onResult(String result) {
                                    search = result;
                                    if (iWebIntercept != null && webView != null) {
                                        iWebIntercept.findAll(webView, result);
                                    }
                                }
                            }).show();
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_find2;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_body_find_all_sync);
            }

            @Override
            public boolean onClick() {
                if (currentActivity != null) {
                    new InputDialog(currentActivity).setInputMessage(search).setInputResultListener(
                            new InputResultListener() {
                                @Override
                                public void onResult(String result) {
                                    search = result;
                                    if (iWebIntercept != null && webView != null) {
                                        iWebIntercept.findAllAsync(webView, result);
                                    }
                                }
                            }).show();
                }
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_find_next;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_body_find_next);
            }

            @Override
            public boolean onClick() {
                if (iWebIntercept != null && webView != null && search != null) {
                    iWebIntercept.findNext(webView);
                }
                return false;
            }
        });
    }

    public final class InJavaScriptObj {

        @JavascriptInterface
        public void showSource(String html) {
            BoxUtils.toastSafe("正在格式化...");
            final String allHtml = "<xmp>" + new HtmlParser(html).parser() + "</xmp>";

            if (iWebIntercept != null && webView != null) {
                BoxUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        if (iWebIntercept != null && webView != null) {
                            iWebIntercept.loadData(webView, allHtml);
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void showBody(String html) {
            BoxUtils.toastSafe("正在格式化...");
            final String bodyHtml = "<xmp>" + new HtmlParser(html).parser() + "</xmp>";
            if (iWebIntercept != null && webView != null) {
                BoxUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        if (iWebIntercept != null && webView != null) {
                            iWebIntercept.loadData(webView, bodyHtml);
                        }
                    }
                });
            }
        }
    }
}
