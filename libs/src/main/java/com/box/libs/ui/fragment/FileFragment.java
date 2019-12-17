package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.R;
import com.box.libs.sandbox.Sandbox;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.ui.item.FileItem;
import com.box.libs.ui.item.TitleItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.FileUtil;
import com.box.libs.util.BoxUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by linjiang on 04/06/2018.
 */

public class FileFragment extends BaseListFragment {

    private File file;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        file = (File) getArguments().getSerializable(PARAM1);
        getToolbar().setTitle(file.getName());
        getToolbar().getMenu().add(0, 0, 0, R.string.pd_name_delete_key).setIcon(
                R.drawable.pd_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(CODE2)
                                 .title(R.string.pd_help_title)
                                 .message(R.string.pd_make_sure, true)
                                 .positiveButton(R.string.pd_ok)
                                 .negativeButton(R.string.pd_cancel)
                                 .show(FileFragment.this);
                }
                return true;
            }
        });
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE1) {
                refresh();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                        null);
            } else if (requestCode == CODE2) {
                FileUtil.deleteDirectory(file);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                        null);
                onBackPressed();
            }
        }
    }

    private void refresh() {
        List<File> files = Sandbox.getFiles(file);
        if (BoxUtils.isNotEmpty(files)) {
            List<BaseItem> data = new ArrayList<>();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d FILES", files.size())));
            for (int i = 0; i < files.size(); i++) {
                data.add(new FileItem(files.get(i)));
            }
            getAdapter().setItems(data);
            getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, BaseItem item) {
                    Bundle bundle = new Bundle();
                    if (item instanceof FileItem) {
                        bundle.putSerializable(PARAM1, (File) item.data);
                        if (((File) item.data).isDirectory()) {
                            launch(FileFragment.class, bundle, CODE1);
                        } else {
                            launch(FileAttrFragment.class, bundle, CODE1);
                        }
                    }
                }
            });
        } else {
            showError(null);
        }
    }
}
