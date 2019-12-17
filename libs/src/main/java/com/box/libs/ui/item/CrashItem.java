package com.box.libs.ui.item;

import android.text.TextUtils;
import android.view.View;

import com.box.libs.R;
import com.box.libs.cache.Crash;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.BoxUtils;

/**
 * Created by linjiang on 04/06/2018.
 */

public class CrashItem extends BaseItem<Crash> {

    public CrashItem(Crash data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, Crash data) {
        pool.setVisibility(R.id.common_item_arrow, View.VISIBLE).setText(R.id.common_item_info,
                TextUtils.isEmpty(data.cause) ? data.type : data.cause).setText(
                R.id.common_item_title, BoxUtils.millis2String(data.createTime, BoxUtils.HHMMSS));
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_common;
    }
}
