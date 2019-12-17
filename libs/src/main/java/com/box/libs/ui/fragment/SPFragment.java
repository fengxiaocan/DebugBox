package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.ui.item.KeyValueItem;
import com.box.libs.ui.item.TitleItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.ui.view.MenuRecyclerView;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.SimpleTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by linjiang on 05/06/2018.
 */

public class SPFragment extends BaseListFragment {

    private File descriptor;
    private String clickKey;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descriptor = (File) getArguments().getSerializable(PARAM1);
        getToolbar().setTitle(descriptor.getName());
        getToolbar().getMenu()
                    .add(0, 0, 0, R.string.pd_name_help)
                    .setIcon(R.drawable.pd_help)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(-1).title(R.string.pd_help_title).message(
                            R.string.pd_help_sp).positiveButton(R.string.pd_ok).show(
                            SPFragment.this);
                }
                return false;
            }
        });

        registerForContextMenu(getRecyclerView());
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof KeyValueItem) {
                    if (((KeyValueItem) item).isTitle) {
                        return;
                    }
                    clickKey = ((KeyValueItem) item).data[0];
                    Bundle bundle = new Bundle();
                    bundle.putString(PARAM1, ((KeyValueItem) item).data[1]);
                    launch(EditFragment.class, bundle, CODE1);
                }
            }
        });

        loadData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (menuInfo instanceof MenuRecyclerView.RvContextMenuInfo) {
            MenuRecyclerView.RvContextMenuInfo info = (MenuRecyclerView.RvContextMenuInfo) menuInfo;
            if (getAdapter().getItems().get(info.position) instanceof KeyValueItem) {
                if (!((KeyValueItem) getAdapter().getItems().get(info.position)).isTitle) {
                    menu.add(-1, 0, 0, R.string.pd_name_copy_value);
                    menu.add(-1, 0, 1, R.string.pd_name_delete_key);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getMenuInfo() instanceof MenuRecyclerView.RvContextMenuInfo) {
            MenuRecyclerView.RvContextMenuInfo info =
                    (MenuRecyclerView.RvContextMenuInfo) item.getMenuInfo();
            BaseItem baseItem = getAdapter().getItems().get(info.position);

            if (baseItem instanceof KeyValueItem) {
                KeyValueItem keyValueItem = (KeyValueItem) baseItem;

                if (keyValueItem.isTitle) {
                    return true;
                }

                if (item.getOrder() == 0) {
                    BoxUtils.copy2ClipBoard("KEY :: " + keyValueItem.data[0] + "\nVALUE  :: " +
                                            keyValueItem.data[1]);
                    return true;
                } else if (item.getOrder() == 1) {
                    String clickedKey = keyValueItem.data[0];
                    DebugBox.get().getSharedPref().removeSharedPrefKey(descriptor, clickedKey);
                    getAdapter().removeItem(info.position);
                    return true;
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    private void loadData() {
        Map<String, String> contents = DebugBox.get().getSharedPref().getSharedPrefContent(
                descriptor);
        if (contents != null && !contents.isEmpty()) {
            List<BaseItem> data = new ArrayList<>();
            data.add(
                    new TitleItem(String.format(Locale.getDefault(), "%d ITEMS", contents.size())));
            data.add(new KeyValueItem(new String[]{"KEY", "VALUE"}, true));
            for (Map.Entry<String, String> entry : contents.entrySet()) {
                data.add(new KeyValueItem(new String[]{entry.getKey(), entry.getValue()}, false,
                        true));
            }
            getAdapter().setItems(data);

        } else {
            showError(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            final String value = data.getStringExtra("value");
            if (!TextUtils.isEmpty(clickKey)) {

                new SimpleTask<>(new SimpleTask.Callback<Void, String>() {
                    @Override
                    public String doInBackground(Void[] params) {
                        return DebugBox.get().getSharedPref().updateSharedPref(descriptor, clickKey,
                                value);
                    }

                    @Override
                    public void onPostExecute(String result) {
                        hideLoading();
                        if (TextUtils.isEmpty(result)) {
                            BoxUtils.toast(R.string.pd_success);
                        } else {
                            BoxUtils.toast(result);
                        }
                        loadData();
                    }
                }).execute();
                showLoading();
            }
        }
    }

}
