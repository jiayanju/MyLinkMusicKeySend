package com.jyj.mylinkmusickeysend;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MyLinkKeyPlayService extends Service {

    private static final String TAG = "MyLinkKeyPlayService";

    private MyLinkKeyReceiver myLinkKeyReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("IKeyClick.KEY_CLICK");
        intentFilter.addAction("IMixControl.MIXCONTROL_ACTION");
        intentFilter.addAction("IDeviceStatusListener.INFO_KEY");
        intentFilter.addAction("INotifyToOpenApp.NOTIFY_TO_OPEN_APP");
        intentFilter.addAction("REQUEST_OPEN_APP");
        intentFilter.addAction("IOsdDisplayAssert.OSDASSERT_ACTION");
        intentFilter.addAction("IPowerOffOrFactory.POWEROFF_FACTORY_KEY");
        intentFilter.addAction("IReceiveCmd.RECEIVE_CMD");
        intentFilter.addAction("IReqChageProp.REQ_TO_CHANGE_PROP");
        intentFilter.addAction("IDeviceStatusListener.STATUS_KEY");

        intentFilter.setPriority(2147483647);

        myLinkKeyReceiver = new MyLinkKeyReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(myLinkKeyReceiver, intentFilter);

        Log.d(TAG, "Service onCreate: MyLinkKeyReceiver is registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister screenOnOffReceiver when destroy.
        if (myLinkKeyReceiver != null) {
            unregisterReceiver(myLinkKeyReceiver);
            Log.d(TAG, "Service onDestroy: MyLinkKeyReceiver is unregistered.");
        }

        sendBroadcast(new Intent("com.jyj.restart.myLinkServcie"));
    }
}
