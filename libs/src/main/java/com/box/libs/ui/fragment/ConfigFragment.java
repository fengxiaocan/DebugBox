package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.R;
import com.box.libs.ui.item.CheckBoxItem;
import com.box.libs.ui.item.NameArrowItem;
import com.box.libs.ui.item.SelectItem;
import com.box.libs.ui.item.TitleItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.ui.view.ColorPicker;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.Config;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2018/7/24.
 */

public class ConfigFragment extends BaseListFragment {

    private int editingType;

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_config);
        getToolbar().getMenu().add(-1, -1, 0, R.string.pd_name_reset).setShowAsAction(
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Config.reset();
                refreshData();
                BoxUtils.toast(R.string.pd_success);
                return false;
            }
        });
        refreshData();


        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (!(item instanceof TitleItem)) {
                    @Config.Type int type = (int) item.getTag();
                    Log.d(TAG, "onItemClick: " + type);
                    switch (type) {
                        case Config.Type.NETWORK_DELAY_REQ:
                        case Config.Type.NETWORK_DELAY_RES:
                        case Config.Type.NETWORK_PAGE_SIZE:
                        case Config.Type.DATABASE_STRING_LENGTH:
                        case Config.Type.UI_ACTIVITY_GRAVITY:
                        case Config.Type.UI_GRID_INTERVAL:
                        case Config.Type.SHAKE_THRESHOLD:
                            editingType = type;
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(PARAM2, true);
                            if (type == Config.Type.SHAKE_THRESHOLD) {
                                bundle.putStringArray(PARAM3,
                                        BoxUtils.newArray("800", "1000", "1200", "1400", "1600"));
                            } else if (type == Config.Type.UI_ACTIVITY_GRAVITY) {
                                bundle.putStringArray(PARAM3,
                                        BoxUtils.newArray("start|top", "end|top", "start|bottom",
                                                "end|bottom"));
                                bundle.putBoolean(PARAM4, true);
                            }
                            launch(EditFragment.class, bundle, CODE1);
                            break;
                        case Config.Type.NETWORK_URLCONNECTION:
                            Config.setNETWORK_URL_CONNECTION(!Config.getNETWORK_URL_CONNECTION());
                            BoxUtils.toast(R.string.pd_relaunch_request);
                            break;
                        case Config.Type.SANDBOX_DPM:
                            Config.setSANDBOX_DPM(!Config.getSANDBOX_DPM());
                            break;
                        case Config.Type.SHAKE_SWITCH:
                            Config.setSHAKE_SWITCH(!Config.getSHAKE_SWITCH());
                            break;
                        case Config.Type.UI_IGNORE_SYS_LAYER:
                            Config.setUI_IGNORE_SYS_LAYER(!Config.getUI_IGNORE_SYS_LAYER());
                            break;
                        case Config.Type.UI_GRID_COLOR:
                            new ColorPicker(getContext()).setColor(Config.getUI_GRID_COLOR())
                                                         .setOnColorSelectListener(
                                                                 new ColorPicker.OnColorSelectListener() {
                                                                     @Override
                                                                     public void onSelectColor(
                                                                             String colorString,
                                                                             int color)
                                                                     {
                                                                         Config.setUI_GRID_COLOR(color);
                                                                         refreshData();
                                                                     }
                                                                 })
                                                         .show();
                            break;
                    }
                }
            }
        });
    }

    private void refreshData() {
        List<BaseItem> data = new ArrayList<>();

        data.add(new TitleItem(ViewKnife.getString(R.string.pd_name_network)));
        data.add(new NameArrowItem(getString(R.string.delay_request),
                "" + Config.getNETWORK_DELAY_REQ()).setTag(Config.Type.NETWORK_DELAY_REQ));
        data.add(new NameArrowItem(getString(R.string.delay_response),
                "" + Config.getNETWORK_DELAY_RES()).setTag(Config.Type.NETWORK_DELAY_RES));
        data.add(new NameArrowItem(getString(R.string.maximum_loads),
                "" + Config.getNETWORK_PAGE_SIZE()).setTag(Config.Type.NETWORK_PAGE_SIZE));
        data.add(new NameArrowItem(getString(R.string.maximum_loads_database_string_length),
                "" + Config.getDATABASE_STRING_LENGTH()).setTag(Config.Type.DATABASE_STRING_LENGTH));

        data.add(new CheckBoxItem(getString(R.string.show_url_connection),
                Config.getNETWORK_URL_CONNECTION()).setTag(Config.Type.NETWORK_URLCONNECTION));

        data.add(new TitleItem(ViewKnife.getString(R.string.pd_name_sandbox)));
        data.add(new CheckBoxItem(getString(R.string.show_protect_mode),
                Config.getSANDBOX_DPM()).setTag(Config.Type.SANDBOX_DPM));

        data.add(new TitleItem("UI"));
        data.add(new NameArrowItem(getString(R.string.gravity_info),
                "" + ViewKnife.parseGravity(Config.getUI_ACTIVITY_GRAVITY())).setTag(
                Config.Type.UI_ACTIVITY_GRAVITY));
        data.add(new NameArrowItem(getString(R.string.interval_grid_line),
                "" + Config.getUI_GRID_INTERVAL()).setTag(Config.Type.UI_GRID_INTERVAL));
        data.add(
                new CheckBoxItem(getString(R.string.ignore_layers), Config.getUI_IGNORE_SYS_LAYER())
                        .setTag(Config.Type.UI_IGNORE_SYS_LAYER));
        data.add(
                new SelectItem(getString(R.string.grid_color), Config.getUI_GRID_COLOR()).setTag(
                        Config.Type.UI_GRID_COLOR));

        data.add(new TitleItem("SHAKE"));
        data.add(new CheckBoxItem(getString(R.string.pd_name_turn_on),
                Config.getSHAKE_SWITCH()).setTag(Config.Type.SHAKE_SWITCH));
        data.add(new NameArrowItem(getString(R.string.pd_name_threshold),
                "" + Config.getSHAKE_THRESHOLD()).setTag(Config.Type.SHAKE_THRESHOLD));

        getAdapter().setItems(data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            String value = data.getStringExtra("value");
            try {
                switch (editingType) {
                    case Config.Type.NETWORK_DELAY_REQ:
                        long req = Long.parseLong(value);
                        Config.setNETWORK_DELAY_REQ(req);
                        break;
                    case Config.Type.NETWORK_DELAY_RES:
                        long res = Long.parseLong(value);
                        Config.setNETWORK_DELAY_RES(res);
                        break;
                    case Config.Type.NETWORK_PAGE_SIZE:
                        int size = Integer.parseInt(value);
                        if (size < 1) {
                            BoxUtils.toast("invalid. At least 1");
                            return;
                        }
                        Config.setNETWORK_PAGE_SIZE(size);
                        break;
                    case Config.Type.DATABASE_STRING_LENGTH:
                        int length = Integer.parseInt(value);
                        if (length < 1) {
                            BoxUtils.toast("invalid. At least 1");
                            return;
                        }
                        Config.setDATABASE_STRING_LENGTH(length);
                        break;
                    case Config.Type.SHAKE_THRESHOLD:
                        int threshold = Integer.parseInt(value);
                        if (threshold < 600) {
                            BoxUtils.toast("invalid. At least 600");
                            return;
                        }
                        Config.setSHAKE_THRESHOLD(threshold);
                        break;
                    case Config.Type.UI_ACTIVITY_GRAVITY:
                        String gravity = String.valueOf(value);
                        int result = ViewKnife.formatGravity(gravity);
                        if (result != 0) {
                            Config.setUI_ACTIVITY_GRAVITY(result);
                        } else {
                            BoxUtils.toast("invalid");
                        }
                        break;
                    case Config.Type.UI_GRID_INTERVAL:
                        int interval = Integer.parseInt(value);
                        if (interval < 1) {
                            BoxUtils.toast("invalid. At least 1");
                            return;
                        }
                        Config.setUI_GRID_INTERVAL(interval);
                        break;

                }
                refreshData();
                BoxUtils.toast(R.string.pd_success);
            } catch (Throwable t) {
                t.printStackTrace();
                BoxUtils.toast(t.getMessage());
            }
        }
    }

}
