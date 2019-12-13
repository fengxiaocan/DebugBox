package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.R;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.ui.item.ContentItem;
import com.box.libs.ui.item.TitleItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.FileUtil;
import com.box.libs.util.SimpleTask;
import com.box.libs.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 05/06/2018.
 */

public class FileAttrFragment extends BaseListFragment {

    private File file;

    @Override
    protected boolean needDefaultDivider() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        file = (File) getArguments().getSerializable(PARAM1);
        if (!file.exists()) {
            showError(null);
            return;
        }
        getToolbar().setTitle(file.getName());


        getToolbar().getMenu().add(-1, 0, 0, R.string.pd_name_open);
        getToolbar().getMenu().add(-1, 0, 1, R.string.pd_name_open_as_text);
        getToolbar().getMenu().add(-1, 0, 2, R.string.pd_name_rename);
        getToolbar().getMenu().add(-1, 0, 3, R.string.pd_name_delete_key);
        getToolbar().getMenu().add(-1, 0, 4, R.string.pd_name_copy_to_sdcard);
        getToolbar().getMenu()
                    .add(0, 0, 5, R.string.pd_name_help)
                    .setIcon(R.drawable.pd_help)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    tryOpen();
                } else if (item.getOrder() == 1) {
                    tryOpenAsText();
                } else if (item.getOrder() == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putString(PARAM1, file.getName());
                    launch(EditFragment.class, bundle, CODE1);
                } else if (item.getOrder() == 3) {
                    tryDel();
                } else if (item.getOrder() == 4) {
                    copyTo();
                } else if (item.getOrder() == 5) {
                    GeneralDialog.build(-1).title(R.string.pd_help_title).message(
                            R.string.pd_help_file).positiveButton(R.string.pd_ok).show(
                            FileAttrFragment.this);
                }
                return true;
            }
        });

        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof ContentItem) {
                    Utils.copy2ClipBoard((String) item.data);
                }
            }
        });
        loadData();
    }

    private void loadData() {
        List<BaseItem> data = new ArrayList<>();
        data.add(new TitleItem("NAME"));
        data.add(new ContentItem(file.getName()));
        data.add(new TitleItem("SIZE"));
        data.add(new ContentItem(FileUtil.fileSize(file)));
        data.add(new TitleItem("MODIFIED"));
        data.add(new ContentItem(Utils.millis2String(file.lastModified(), Utils.NO_MILLIS)));
        data.add(new TitleItem("AUTHORITY"));
        data.add(new ContentItem(
                String.format("X: %b    W: %b    R: %b", file.canExecute(), file.canWrite(),
                        file.canRead())));
        data.add(new TitleItem("HASH"));
        data.add(new ContentItem(
                FileUtil.bytesToHexString(String.valueOf(file.hashCode()).getBytes())));
        data.add(new TitleItem("TYPE"));
        String type = FileUtil.getFileType(file.getPath());
        data.add(new ContentItem(TextUtils.isEmpty(type) ? "other" : type));
        data.add(new TitleItem("PATH"));
        data.add(new ContentItem(file.getPath()));
        getAdapter().setItems(data);

        new SimpleTask<>(new SimpleTask.Callback<File, List<BaseItem>>() {
            @Override
            public List<BaseItem> doInBackground(File[] params) {
                List<BaseItem> data = new ArrayList<>();
                data.add(new TitleItem("MD5"));
                data.add(new ContentItem(FileUtil.md5File(params[0])));
                return data;
            }

            @Override
            public void onPostExecute(List<BaseItem> result) {
                if (Utils.isNotEmpty(result)) {
                    getAdapter().insertItems(result, 10);
                }
            }
        }).execute(file);
    }

    private void copyTo() {
        new SimpleTask<>(new SimpleTask.Callback<File, String>() {
            @Override
            public String doInBackground(File[] params) {
                String result = FileUtil.fileCopy2Tmp(params[0]);
                return result;
            }

            @Override
            public void onPostExecute(String result) {
                hideLoading();
                GeneralDialog.build(-1).title(R.string.pd_success).message(R.string.pd_copy_hint,
                        result).positiveButton(R.string.pd_ok).show(FileAttrFragment.this);
            }
        }).execute(file);
        showLoading();
    }

    private void tryOpen() {
        new SimpleTask<>(new SimpleTask.Callback<File, Intent>() {
            @Override
            public Intent doInBackground(File[] params) {
                String result = FileUtil.fileCopy2Tmp(params[0]);
                if (!TextUtils.isEmpty(result)) {
                    return FileUtil.getFileIntent(result);
                }
                return null;
            }

            @Override
            public void onPostExecute(Intent result) {
                hideLoading();
                if (result != null) {
                    try {
                        startActivity(result);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Utils.toast(t.getMessage());
                    }
                } else {
                    Utils.toast(R.string.pd_not_support);
                }
            }
        }).execute(file);
        showLoading();
    }

    private void tryOpenAsText() {
        new SimpleTask<>(new SimpleTask.Callback<File, List<String>>() {
            @Override
            public List<String> doInBackground(File[] params) {
                return FileUtil.readAsPlainText(params[0]);
            }

            @Override
            public void onPostExecute(List<String> result) {
                hideLoading();
                if (result != null) {
                    List<BaseItem> items = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        items.add(new ContentItem(result.get(i)));
                    }
                    getAdapter().setItems(items);
                } else {
                    Utils.toast(R.string.pd_not_support);
                }
            }
        }).execute(file);
        showLoading();
    }

    private void tryDel() {
        new SimpleTask<>(new SimpleTask.Callback<File, Boolean>() {
            @Override
            public Boolean doInBackground(File[] params) {
                return params[0].delete();
            }

            @Override
            public void onPostExecute(Boolean result) {
                hideLoading();
                Utils.toast(result ? R.string.pd_success : R.string.pd_failed);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                        null);
                onBackPressed();
            }
        }).execute(file);
        showLoading();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            final String value = data.getStringExtra("value");
            new SimpleTask<>(new SimpleTask.Callback<Void, Boolean>() {
                @Override
                public Boolean doInBackground(Void[] params) {
                    return FileUtil.renameTo(file, value);
                }

                @Override
                public void onPostExecute(Boolean result) {
                    hideLoading();
                    Utils.toast(result ? R.string.pd_success : R.string.pd_failed);
                    loadData();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                            null);
                }
            }).execute();
            showLoading();
        }
    }

}
