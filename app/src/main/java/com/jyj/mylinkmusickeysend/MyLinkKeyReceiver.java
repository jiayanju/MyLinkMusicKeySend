package com.jyj.mylinkmusickeysend;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class MyLinkKeyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyLinkKeyReceiver";

    private static final String KEY_CLICK_ACTION = "IKeyClick.KEY_CLICK";

    private static final String CLICK_ACTION_KEY_PARAM = "CLICK_KEY";

    private static final String CLICK_ACTION_STATUS_PARAM = "STATUS_KEY";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Receive action: " + intent.getAction());
//        Toast.makeText(context, "Receive action: " + intent.getAction(), Toast.LENGTH_SHORT).show();

        if (KEY_CLICK_ACTION.equals(intent.getAction())) {
            int keyValue = intent.getIntExtra(CLICK_ACTION_KEY_PARAM, -1);
            int statusValue = intent.getIntExtra(CLICK_ACTION_STATUS_PARAM, -1);

            Log.d(TAG, "Click " + keyValue + " Status: " + statusValue);
//            Toast.makeText(context, "Click " + keyValue + " Status: " + statusValue, Toast.LENGTH_SHORT).show();

            if (keyValue == 5) {
//                ShellCmd.getInstance().sendInputKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
            } else if (keyValue == 6) {
//                ShellCmd.getInstance().sendInputKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            } else if (keyValue == 32) {
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            }
            abortBroadcast();
        }

    }

    public void sendKeyEvent(final int paramInt) {
        new Thread() {
            public void run() {
                Log.d("sendKeyEvent", "=============Start===============");
                try {
                    Instrumentation instrumentation = new Instrumentation();
                    instrumentation.sendKeyDownUpSync(paramInt);
                } catch (Exception localException) {
                    Log.e(TAG, localException.getMessage());
                }
            }
        }.start();
    }
}
