package com.box.libs;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import com.box.libs.function.IFunc;
import com.box.libs.ui.Dispatcher;
import com.box.libs.ui.InputDialog;
import com.box.libs.ui.InputResultListener;
import com.box.libs.ui.connector.Type;
import com.box.libs.ui.view.WebConfigView;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by linjiang on 2019/3/4.
 */

class WebController<W>
        implements WebConfigView.OnItemClickListener, Application.ActivityLifecycleCallbacks {

    public static final String JAVA_SCRIPT_NAME = "java_obj";
    public static final String SHOW_SOURCE_JS =
            "javascript:window.java_obj.showSource(document.documentElement.outerHTML);";
    public static final String SHOW_BODY_JS =
            "javascript:window.java_obj.showBody(document.body.innerHTML);";

    private final WebConfigView webConfigView;
    private final List<IFunc> functions = new ArrayList<>();
    private final LinkedHashSet<String> hashSet = new LinkedHashSet<>();
    private Activity currentActivity;
    private IWebFunc<W> wiWebFunc;
    private String url;
    private String search;

    WebController(IWebFunc<W> wiWebFunc) {
        this.wiWebFunc = wiWebFunc;
        Application app = BoxUtils.getApplication();
        app.registerActivityLifecycleCallbacks(this);
        webConfigView = new WebConfigView(app);
        webConfigView.setOnItemClickListener(this);
        addDefaultFunctions();
    }

    void init(Activity activity) {
        this.currentActivity = activity;
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
        showOverlay();
        boolean succeed = webConfigView.open();
        if (!succeed) {
            Dispatcher.start(BoxUtils.getApplication(), Type.PERMISSION);
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
        if (FuncController.isOpen()) {
            //只有在打开工具箱的时候,才能够执行
            String tag = activity.toString();
            if (!hashSet.contains(tag)) {
                //防止多次调用
                hashSet.add(tag);
                wiWebFunc.findWebView(activity);
            }
            if (activity == currentActivity) {
                showOverlay();
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (FuncController.isOpen()) {
            //只有在打开工具箱的时候,才能够执行
            if (activity == currentActivity) {
                hideOverlay();
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (FuncController.isOpen()) {
            //只有在打开工具箱的时候,才能够执行
            if (activity == currentActivity) {
                showOverlay();
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (FuncController.isOpen()) {
            //只有在打开工具箱的时候,才能够执行
            if (activity == currentActivity) {
                hideOverlay();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        hashSet.remove(activity.toString());
        if (FuncController.isOpen()) {
            //只有在打开工具箱的时候,才能够执行
            if (activity == currentActivity) {
                close();
                currentActivity = null;
            }
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
                wiWebFunc.goBack();
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
                wiWebFunc.reload();
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
                wiWebFunc.goForward();
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
                wiWebFunc.clearCache();
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
                                    wiWebFunc.loadUrl(result);
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
                                    if (result != null) {
                                        if (result.startsWith("javascript:")) {
                                            wiWebFunc.addJs(result);
                                        } else {
                                            wiWebFunc.addJs("javascript:" + result);
                                        }
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
                wiWebFunc.addJs(SHOW_SOURCE_JS);
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
                wiWebFunc.addJs(SHOW_BODY_JS);
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
                                    wiWebFunc.findAll(result);
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
                                    wiWebFunc.findAllAsync(result);
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
                wiWebFunc.findNext();
                return false;
            }
        });
    }

}
