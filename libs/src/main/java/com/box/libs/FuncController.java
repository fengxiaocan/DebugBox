package com.box.libs;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import com.box.libs.function.IFunc;
import com.box.libs.inspector.CurInfoView;
import com.box.libs.inspector.GridLineView;
import com.box.libs.ui.Dispatcher;
import com.box.libs.ui.connector.Type;
import com.box.libs.ui.view.FuncView;
import com.box.libs.util.Utils;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2019/3/4.
 */

class FuncController
        implements Application.ActivityLifecycleCallbacks, FuncView.OnItemClickListener {

    private final FuncView funcView;
    private final CurInfoView curInfoView;
    private final GridLineView gridLineView;
    private final List<IFunc> functions = new ArrayList<>();
    private int activeCount;

    FuncController(Application app) {
        funcView = new FuncView(app);
        funcView.setOnItemClickListener(this);
        curInfoView = new CurInfoView(app);
        gridLineView = new GridLineView(app);
        app.registerActivityLifecycleCallbacks(this);
        addDefaultFunctions();
    }

    void addFunc(IFunc func) {
        if (!functions.contains(func)) {
            functions.add(func);
            funcView.addItem(func.getIcon(), func.getName());
        }
    }

    void open() {
        if (funcView.isVisible()) {
            boolean succeed = funcView.open();
            if (!succeed) {
                Dispatcher.start(Utils.getApplication(), Type.PERMISSION);
            }
        }
    }

    void close() {
        funcView.close();
    }

    private void showOverlay() {
        funcView.setVisibility(View.VISIBLE);
        curInfoView.setVisibility(View.VISIBLE);
        gridLineView.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        funcView.setVisibility(View.GONE);
        curInfoView.setVisibility(View.GONE);
        gridLineView.setVisibility(View.GONE);
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
        activeCount++;
        if (activeCount == 1) {
            showOverlay();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof Dispatcher) {
            hideOverlay();
        }
        curInfoView.updateText(activity.getClass().getName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof Dispatcher) {
            if (activeCount > 0) {
                showOverlay();
            }
        }
        curInfoView.updateText(null);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeCount--;
        if (activeCount <= 0) {
            hideOverlay();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void addDefaultFunctions() {
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_network;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_network);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.NET);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_disk;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_sandbox);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.FILE);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_select;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_select);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.SELECT);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_bug;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_crash);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.BUG);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_layer;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_layer);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.HIERARCHY);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_ruler;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_baseline);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.BASELINE);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_map;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_navigate);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.ROUTE);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_history;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_history);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.HISTORY);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_windows;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_activity);
            }

            @Override
            public boolean onClick() {
                boolean isOpen = curInfoView.isOpen();
                curInfoView.toggle();
                return !isOpen;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_grid;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_gridline);
            }

            @Override
            public boolean onClick() {
                boolean isOpen = gridLineView.isOpen();
                gridLineView.toggle();
                return !isOpen;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_config;
            }

            @Override
            public String getName() {
                return ViewKnife.getString(R.string.pd_name_config);
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getApplication(), Type.CONFIG);
                return false;
            }
        });
    }
}
