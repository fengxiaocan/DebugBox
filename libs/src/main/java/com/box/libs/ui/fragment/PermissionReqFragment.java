package com.box.libs.ui.fragment;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.ui.GeneralDialog;

import java.util.logging.Logger;

/**
 * Created by linjiang on 2019/3/5.
 */

public class PermissionReqFragment extends Fragment {

    private final int code = 0x10;
    private boolean isGoIntent = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        GeneralDialog.build(code)
                     .title(R.string.pd_permission_title)
                     .message(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                             R.string.pd_please_allow_permission : R.string.pd_permission_not_sure)
                     .positiveButton(R.string.pd_ok)
                     .negativeButton(R.string.pd_cancel)
                     .cancelable(false)
                     .show(this);
        isGoIntent = false;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isGoIntent){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getContext())) {
                    DebugBox.get().open();
                }
            }
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (code == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getContext())) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            isGoIntent = true;
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            getActivity().finish();
                        }
                    }else {
                        getActivity().finish();
                    }
                }else {
                    getActivity().finish();
                }
            }else {
                getActivity().finish();
            }
//           getActivity().finish();
        }
    }
}
