package com.box.libs.ui.item;


import com.box.libs.R;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;

public class ExceptionItem extends BaseItem<String> {
    public ExceptionItem(String data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        pool.setText(R.id.text, data);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_exception;
    }
}
