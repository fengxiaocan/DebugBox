package com.box.libs;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.box.libs.crash.CrashHandler;
import com.box.libs.database.Databases;
import com.box.libs.function.IFunc;
import com.box.libs.history.HistoryRecorder;
import com.box.libs.inspector.attribute.AttrFactory;
import com.box.libs.network.OkHttpInterceptor;
import com.box.libs.preference.SharedPref;
import com.box.libs.ui.view.WebConfigView;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.Config;
import com.box.libs.util.SensorDetector;
import com.box.libs.web.IFuncOnClickListener;

import java.util.List;

/**
 * Created by linjiang on 29/05/2018.
 */
public final class DebugBox<W> /*extends FileProvider*/ {
    private static DebugBox INSTANCE;
    private static SensorDetector sensorDetector;
    private static IWebFunc sWebFunc;
    private OkHttpInterceptor interceptor;
    private Databases databases;
    private SharedPref sharedPref;
    private AttrFactory attrFactory;
    private CrashHandler crashHandler;
    private HistoryRecorder historyRecorder;
    private FuncController funcController;
    private WebController<W> webController;

    private DebugBox() {
        Log.e("noah", "DebugBox init");
    }

    public static DebugBox get() {
        if (INSTANCE == null) {
            DebugBox debugBox = new DebugBox();
            debugBox.init();
            INSTANCE = debugBox;
        }
        return INSTANCE;
    }

    public static void init(Application app) {
        BoxUtils.init(app);
        //是否需要开启摇一摇
        if (Config.getSHAKE_SWITCH()) {
            sensorDetector = new SensorDetector(new SensorDetector.Callback() {
                @Override
                public void shakeValid() {
                    get().open();
                }
            });
        }
    }

    public static void init(Application app, boolean openSensor) {
        BoxUtils.init(app);
        //是否需要开启摇一摇
        Config.setSHAKE_SWITCH(openSensor);
        if (openSensor) {
            sensorDetector = new SensorDetector(new SensorDetector.Callback() {
                @Override
                public void shakeValid() {
                    get().open();
                }
            });
        }
    }

    private void init() {
        Application app = BoxUtils.getApplication();
        if (app == null) {
            throw new NullPointerException("DebugBox必须要注册init方法");
        }
        funcController = new FuncController(app);
        interceptor = new OkHttpInterceptor();
        databases = new Databases();
        sharedPref = new SharedPref();
        attrFactory = new AttrFactory();
        crashHandler = new CrashHandler(app);
        historyRecorder = new HistoryRecorder(app);

        if (sWebFunc != null) {
            //在初始化的时候,添加上
            funcController.addFunc(sWebFunc);
            //在初始化的时候,初始化控制器
            webController = new WebController<>(sWebFunc);
            //添加子类注册方法
            List<IFunc> iFuncs = sWebFunc.getFuncs();
            if (iFuncs != null) {
                for (IFunc iFunc : iFuncs) {
                    webController.addFunc(iFunc);
                }
            }
            //设置回调
            sWebFunc.setOnOpenListener(new IFuncOnClickListener() {
                @Override
                public void onOpen() {
                    funcController.hideOverlay();
                    webController.init(getTopActivity());
                    webController.onCloseListener(new WebConfigView.OnCloseListener() {
                        @Override
                        public void onClose() {
                            funcController.showOverlay();
                        }
                    });
                    webController.open();
                }
            });
        }
    }

    public OkHttpInterceptor getInterceptor() {
        return interceptor;
    }

    public Databases getDatabases() {
        return databases;
    }

    public SharedPref getSharedPref() {
        return sharedPref;
    }

    public AttrFactory getAttrFactory() {
        return attrFactory;
    }

    /**
     * @hide
     */
    public Activity getTopActivity() {
        return historyRecorder.getTopActivity();
    }

    /**
     * Add a custom entry to the panel.
     * also see @{@link com.box.libs.function.IFunc}
     *
     * @param func
     */
    public void addFunction(IFunc func) {
        funcController.addFunc(func);
    }

    /**
     * 添加浏览器的拦截
     *
     * @param func
     */
    public static<W> void registWebFunction(IWebFunc<W> func) {
        //懒加载的方式,先在box 中注册该方法函数
        sWebFunc = func;
    }

    /**
     * Open the panel.
     */
    public void open() {
        funcController.open();
    }

    /**
     * 开启摇一摇
     */
    public void openShakeValid() {
        Config.setSHAKE_SWITCH(true);
        if (sensorDetector == null) {
            sensorDetector = new SensorDetector(new SensorDetector.Callback() {
                @Override
                public void shakeValid() {
                    get().open();
                }
            });
        }
    }

    /**
     * Close the panel.
     */
    public void close() {
        funcController.close();
    }

    /**
     * Disable the Shake feature.
     */
    public void disableShakeSwitch() {
        sensorDetector.unRegister();
    }

}
