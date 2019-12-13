package com.box.libs.ui.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.cache.Content;
import com.box.libs.cache.Summary;
import com.box.libs.network.NetStateListener;
import com.box.libs.ui.connector.SimpleOnActionExpandListener;
import com.box.libs.ui.connector.SimpleOnQueryTextListener;
import com.box.libs.ui.item.NetItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.Config;
import com.box.libs.util.SimpleTask;
import com.box.libs.util.Utils;
import com.box.libs.util.ViewKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2018/6/22.
 */

public class NetFragment extends BaseListFragment
        implements Toolbar.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener,
        NetStateListener {

    private List<BaseItem> originData = new ArrayList<>();
    private List<BaseItem> tmpFilter = new ArrayList<>();

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_network);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_1, 0, "").setActionView(
                new SwitchCompat(getContext())).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_2, 1, R.string.pd_name_search).setActionView(
                new SearchView(getContext())).setIcon(R.drawable.pd_search).setShowAsAction(
                MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_3, 2, R.string.pd_name_clear);
        setSearchView();
        getToolbar().setOnMenuItemClickListener(this);
        SwitchCompat switchCompat = ((SwitchCompat) getToolbar().getMenu().findItem(
                R.id.pd_menu_id_1).getActionView());
        switchCompat.setOnCheckedChangeListener(this);
        if (Config.isNetLogEnable()) {
            switchCompat.setChecked(true);
        } else {
            showOpenLogHint();
        }
        DebugBox.get().getInterceptor().setListener(this);
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof NetItem) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(PARAM1, ((Summary) item.data).id);
                    launch(NetSummaryFragment.class, bundle);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DebugBox.get().getInterceptor().removeListener();
    }

    private void loadData() {
        hideError();
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<Summary>>() {
            @Override
            public List<Summary> doInBackground(Void[] params) {
                return Summary.queryList();
            }

            @Override
            public void onPostExecute(List<Summary> result) {
                hideLoading();
                if (Utils.isNotEmpty(result)) {
                    List<BaseItem> data = new ArrayList<>(result.size());
                    for (Summary summary : result) {
                        data.add(new NetItem(summary));
                    }
                    getAdapter().setItems(data);

                    originData.clear();
                    originData.addAll(getAdapter().getItems());
                } else {
                    showError(null);
                }
            }
        }).execute();
    }

    private void clearData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, Void>() {
            @Override
            public Void doInBackground(Void[] params) {
                Summary.clear();
                Content.clear();
                return null;
            }

            @Override
            public void onPostExecute(Void result) {
                getAdapter().clearItems();
                hideLoading();
                showError(null);
            }
        }).execute();
    }

    private void setSearchView() {
        MenuItem menuItem = getToolbar().getMenu().findItem(R.id.pd_menu_id_2);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        searchView.setQueryHint(ViewKnife.getString(R.string.pd_net_search_hint));
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                closeSoftInput();
                filter(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(menuItem, new SimpleOnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                loadData();
                return true;
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.pd_menu_id_3) {
            if (!Config.isNetLogEnable()) {
                return false;
            }
            clearData();
        }
        closeSoftInput();
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Config.setNetLogEnable(isChecked);
        if (isChecked) {
            loadData();
        } else {
            showOpenLogHint();
        }
    }

    private void showOpenLogHint() {
        getAdapter().clearItems();
        showError(ViewKnife.getString(R.string.pd_please_open_net_log));
    }

    @Override
    public void onRequestStart(long id) {
        refreshSingleData(true, id);
    }

    @Override
    public void onRequestEnd(long id) {
        refreshSingleData(false, id);
    }

    private void filter(String condition) {
        tmpFilter.clear();
        if (TextUtils.isEmpty(condition)) {
            loadData();
            return;
        }
        if (Utils.isNotEmpty(originData)) {
            for (int i = originData.size() - 1; i >= 0; i--) {
                if (originData.get(i) instanceof NetItem) {
                    String url = ((Summary) originData.get(i).data).url;
                    if (url.contains(condition)) {
                        tmpFilter.add(originData.get(i));
                    }
                }
            }
            getAdapter().setItems(tmpFilter);
        }
    }

    private void refreshSingleData(final boolean isNew, final long id) {
        new SimpleTask<>(new SimpleTask.Callback<Void, Summary>() {
            @Override
            public Summary doInBackground(Void[] params) {
                return Summary.query(id);
            }

            @Override
            public void onPostExecute(Summary result) {
                hideLoading();
                if (result == null) {
                    return;
                }
                if (!isNew) {
                    for (int i = 0; i < getAdapter().getItems().size(); i++) {
                        if (getAdapter().getItems().get(i) instanceof NetItem) {
                            if (((Summary) getAdapter().getItems().get(i).data).id == result.id) {
                                getAdapter().getItems().get(i).data = result;
                                getAdapter().notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                } else {
                    getAdapter().insertItem(new NetItem(result), 0);
                }
                originData.clear();
                originData.addAll(getAdapter().getItems());
            }
        }).execute();
    }
}
