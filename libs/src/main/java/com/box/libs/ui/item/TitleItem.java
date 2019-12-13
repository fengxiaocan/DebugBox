package com.box.libs.ui.item;


import com.box.libs.R;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 03/06/2018.
 */

public class TitleItem extends BaseItem<String> {
    public TitleItem(String data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        pool.setText(R.id.item_title_id, data);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_title;
    }
}
