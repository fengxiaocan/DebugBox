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
import com.box.libs.util.BoxUtils;
import com.box.libs.util.Config;
import com.box.libs.util.SensorDetector;
import com.box.libs.web.IBoxVisibleListener;
import com.box.libs.web.IWebFunc;

/**
 * Created by linjiang on 29/05/2018.
 */
public final class DebugBox /*extends FileProvider*/ {
    private static DebugBox INSTANCE;
    private static SensorDetector sensorDetector;
    private boolean notHostProcess;
    private OkHttpInterceptor interceptor;
    private Databases databases;
    private SharedPref sharedPref;
    private AttrFactory attrFactory;
    private CrashHandler crashHandler;
    private HistoryRecorder historyRecorder;
    private FuncController funcController;

    private DebugBox() {
        Log.e("noah", "DebugBox init");
    }

    public static DebugBox get() {
        if (INSTANCE == null) {
            // Not the host process
            DebugBox debugBox = new DebugBox();
            debugBox.init();
            //            debugBox.onCreate();
            INSTANCE = debugBox;
        }
        return INSTANCE;
    }

    //    @Override
    //    public boolean onCreate() {
    //        INSTANCE = this;
    //        Context context = BoxUtils.makeContextSafe(getContext());
    //        init(((Application) context));
    //        return super.onCreate();
    //    }

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

    public static void init(Application app,boolean openSensor) {
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
     * @param func
     * @param <W>
     */
    public<W> void addWebFunction(IWebFunc<W> func) {
        func.setBoxVisibleListener(new IBoxVisibleListener() {
            @Override
            public void showBox() {
                funcController.showOverlay();
            }

            @Override
            public void hideBox() {
                funcController.hideOverlay();
            }
        });
        funcController.addFunc(func);
    }

    /**
     * Open the panel.
     */
    public void open() {
        if (notHostProcess) {
            return;
        }
        notHostProcess = true;
        funcController.open();
    }

    /**
     * 开启摇一摇
     *
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
