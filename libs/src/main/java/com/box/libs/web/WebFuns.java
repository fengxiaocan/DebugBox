package com.box.libs.web;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.function.IFunc;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.ui.view.WebConfigView;
import com.box.libs.util.ViewKnife;

public class WebFuns implements IFunc {
    private WebController webController;

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
        if (webController != null) {
            webController = null;
        }
        Activity topActivity = DebugBox.get().getTopActivity();
        View decor = ViewKnife.tryGetTheFrontView(topActivity);
        if (decor != null) {
            WebView webView = WebController.findWebView(decor);
            if (webView != null) {
//                hideOverlay();
                webController = new WebController(topActivity, webView);
                webController.onCloseListener(new WebConfigView.OnCloseListener() {
                    @Override
                    public void onClose() {
//                        showOverlay();
                    }
                });
                webController.open();
            } else {
                if (topActivity instanceof AppCompatActivity) {
                    GeneralDialog.build(-1)
                                 .title(R.string.pd_help_title)
                                 .message(R.string.pd_help_no_webview)
                                 .positiveButton(R.string.pd_ok)
                                 .show((AppCompatActivity) topActivity);
                } else {
                    Toast.makeText(topActivity,
                            topActivity.getString(R.string.pd_help_no_webview),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }


}
