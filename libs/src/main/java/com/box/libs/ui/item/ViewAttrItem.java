package com.box.libs.ui.item;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.box.libs.R;
import com.box.libs.inspector.model.Attribute;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2018/6/20.
 */

public class ViewAttrItem extends BaseItem<Attribute> {

    private boolean canEdit;

    public ViewAttrItem(Attribute data) {
        super(data);
        canEdit = data.attrType != Attribute.Edit.NORMAL;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, Attribute data) {
        pool.setTextGravity(R.id.item_key, Gravity.CENTER_VERTICAL)
            .setText(R.id.item_key, data.attrName)
            .setTextGravity(R.id.item_value, Gravity.CENTER_VERTICAL)
            .setBackgroundColor(R.id.item_value, Color.WHITE)
            .setText(R.id.item_value, data.attrValue);

        pool.getView(R.id.item_value).setVisibility(View.VISIBLE);
        pool.getView(R.id.item_edit).setVisibility(View.GONE);
        pool.getView(R.id.item_arrow).setVisibility(canEdit ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_key_value;
    }
}
