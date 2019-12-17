package com.box.libs.ui.item;

import android.view.View;

import com.box.libs.R;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.FileUtil;

import java.io.File;
import java.util.Locale;

/**
 * Created by linjiang on 04/06/2018.
 */

public class FileItem extends BaseItem<File> {
    private String info;

    public FileItem(File data) {
        super(data);
        if (!data.isDirectory()) {
            info = String.format(Locale.getDefault(), "%s    %s", FileUtil.fileSize(data),
                    BoxUtils.millis2String(data.lastModified(), BoxUtils.NO_MILLIS));
        } else {
            info = String.format(Locale.getDefault(), "%d items    %s", BoxUtils.getCount(data.list()),
                    BoxUtils.millis2String(data.lastModified(), BoxUtils.NO_MILLIS));
        }
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, File data) {
        pool.setVisibility(R.id.common_item_arrow, data.isDirectory() ? View.VISIBLE : View.GONE)
            .setText(R.id.common_item_title, data.getName())
            .setText(R.id.common_item_info, info);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_common;
    }
}
