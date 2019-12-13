package com.box.libs.ui.item;

import android.graphics.Color;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.box.libs.R;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.ViewKnife;

/**
 * Created by linjiang on 2018/7/22.
 */

public class ViewItem extends BaseItem<View> {
    public boolean selected;
    public boolean related;

    public ViewItem(View data) {
        super(data);
    }

    public ViewItem(View data, boolean selected, boolean related) {
        super(data);
        this.selected = selected;
        this.related = related;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, View data) {
        pool.setText(R.id.view_name_title, data.getClass().getSimpleName()).setText(
                R.id.view_name_subtitle, ViewKnife.getIdString(data));
        if (selected) {
            pool.getView(R.id.view_name_wrapper).setBackgroundColor(
                    ViewKnife.getColor(R.color.pd_blue));
            pool.setTextColor(R.id.view_name_title, Color.WHITE).setTextColor(
                    R.id.view_name_subtitle, Color.WHITE);
        } else {
            ViewCompat.setBackground(pool.getView(R.id.view_name_wrapper), ViewKnife.getDrawable(
                    related ? R.drawable.pd_shape_btn_bg_related : R.drawable.pd_shape_btn_bg));
            pool.setTextColor(R.id.view_name_title, 0xff000000).setTextColor(
                    R.id.view_name_subtitle, ViewKnife.getColor(R.color.pd_label_dark));
        }
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_view_name;
    }
}
