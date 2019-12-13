package com.box.libs.ui.item;


import com.box.libs.R;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2018/6/20.
 */

public class OptionItem extends BaseItem<String> {
    public OptionItem(String data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        pool.setText(R.id.item_option_btn, data);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_option;
    }
}
