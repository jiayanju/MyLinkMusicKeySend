package com.jyj.mylinkmusickeysend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);


        setTitle("Key Play");

        Intent backgroundService = new Intent(getApplicationContext(), MyLinkKeyPlayService.class);
        startService(backgroundService);

        Button meidaNext = (Button) findViewById(R.id.media_next);
        meidaNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ShellCmd.getInstance().sendInputKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                Intent intent = new Intent("IKeyClick.KEY_CLICK");
                intent.putExtra("CLICK_KEY", 6);
                intent.putExtra("STATUS_KEY", 0);
                sendBroadcast(intent);
            }
        });

        Button mediaPrevious = (Button) findViewById(R.id.media_previous);
        mediaPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShellCmd.getInstance().sendInputKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            }
        });

        Log.d(MainActivity.class.getSimpleName(), "onCreate ");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(MainActivity.class.getSimpleName(), "onDestroy");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
