package com.wcl.videoedit.utils;

import android.app.Application;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by wangchunlong on 2018/5/22.
 */

public class AppUtils {
    private static Application mApplication;
    public static Application getApplication() {
        if (mApplication == null) {
            try {
                Class activityThreadClazz = Class.forName("android.app.ActivityThread");
                Method method = activityThreadClazz.getMethod("currentActivityThread");
                Object activityThreadObj = method.invoke(activityThreadClazz, new Object[0]);
                Class activityThreadCls = activityThreadObj.getClass();
                Field field = activityThreadCls.getDeclaredField("mInitialApplication");
                field.setAccessible(true);
                mApplication = (Application) field.get(activityThreadObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mApplication;
    }

}
