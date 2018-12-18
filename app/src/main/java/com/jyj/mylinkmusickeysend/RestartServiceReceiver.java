package com.jyj.mylinkmusickeysend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent backgroundService = new Intent(context, MyLinkKeyPlayService.class);
        context.startService(backgroundService);
    }
}
