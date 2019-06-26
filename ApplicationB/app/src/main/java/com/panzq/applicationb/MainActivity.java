package com.panzq.applicationb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements SafeHandler.Callback {


    LocalSocket lsocket = null;
    LocalSocketAddress addr = new LocalSocketAddress("4A:4B:4C:4D:4E:4F");
    BufferedWriter br;
    SafeHandler handler;

    private static final int HANDLER_SENDMESSAGE = 0x100;
    private boolean isVideoConferenceApkExist = false;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("panzqww", "B --- onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("panzqww", "B --- onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("panzqww", "B --- onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("panzqww", "B --- onRestoreInstanceState");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button starActivityA = findViewById(R.id.startAcitvityA);
        handler = new SafeHandler(this);
        Log.d("panzqww", "B --- onCreate");
        isVideoConferenceApkExist = checkApkExist(this, "com.panzq.applicationa");

        starActivityA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVideoConferenceApkExist) {
                    Intent intent = new Intent();
                    intent.putExtra("whiteboardAction", true);
                    intent.setComponent(new ComponentName("com.panzq.applicationa",
                            "com.panzq.applicationa.MainActivity"));
                    startActivity(intent);
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("panzqww", Thread.currentThread().getName() + "2222222");
                                    handler.sendMessageOnly(HANDLER_SENDMESSAGE, 2000);
                                }
                            }
                    ).start();
                }
            }
        });
    }

    private void sendMessage() {
        Log.d("panzqww", "BBB====sendMessage=====");
        try {
            lsocket = new LocalSocket();
            lsocket.connect(addr);
            String result;
           /* BufferedReader reader = new BufferedReader(new InputStreamReader(lsocket.getInputStream()));
            while (null != (result = reader.readLine())) {
                Log.d("panzqww", "BBBBB === 接收到" + result);
            }*/
            br = new BufferedWriter(new OutputStreamWriter(lsocket.getOutputStream()));
            br.write(Thread.currentThread().getName() + "发送:" + (System.currentTimeMillis()));
            br.newLine();
            br.flush();
            br.close();
            //reader.close();
            lsocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_SENDMESSAGE:
                sendMessage();
                handler.sendMessageOnly(HANDLER_SENDMESSAGE, 2000);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("panzqww", "stop");
        Log.d("panzqww", "B --- onDestroy");
        handler.removeCallbacksAndMessages(null);

    }

    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
