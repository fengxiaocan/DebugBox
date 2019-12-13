package com.box.libs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.box.libs.DebugBox;
import com.box.libs.R;
import com.box.libs.inspector.OperableView;
import com.box.libs.ui.GeneralDialog;
import com.box.libs.ui.item.ViewItem;
import com.box.libs.ui.recyclerview.BaseItem;
import com.box.libs.ui.recyclerview.UniversalAdapter;
import com.box.libs.util.Utils;
import com.box.libs.util.ViewKnife;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 15/06/2018.
 */

public class ViewFragment extends BaseFragment implements View.OnClickListener {

    private BottomSheetBehavior behavior;
    private OperableView operableView;
    private View targetView;
    private TextView tvType, tvClazz, tvPath, tvId, tvSize;
    private RecyclerView parentRv, currentRv, childRv;
    private UniversalAdapter parentAdapter = new UniversalAdapter(), currentAdapter =
            new UniversalAdapter(), childAdapter = new UniversalAdapter();
    private UniversalAdapter.OnItemClickListener clickListener =
            new UniversalAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, BaseItem item) {
                    if (item instanceof ViewItem) {
                        View clickItem = (View) item.data;
                        boolean selected = ((ViewItem) item).selected;
                        if (!selected) {
                            boolean success = operableView.handleClick(clickItem);
                            if (!success) {
                                Utils.toast("Alpha == 0 || Visibility != VISIBLE");
                            }
                        }
                    }
                }
            };

    @Override
    protected Toolbar onCreateToolbar() {
        return null;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        View panelView = LayoutInflater.from(getContext()).inflate(R.layout.pd_layout_view_panel,
                null);
        operableView = new OperableView(getContext());
        operableView.tryGetFrontView(DebugBox.get().getTopActivity());
        operableView.setOnClickListener(this);

        CoordinatorLayout layout = new CoordinatorLayout(getContext());
        CoordinatorLayout.LayoutParams selectViewParams = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(operableView, selectViewParams);
        CoordinatorLayout.LayoutParams panelViewParams = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        panelViewParams.setBehavior(behavior = new BottomSheetBehavior());
        // shadow's height is 18dp
        behavior.setPeekHeight(ViewKnife.dip2px(122));
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        layout.addView(panelView, panelViewParams);

        return layout;
    }

    @Override
    public void onClick(View v) {
        if (operableView.isSelectedEmpty()) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        targetView = v;
        refreshViewInfo(v);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        view.findViewById(R.id.view_panel_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add flag
                targetView.setTag(R.id.pd_view_tag_for_unique, new Object());
                launch(ViewAttrFragment.class, null);
            }
        });
        view.findViewById(R.id.view_panel_hierarchy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetView.setTag(R.id.pd_view_tag_for_unique, new Object());
                launch(HierarchyFragment.class, null);
            }
        });
        tvType = view.findViewById(R.id.view_panel_type);
        tvClazz = view.findViewById(R.id.view_panel_clazz);
        tvPath = view.findViewById(R.id.view_panel_path);
        tvId = view.findViewById(R.id.view_panel_id);
        tvSize = view.findViewById(R.id.view_panel_size);
        parentRv = view.findViewById(R.id.view_panel_parent);
        parentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        parentRv.setAdapter(parentAdapter);
        parentAdapter.setListener(clickListener);
        currentRv = view.findViewById(R.id.view_panel_current);
        currentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        currentRv.setAdapter(currentAdapter);
        currentAdapter.setListener(clickListener);
        childRv = view.findViewById(R.id.view_panel_child);
        childRv.setLayoutManager(new LinearLayoutManager(getContext()));
        childRv.setAdapter(childAdapter);
        childAdapter.setListener(clickListener);

        GeneralDialog.build(-1)
                     .title(R.string.pd_help_title)
                     .message(R.string.pd_help_operate)
                     .positiveButton(R.string.pd_ok)
                     .show(this);
    }

    private void refreshViewInfo(View target) {
        tvType.setText(target instanceof ViewGroup ? "group" : "view");
        tvClazz.setText(target.getClass().getSimpleName());
        tvPath.setText(target.getClass().getName());
        tvId.setText(ViewKnife.getIdString(target));
        int widthText = ViewKnife.px2dip(target.getWidth());
        int heightText = ViewKnife.px2dip(target.getHeight());
        tvSize.setText(String.format("%d x %d dp", widthText, heightText));
        parentAdapter.clearItems();
        currentAdapter.clearItems();
        childAdapter.clearItems();
        if (target instanceof ViewGroup) {
            List<BaseItem> childData = new ArrayList<>();
            for (int i = 0; i < ((ViewGroup) target).getChildCount(); i++) {
                View item = ((ViewGroup) target).getChildAt(i);
                childData.add(new ViewItem(item, false, true));
            }
            childAdapter.setItems(childData);
        }
        if (target.getParent() != null && target.getParent() instanceof ViewGroup) {
            ViewGroup parentGroup = (ViewGroup) target.getParent();
            List<BaseItem> parentGroupData = new ArrayList<>();
            for (int i = 0; i < parentGroup.getChildCount(); i++) {
                View item = parentGroup.getChildAt(i);
                parentGroupData.add(new ViewItem(item, item == target, false));
            }
            currentAdapter.setItems(parentGroupData);
            if (parentGroup.getParent() != null && parentGroup.getParent() instanceof ViewGroup) {
                ViewGroup grandGroup = (ViewGroup) parentGroup.getParent();
                List<BaseItem> grandGroupData = new ArrayList<>();
                for (int i = 0; i < grandGroup.getChildCount(); i++) {
                    View item = grandGroup.getChildAt(i);
                    grandGroupData.add(new ViewItem(item, false, item == target.getParent()));
                }
                parentAdapter.setItems(grandGroupData);
            }
        }
    }
}
