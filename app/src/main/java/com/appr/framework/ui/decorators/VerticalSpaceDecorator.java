package com.appr.framework.ui.decorators;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpaceDecorator extends RecyclerView.ItemDecoration {
    //region Private members

    private final int verticalSpaceTop;
    private final int verticalSpaceBottom;

    //endregion

    //region Constructor

    public VerticalSpaceDecorator(int verticalSpaceTop, int verticalSpaceBottom) {
        this.verticalSpaceTop = verticalSpaceTop;
        this.verticalSpaceBottom = verticalSpaceBottom;
    }

    //endregion

    //region ItemDecoration members

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int itemCount = state.getItemCount();

        final int itemPosition = parent.getChildAdapterPosition(view);

        // no position, leave it alone
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // first item
        if (itemPosition == 0) {
            outRect.bottom = verticalSpaceBottom;
        }
        // last item
        else if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.top = verticalSpaceTop;
        }
        // every other item
        else {
            outRect.top = verticalSpaceTop;
            outRect.bottom = verticalSpaceBottom;
        }
    }

    //endregion
}
