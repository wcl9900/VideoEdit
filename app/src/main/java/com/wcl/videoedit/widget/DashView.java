package com.wcl.videoedit.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangchunlong on 2018/7/17.
 */

public class DashView extends View {
    public DashView(Context context) {
        super(context);
        init();
    }

    public DashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
}
