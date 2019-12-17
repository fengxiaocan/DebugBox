package com.box.libs.ui.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.database.Column;
import com.box.libs.database.DatabaseResult;
import com.box.libs.ui.item.KeyEditItem;
import com.box.libs.ui.item.KeyValueItem;
import com.box.libs.ui.item.TitleItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.util.BoxUtils;
import com.box.libs.util.SimpleTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by linjiang on 07/06/2018.
 */

public class AddRowFragment extends BaseListFragment {

    private int key;
    private String table;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getArguments().getInt(PARAM1);
        table = getArguments().getString(PARAM2);
        getArguments().remove(PARAM3);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_add);

        getToolbar().getMenu().add(-1, -1, 0, R.string.pd_name_save).setShowAsAction(
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<BaseItem> datas = getAdapter().getItems();
                if (BoxUtils.isNotEmpty(datas)) {
                    ContentValues values = new ContentValues();
                    for (int i = 0; i < datas.size(); i++) {
                        if (datas.get(i) instanceof KeyEditItem) {
                            if (!((KeyEditItem) datas.get(i)).editable) {
                                continue;
                            }
                            String[] data = ((KeyEditItem) datas.get(i)).data;
                            //                            if (((KeyEditItem)datas.get(i)).isNotNull && data[1] == null) {
                            //                                BoxUtils.toast("failed, [" + data[0] + "] need valid value");
                            //                                return true;
                            //                            }
                            values.put(data[0], data[1]);
                        }
                    }
                    if (values.size() > 0) {
                        insert(values);
                    }
                }
                return true;
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
        loadData();
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                return DebugBox.get().getDatabases().getTableInfo(key, table);
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                List<BaseItem> data = new ArrayList<>();
                if (result.sqlError == null) {
                    data.add(new TitleItem(String.format(Locale.getDefault(), "%d COLUMNS",
                            result.values.size())));
                    data.add(new KeyValueItem(new String[]{"KEY", "VALUE"}, true));
                    Map<String, Integer> keyMapIndex = new HashMap<>();
                    for (int i = 0; i < result.columnNames.size(); i++) {
                        if (TextUtils.equals(result.columnNames.get(i), Column.NAME)) {
                            keyMapIndex.put(Column.NAME, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.TYPE)) {
                            keyMapIndex.put(Column.TYPE, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.NOT_NULL)) {
                            keyMapIndex.put(Column.NOT_NULL, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.DEF_VALUE)) {
                            keyMapIndex.put(Column.DEF_VALUE, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.PK)) {
                            keyMapIndex.put(Column.PK, i);
                        }
                    }
                    for (int i = 0; i < result.values.size(); i++) {
                        boolean isPrimaryKey = result.values.get(i)
                                                            .get(keyMapIndex.get(Column.PK))
                                                            .equals("1");
                        boolean isNotNull = (result.values.get(i).get(
                                keyMapIndex.get(Column.NOT_NULL)).equals("1"));
                        String typeName = result.values.get(i).get(keyMapIndex.get(Column.TYPE));
                        boolean isInteger = "INTEGER".equalsIgnoreCase(typeName);
                        data.add(new KeyEditItem(isPrimaryKey && isInteger,
                                new String[]{result.values.get(i)
                                                          .get(keyMapIndex.get(Column.NAME)) +
                                             (isPrimaryKey ? "  (primaryKey)" : ""),
                                        (isPrimaryKey && isInteger) ? "AUTO" : result.values.get(i)
                                                                                            .get(keyMapIndex
                                                                                                    .get(Column.DEF_VALUE))},
                                typeName + (isNotNull ? "" : "  (optional)")));
                    }
                    getAdapter().setItems(data);
                } else {
                    BoxUtils.toast(result.sqlError.message);
                }
                hideLoading();
            }
        }).execute();
    }

    private void insert(final ContentValues values) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                return DebugBox.get().getDatabases().insert(key, table, values);
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                hideLoading();
                if (result.sqlError == null) {
                    BoxUtils.toast(R.string.pd_success);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                            null);
                } else {
                    BoxUtils.toast(result.sqlError.message);
                    getTargetFragment().onActivityResult(getTargetRequestCode(),
                            Activity.RESULT_CANCELED, null);
                }
            }
        }).execute();
    }
}
