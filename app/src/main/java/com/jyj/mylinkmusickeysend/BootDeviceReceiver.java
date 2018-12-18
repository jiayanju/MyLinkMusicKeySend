package com.jyj.mylinkmusickeysend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class BootDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        String message = "KeyPlay 启动了，开始愉快的玩耍吧 。。。";

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Intent backgroundService = new Intent(context, MyLinkKeyPlayService.class);
            context.startService(backgroundService);
        }
    }
}
