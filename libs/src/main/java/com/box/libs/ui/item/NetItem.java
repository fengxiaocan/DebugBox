package com.box.libs.ui.item;

import android.graphics.Color;
import android.text.TextUtils;

import com.box.libs.R;
import com.box.libs.cache.Summary;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.Utils;

import java.util.Locale;

/**
 * Created by linjiang on 2018/6/23.
 */

public class NetItem extends BaseItem<Summary> {

    public NetItem(Summary data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, Summary data) {
        if (position % 2 == 0) {
            pool.itemView.setBackgroundResource(R.color.pd_item_bg);
        } else {
            pool.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        boolean done = data.status != 0;
        pool.setImageResource(R.id.item_net_status, !done ? R.drawable.pd_transform :
                (data.status == 1 ? R.drawable.pd_error : R.drawable.pd_done));

        pool.setTextColor(R.id.item_net_url,
                done && data.code > 0 && data.code != 200 ? Color.BLUE : Color.BLACK);
        pool.setText(R.id.item_net_url, data.url).setText(R.id.item_net_host, data.host).setText(
                R.id.item_net_info, String.format(Locale.getDefault(), "%s    %s    %s%s%s",
                        Utils.millis2String(data.start_time, Utils.HHMMSS), data.method,
                        done && data.code > 0 ? data.code + "    " : "",
                        (done && data.response_size > 0) ?
                                Utils.formatSize(data.response_size) + "    " : "",
                        done && data.end_time > 0 && data.start_time > 0 ?
                                (data.end_time - data.start_time) + "ms" : ""));

        if (done) {
            pool.setCompoundDrawableLeft(R.id.item_net_url,
                    isImage(data.response_content_type) ? R.drawable.pd_image : 0);
        } else {
            pool.setCompoundDrawableLeft(R.id.item_net_url, 0);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_net;
    }

    private boolean isImage(String contentType) {
        return !TextUtils.isEmpty(contentType) && contentType.contains("image");
    }

}
