package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.database.DatabaseResult;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.ui.connector.SimpleOnActionExpandListener;
import com.box.libs.ui.connector.SimpleOnQueryTextListener;
import com.box.libs.ui.item.GridItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.GridDividerDecoration;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.ui.view.MenuRecyclerView;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.SimpleTask;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 03/06/2018.
 */

public class TableFragment extends BaseFragment {
    private UniversalAdapter adapter;
    private MenuRecyclerView recyclerView;
    private int key;
    // true: table's info; false: table's content
    private boolean mode;
    private String table;
    private String primaryKey;
    private GridItem clickedItem;
    private String realTimeQueryCondition;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getArguments().getInt(PARAM1);
        table = getArguments().getString(PARAM2);
        mode = getArguments().getBoolean(PARAM3);
        primaryKey = DebugBox.get().getDatabases().getPrimaryKey(key, table);
    }

    @Override
    protected View getLayoutView() {
        HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setFillViewport(true);
        recyclerView = new MenuRecyclerView(getContext());
        recyclerView.setBackgroundColor(ViewKnife.getColor(R.color.pd_main_bg));
        scrollView.addView(recyclerView, params);
        return scrollView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!mode) {
            initMenu();
        }
        adapter = new UniversalAdapter();
        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new GridDividerDecoration.Builder().setColor(
                ViewKnife.getColor(R.color.pd_divider_light))
                .setThickness(ViewKnife.dip2px(1f))
                .build());
        recyclerView.setAdapter(adapter);

        adapter.setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof GridItem) {
                    if (mode) {
                        return;
                    }
                    if (!((GridItem) item).isEnable()) {
                        return;
                    }
                    clickedItem = (GridItem) item;
                    Bundle bundle = new Bundle();
                    bundle.putString(PARAM1, ((GridItem) item).data);
                    launch(EditFragment.class, bundle, CODE1);
                }
            }
        });
        adapter.setLongClickListener(new UniversalAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position, BaseItem item) {
                if (item instanceof GridItem) {
                    // make sure that only the content can be response for menuEvent
                    return !((GridItem) item).isEnable();
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeSoftInput();
    }

    @Override
    protected void onViewEnterAnimEnd(View container) {
        loadData(null);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(-1, R.id.pd_menu_id_1, 0, R.string.pd_name_copy_value);
        menu.add(-1, R.id.pd_menu_id_2, 1, R.string.pd_name_delete_row);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        MenuRecyclerView.RvContextMenuInfo info =
                (MenuRecyclerView.RvContextMenuInfo) item.getMenuInfo();
        BaseItem gridItem = adapter.getItems().get(info.position);
        if (gridItem instanceof GridItem) {
            if (item.getItemId() == R.id.pd_menu_id_1) {
                BoxUtils.copy2ClipBoard((String) gridItem.data);
                return true;
            } else if (item.getItemId() == R.id.pd_menu_id_2) {
                String pkValue = ((GridItem) gridItem).primaryKeyValue;
                delete(pkValue);
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void initMenu() {
        getToolbar().getMenu()
                .add(0, 0, 0, R.string.pd_name_help)
                .setIcon(R.drawable.pd_help)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem searchItem = getToolbar().getMenu().add(0, 0, 1, R.string.pd_name_search);
        searchItem.setActionView(new SearchView(getContext()))
                .setIcon(R.drawable.pd_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        getToolbar().getMenu().add(0, 0, 2, R.string.pd_name_info);
        getToolbar().getMenu().add(0, 0, 3, R.string.pd_name_add);
        getToolbar().getMenu().add(0, 0, 4, R.string.pd_name_delete_all);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(ViewKnife.getString(R.string.pd_search_hint));
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                closeSoftInput();
                realTimeQueryCondition = query;
                loadData(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(searchItem, new SimpleOnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (!TextUtils.isEmpty(realTimeQueryCondition)) {
                    realTimeQueryCondition = null;
                    loadData(null);
                }
                return true;
            }
        });
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(-1).title(R.string.pd_help_title).message(
                            R.string.pd_help_table).positiveButton(R.string.pd_ok).show(
                            TableFragment.this);
                }
                if (item.getOrder() == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM1, key);
                    bundle.putString(PARAM2, table);
                    bundle.putBoolean(PARAM3, true);
                    launch(TableFragment.class, bundle);
                } else if (item.getOrder() == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM1, key);
                    bundle.putString(PARAM2, table);
                    launch(AddRowFragment.class, bundle, CODE2);
                } else if (item.getOrder() == 4) {
                    delete(null);
                }
                closeSoftInput();
                return true;
            }
        });
    }

    private void loadData(final String condition) {
        adapter.clearItems();
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                if (mode) {
                    return DebugBox.get().getDatabases().getTableInfo(key, table);
                } else {
                    return DebugBox.get().getDatabases().query(key, table, condition);
                }
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                List<BaseItem> data = new ArrayList<>();
                if (result.sqlError == null) {
                    recyclerView.setLayoutManager(
                            new GridLayoutManager(getContext(), result.columnNames.size()));
                    int pkIndex = 0;
                    for (int i = 0; i < result.columnNames.size(); i++) {
                        data.add(new GridItem(result.columnNames.get(i), true));
                        if (TextUtils.equals(result.columnNames.get(i), primaryKey)) {
                            pkIndex = i;
                        }
                    }
                    for (int i = 0; i < result.values.size(); i++) {
                        for (int j = 0; j < result.values.get(i).size(); j++) {
                            GridItem item = new GridItem(result.values.get(i).get(j),
                                    result.values.get(i).get(pkIndex), result.columnNames.get(j));
                            if (!mode && pkIndex == j) {
                                item.setIsPrimaryKey();
                            }
                            data.add(item);
                        }
                    }
                    adapter.setItems(data);
                } else {
                    BoxUtils.toast(result.sqlError.message);
                }
                hideLoading();
            }
        }).execute();
    }

    private void delete(final String pkValue) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                return DebugBox.get().getDatabases().delete(key, table,
                        TextUtils.isEmpty(pkValue) ? null : primaryKey,
                        TextUtils.isEmpty(pkValue) ? null : pkValue);
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                hideLoading();
                if (result.sqlError != null) {
                    BoxUtils.toast(result.sqlError.message);
                } else {
                    realTimeQueryCondition = null;
                    BoxUtils.toast(R.string.pd_success);
                    loadData(null);
                }
            }
        }).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            final String value = data.getStringExtra("value");
            showLoading();
            new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
                @Override
                public DatabaseResult doInBackground(Void[] params) {
                    return DebugBox.get().getDatabases().update(key, table, primaryKey,
                            clickedItem.primaryKeyValue, clickedItem.columnName, value);
                }

                @Override
                public void onPostExecute(DatabaseResult result) {
                    hideLoading();
                    BoxUtils.toast(result.sqlError != null ? R.string.pd_failed : R.string.pd_success);
                    loadData(realTimeQueryCondition);
                }
            }).execute();
        } else if (requestCode == CODE2 && resultCode == Activity.RESULT_OK) {
            loadData(realTimeQueryCondition);
        }
    }

}
