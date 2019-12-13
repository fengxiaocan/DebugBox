package com.box.libs.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by linjiang on 07/06/2018.
 */

public class MenuRecyclerView extends RecyclerView {
    private RvContextMenuInfo contextMenuInfo;

    public MenuRecyclerView(Context context) {
        super(context);
    }

    public MenuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return contextMenuInfo;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        // only valid for the direct child
        if (indexOfChild(originalView) == -1) {
            return false;
        }
        final int position = getChildAdapterPosition(originalView);
        if (position >= 0) {
            final long itemId = getAdapter().getItemId(position);
            contextMenuInfo = new RvContextMenuInfo(originalView, position, itemId);
            return super.showContextMenuForChild(originalView);
        }
        return false;
    }

    public class RvContextMenuInfo implements ContextMenu.ContextMenuInfo {

        public View targetView;
        public int position;
        public long id;
        public RvContextMenuInfo(View targetView, int position, long id) {
            this.targetView = targetView;
            this.position = position;
            this.id = id;
        }
    }
}
