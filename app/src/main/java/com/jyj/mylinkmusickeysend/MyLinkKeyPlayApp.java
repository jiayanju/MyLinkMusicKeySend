package com.jyj.mylinkmusickeysend;

import android.app.Application;

public class MyLinkKeyPlayApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Thread.setDefaultUncaughtExceptionHandler(CrashExceptionHandler.getInstance(this));
        CrashCatchHandler crashCatchHandler = CrashCatchHandler.getInstance();//获得单例
        crashCatchHandler.init(getApplicationContext());//初始化,传入context

    }
}
