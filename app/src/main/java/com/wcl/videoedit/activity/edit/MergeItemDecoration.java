package com.wcl.videoedit.activity.edit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wcl.videoedit.R;

/**
 * Created by wangchunlong on 2018/7/17.
 */

public class MergeItemDecoration extends RecyclerView.ItemDecoration {

    Drawable drawableDivider;

    public MergeItemDecoration(Context context) {
        drawableDivider = context.getResources().getDrawable(R.drawable.ic_merge_line);
        drawableDivider.setColorFilter(context.getResources().getColor(R.color.text_normal), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            View childAt = parent.getChildAt(i);
            int childAdapterPosition = parent.getChildAdapterPosition(childAt);
            if(childAdapterPosition != 0){
                int left = childAt.getLeft() - drawableDivider.getIntrinsicWidth();
                int top = childAt.getTop() + (childAt.getHeight()/2 - drawableDivider.getIntrinsicHeight() / 2);
                drawableDivider.setBounds(left, top, drawableDivider.getIntrinsicWidth() + left, drawableDivider.getIntrinsicHeight() + top);
                drawableDivider.draw(c);
            }
            else {
                //do nothing
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        if(childAdapterPosition != 0){
            outRect.set(drawableDivider.getIntrinsicWidth(), 0, 0, 0);
        }
        else {
            outRect.set(0, 0, 0, 0);
        }
    }
}
