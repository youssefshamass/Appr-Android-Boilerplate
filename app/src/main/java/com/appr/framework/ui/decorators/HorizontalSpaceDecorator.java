package com.appr.framework.ui.decorators;

import android.graphics.Rect;
import android.view.View;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpaceDecorator extends RecyclerView.ItemDecoration {
    //region Private members

    private final int horizontalSpace;

    //endregion

    //region Constructor

    public HorizontalSpaceDecorator(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    //endregion

    //region ItemDecoration members

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        if (Locale.getDefault().getISO3Language().equalsIgnoreCase("ar"))
            outRect.left = horizontalSpace;
        else
            outRect.right = horizontalSpace;
    }

    //endregion
}
