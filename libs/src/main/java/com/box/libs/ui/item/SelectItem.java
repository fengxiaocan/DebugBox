package com.box.libs.ui.item;


import android.view.View;

import com.box.libs.R;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.ui.view.ColorPicker;

/**
 * Created by linjiang on 03/06/2018.
 */

public class SelectItem extends NameItem {
    private int color;

    public SelectItem(String data, int color) {
        super(data);
        this.color = color;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        super.onBinding(position, pool, data);
        pool.setVisibility(R.id.common_item_arrow, View.VISIBLE).setText(R.id.common_item_arrow,
                ColorPicker.toHexFromColor(color));
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_common;
    }
}
