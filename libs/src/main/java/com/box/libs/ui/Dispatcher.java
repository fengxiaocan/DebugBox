package com.box.libs.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.box.libs.ui.connector.Type;
import com.box.libs.ui.connector.UIStateCallback;
import com.box.libs.ui.fragment.ConfigFragment;
import com.box.libs.ui.fragment.CrashFragment;
import com.box.libs.ui.fragment.HierarchyFragment;
import com.box.libs.ui.fragment.HistoryFragment;
import com.box.libs.ui.fragment.MeasureFragment;
import com.box.libs.ui.fragment.NetFragment;
import com.box.libs.ui.fragment.PermissionReqFragment;
import com.box.libs.ui.fragment.RouteFragment;
import com.box.libs.ui.fragment.SandboxFragment;
import com.box.libs.ui.fragment.ViewFragment;
import com.box.libs.util.ViewKnife;

/**
 * Created by linjiang on 30/05/2018.
 */

public class Dispatcher extends AppCompatActivity implements UIStateCallback {

    public static final String PARAM1 = "param1";
    private @Type
    int type;
    private View hintView;

    public static void start(Context context, @Type int type) {
        boolean needTrans = type == Type.BASELINE || type == Type.SELECT || type == Type.PERMISSION;
        Intent intent = new Intent(context,
                needTrans ? TransActivity.class : Dispatcher.class).putExtra(PARAM1, type);
        // This flag is very ridiculous in different android versions, like a bug
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra(PARAM1, Type.FILE);
        ViewKnife.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        ViewKnife.transStatusBar(getWindow());
        dispatch(savedInstanceState);
    }

    private void dispatch(Bundle savedInstanceState) {
        switch (type) {
            case Type.PERMISSION:
                if (savedInstanceState == null) {
                    addFragment(PermissionReqFragment.class);
                }
                break;
            case Type.BASELINE:
                if (savedInstanceState == null) {
                    addFragment(MeasureFragment.class);
                }
                break;
            case Type.SELECT:
                if (savedInstanceState == null) {
                    addFragment(ViewFragment.class);
                } else {
                    finish();
                }
                break;
            case Type.NET:
                if (savedInstanceState == null) {
                    addFragment(NetFragment.class);
                }
                break;
            case Type.FILE:
                if (savedInstanceState == null) {
                    addFragment(SandboxFragment.class);
                }
                break;
            case Type.BUG:
                if (savedInstanceState == null) {
                    addFragment(CrashFragment.class);
                }
                break;
            case Type.HISTORY:
                if (savedInstanceState == null) {
                    addFragment(HistoryFragment.class);
                }
                break;
            case Type.CONFIG:
                if (savedInstanceState == null) {
                    addFragment(ConfigFragment.class);
                }
                break;
            case Type.HIERARCHY:
                if (savedInstanceState == null) {
                    addFragment(HierarchyFragment.class);
                } else {
                    finish();
                }
                break;
            case Type.ROUTE:
                if (savedInstanceState == null) {
                    addFragment(RouteFragment.class);
                }
                break;
        }

    }

    private void addFragment(Class<? extends Fragment> clazz) {
        try {
            getSupportFragmentManager().beginTransaction().add(Window.ID_ANDROID_CONTENT,
                    clazz.newInstance()).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void showHint() {
        if (hintView == null) {
            hintView = new ProgressBar(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            hintView.setLayoutParams(params);
        }
        if (hintView.getParent() == null) {
            if (getWindow() != null) {
                if (getWindow().getDecorView() instanceof ViewGroup) {
                    ((ViewGroup) getWindow().getDecorView()).addView(hintView);
                }
            }
        }
        if (hintView.getVisibility() == View.GONE) {
            hintView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideHint() {
        if (hintView != null) {
            if (hintView.getVisibility() != View.GONE) {
                hintView.setVisibility(View.GONE);
            }
        }
    }
}
