package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.R;
import com.box.libs.ui.item.RouteParamItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.ui.view.MenuRecyclerView;
import com.box.libs.util.Utils;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2019/1/13.
 */

public class RouteParamFragment extends BaseListFragment implements RouteParamItem.EditListener {

    @Override
    protected View getLayoutView() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(ViewKnife.getColor(R.color.pd_main_bg));
        layout.addView(super.getLayoutView(),
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        Button button = new Button(getContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                            assembleTargetIntent());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        button.setTextSize(14);
        button.setText("launch Activity");
        button.setGravity(Gravity.CENTER);
        button.setBackgroundResource(R.drawable.pd_shape_btn_bg);
        LinearLayout.LayoutParams buttonParam;
        layout.addView(button, buttonParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewKnife.dip2px(45)));
        buttonParam.leftMargin = buttonParam.topMargin =
                buttonParam.rightMargin = buttonParam.bottomMargin = ViewKnife.dip2px(16);

        return layout;
    }

    @Override
    protected Toolbar onCreateToolbar() {
        return new Toolbar(new ContextThemeWrapper(getContext(), R.style.pd_toolbar));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(getArguments().getString(PARAM1));
        getToolbar().setSubtitle(getArguments().getString(PARAM2));
        initToolbar();
        registerForContextMenu(getRecyclerView());

        List<BaseItem> data = new ArrayList<>();
        data.add(new RouteParamItem(RouteParamItem.Type.STRING, this));
        data.add(new RouteParamItem(RouteParamItem.Type.BOOLEAN, this));
        data.add(new RouteParamItem(RouteParamItem.Type.INT, this));
        data.add(new RouteParamItem(RouteParamItem.Type.LONG, this));
        data.add(new RouteParamItem(RouteParamItem.Type.FLOAT, this));
        data.add(new RouteParamItem(RouteParamItem.Type.DOUBLE, this));
        getAdapter().setItems(data);
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                getRecyclerView().getChildAt(position).showContextMenu();

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (menuInfo instanceof MenuRecyclerView.RvContextMenuInfo) {
            MenuRecyclerView.RvContextMenuInfo info = (MenuRecyclerView.RvContextMenuInfo) menuInfo;
            RouteParamItem paramItem = getAdapter().getItem(info.position);
            if (paramItem.data != RouteParamItem.Type.NONE) {
                menu.add(-1, R.id.pd_menu_id_1, 0, R.string.pd_name_add);
            }
            menu.add(-1, R.id.pd_menu_id_2, 0, R.string.pd_name_delete_key);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getMenuInfo() instanceof MenuRecyclerView.RvContextMenuInfo) {
            MenuRecyclerView.RvContextMenuInfo info =
                    (MenuRecyclerView.RvContextMenuInfo) item.getMenuInfo();
            RouteParamItem paramItem = getAdapter().getItem(info.position);
            if (item.getItemId() == R.id.pd_menu_id_2) {
                getAdapter().removeItem(info.position);
            } else if (item.getItemId() == R.id.pd_menu_id_1) {
                getAdapter().insertItem(new RouteParamItem(paramItem.data, this), info.position);
            }
        }
        return super.onContextItemSelected(item);
    }

    private void initToolbar() {
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.STRING, "add String extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.BOOLEAN, "add boolean extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.INT, "add int extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.LONG, "add long extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.FLOAT, "add float extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.DOUBLE, "add double extra");
        SubMenu subMenu = getToolbar().getMenu().addSubMenu(0, 0, 7, "add flag");
        subMenu.add(R.id.pd_menu_id_1, 0, 0, "FLAG_ACTIVITY_NEW_TASK");
        subMenu.add(R.id.pd_menu_id_1, 0, 1, "FLAG_ACTIVITY_SINGLE_TOP");
        subMenu.add(R.id.pd_menu_id_1, 0, 2, "FLAG_ACTIVITY_CLEAR_TASK");
        subMenu.add(R.id.pd_menu_id_1, 0, 3, "FLAG_ACTIVITY_CLEAR_TOP");
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.hasSubMenu()) {
                    return true;
                } else {
                    if (item.getGroupId() == R.id.pd_menu_id_1) {
                        RouteParamItem paramItem = new RouteParamItem(RouteParamItem.Type.NONE);
                        switch (item.getOrder()) {
                            case 0:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_NEW_TASK,
                                        item.getTitle().toString());
                                break;
                            case 1:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_SINGLE_TOP,
                                        item.getTitle().toString());
                                break;
                            case 2:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_CLEAR_TASK,
                                        item.getTitle().toString());
                                break;
                            case 3:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_CLEAR_TOP,
                                        item.getTitle().toString());
                                break;
                        }
                        getAdapter().insertItem(paramItem);
                    } else {
                        getAdapter().insertItem(new RouteParamItem(item.getOrder()));
                    }
                    return true;
                }
            }
        });
    }

    private Intent assembleTargetIntent() throws ClassNotFoundException {
        String clazz = getArguments().getString(PARAM2);
        Intent intent = new Intent(getContext(), Class.forName(clazz));
        List<BaseItem> items = getAdapter().getItems();
        if (Utils.isNotEmpty(items)) {
            for (int i = 0; i < items.size(); i++) {
                RouteParamItem item = (RouteParamItem) items.get(i);
                switch (item.data) {
                    case RouteParamItem.Type.BOOLEAN:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Boolean.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.DOUBLE:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Double.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.NONE:
                        intent.addFlags(item.getFlagType());
                        break;
                    case RouteParamItem.Type.FLOAT:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Float.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.INT:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Integer.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.LONG:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Long.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.STRING:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), item.getInput2());
                        }
                        break;
                }
            }
        }
        return intent;
    }

    @Override
    public void onEditReq(String def, @RouteParamItem.Type int type) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM1, def);
        if (type == RouteParamItem.Type.BOOLEAN) {
            bundle.putStringArray(PARAM3, Utils.newArray("true", "false"));
            bundle.putBoolean(PARAM4, true);
        } else {
            bundle.putBoolean(PARAM2, type != RouteParamItem.Type.STRING);
        }
        launch(EditFragment.class, bundle, CODE1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            String value = data.getStringExtra("value");
            RouteParamItem selectedItem = null;
            for (int i = 0; i < getAdapter().getItemCount(); i++) {
                RouteParamItem item = getAdapter().getItem(i);
                if (item.isEditRequesting()) {
                    selectedItem = item;
                    break;
                }
            }
            if (selectedItem != null) {
                selectedItem.setTheEditResult(value);
                getAdapter().notifyDataSetChanged();
            }
        }
    }
}
