package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.box.libs.R;
import com.box.libs.ui.item.RouteItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.util.BoxUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2019/1/13.
 */

public class RouteFragment extends BaseListFragment {

    private final RouteItem.Callback callback = new RouteItem.Callback() {
        @Override
        public void onClick(String simpleName, String clazz, boolean needParam) {
            if (needParam) {
                Bundle bundle = new Bundle();
                bundle.putString(PARAM1, simpleName);
                bundle.putString(PARAM2, clazz);
                launch(RouteParamFragment.class, bundle, CODE1);
                return;
            }
            try {
                Intent intent = new Intent(getContext(), Class.forName(clazz));
                go(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_navigate);
        List<String> activities = BoxUtils.getActivities();
        List<BaseItem> data = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            data.add(new RouteItem(activities.get(i), callback));
        }
        getAdapter().setItems(data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            go(data);
        }
    }

    private void go(Intent intent) {
        startActivity(intent);
        getActivity().finish();
    }

}
