package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.sandbox.Sandbox;
import com.box.libs.ui.item.DBItem;
import com.box.libs.ui.item.FileItem;
import com.box.libs.ui.item.SPItem;
import com.box.libs.ui.item.TitleItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.Config;
import com.box.libs.util.SimpleTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 01/06/2018.
 */

public class SandboxFragment extends BaseListFragment {

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_sandbox);

        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                Bundle bundle = new Bundle();
                if (item instanceof DBItem) {
                    bundle.putInt(PARAM1, ((DBItem) item).key);
                    launch(DBFragment.class, (String) item.data, bundle);
                } else if (item instanceof SPItem) {
                    bundle.putSerializable(PARAM1, ((SPItem) item).descriptor);
                    launch(SPFragment.class, bundle);
                } else if (item instanceof FileItem) {
                    bundle.putSerializable(PARAM1, (File) item.data);
                    if (((File) item.data).isDirectory()) {
                        launch(FileFragment.class, bundle, CODE1);
                    } else {
                        launch(FileAttrFragment.class, bundle);
                    }
                }
            }
        });

        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE1) {
                loadData();
            }
        }
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<BaseItem>>() {
            @Override
            public List<BaseItem> doInBackground(Void[] params) {
                SparseArray<String> databaseNames =
                        DebugBox.get().getDatabases().getDatabaseNames();
                List<BaseItem> data = new ArrayList<>(databaseNames.size());
                data.add(new TitleItem(getString(R.string.pd_name_database)));
                for (int i = 0; i < databaseNames.size(); i++) {
                    data.add(new DBItem(databaseNames.valueAt(i), databaseNames.keyAt(i)));
                }
                data.add(new TitleItem(getString(R.string.pd_name_sp)));
                List<File> spFiles = DebugBox.get().getSharedPref().getSharedPrefDescs();
                for (int i = 0; i < spFiles.size(); i++) {
                    data.add(new SPItem(spFiles.get(i).getName(), spFiles.get(i)));
                }

                data.add(new TitleItem(getString(R.string.pd_name_file)));

                List<File> descriptors = Sandbox.getRootFiles();
                for (int i = 0; i < descriptors.size(); i++) {
                    data.add(new FileItem(descriptors.get(i)));
                }

                if (Config.getSANDBOX_DPM() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    data.add(new TitleItem("Device-protect-mode Files"));
                    List<File> dpm = Sandbox.getDPMFiles();
                    for (int i = 0; i < dpm.size(); i++) {
                        data.add(new FileItem(dpm.get(i)));
                    }
                }

                return data;
            }

            @Override
            public void onPostExecute(List<BaseItem> result) {
                hideLoading();
                getAdapter().setItems(result);
            }
        }).execute();


    }
}
