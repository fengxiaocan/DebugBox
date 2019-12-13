package com.box.libs.ui.fragment;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.R;
import com.box.libs.cache.History;
import com.box.libs.ui.item.KeyValueItem;
import com.box.libs.ui.item.TitleItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.util.SimpleTask;
import com.box.libs.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2019/3/4.
 */

public class HistoryFragment extends BaseListFragment {

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Activity History");
        getToolbar().getMenu().add(-1, 0, 0, R.string.pd_name_delete_key).setIcon(
                R.drawable.pd_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                History.clear();
                getAdapter().clearItems();
                Utils.toast(R.string.pd_success);
                return true;
            }
        });
        loadData();
    }

    private void loadData() {
        hideError();
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<History>>() {
            @Override
            public List<History> doInBackground(Void[] params) {
                return History.query();
            }

            @Override
            public void onPostExecute(List<History> result) {
                hideLoading();
                List<BaseItem> data = new ArrayList<>(result.size());
                if (Utils.isNotEmpty(result)) {
                    data.add(new TitleItem("Task"));
                    for (History history : result) {
                        String[] value = new String[2];
                        value[0] = history.activity;
                        value[1] = history.event;
                        data.add(new KeyValueItem(value, false, false,
                                Utils.millis2String(history.createTime, Utils.HHMMSS)));
                    }
                    getAdapter().setItems(data);
                } else {
                    showError(null);
                }
            }
        }).execute();
    }
}
