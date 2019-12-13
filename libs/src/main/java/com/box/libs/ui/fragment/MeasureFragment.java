package com.box.libs.ui.fragment;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.R;
import com.box.libs.inspector.BaseLineView;
import com.box.libs.ui.GeneralDialog;

/**
 * Created by linjiang on 2019/3/5.
 */

public class MeasureFragment extends BaseFragment {

    @Override
    protected Toolbar onCreateToolbar() {
        return null;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        return new BaseLineView(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        GeneralDialog.build(-1)
                     .title(R.string.pd_help_title)
                     .message(R.string.pd_help_baseline)
                     .positiveButton(R.string.pd_ok)
                     .show(this);
    }


}
