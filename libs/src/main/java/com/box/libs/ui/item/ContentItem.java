package com.box.libs.ui.item;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import com.box.libs.R;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.ViewKnife;

/**
 * Created by linjiang on 06/06/2018.
 */

public class ContentItem extends NameItem {

    private boolean focus;

    public ContentItem(String data) {
        super(data);
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        pool.getView(R.id.db_list_item_wrapper).getLayoutParams().height =
                ViewGroup.LayoutParams.WRAP_CONTENT;
        ((TextView) pool.getView(R.id.common_item_title)).setSingleLine(false);
        pool.setBackgroundColor(R.id.db_list_item_wrapper,
                focus ? ViewKnife.getColor(R.color.pd_item_focus) : Color.TRANSPARENT);
        super.onBinding(position, pool, data);
    }
}
