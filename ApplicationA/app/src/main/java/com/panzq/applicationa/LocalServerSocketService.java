package com.panzq.applicationa;

import android.app.Service;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.Nullable;

public class LocalServerSocketService extends Service {
    private LocalServerSocket mlocalServerSocket = null;

    private boolean isRunning = false;
    private static final int DISCONNECT = 0x12;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISCONNECT:
                    Log.d("panzqww", "===stopSelf");
                    stopSelf();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        Log.d("panzqww", "===启动LocalServerSocketService");
        Log.d("panzqww", "===onCreate");
        super.onCreate();
        try {
            mlocalServerSocket = new LocalServerSocket("4A:4B:4C:4D:4E:4F");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                isRunning = true;
                try {
                    while (isRunning) {
                        LocalSocket accept = mlocalServerSocket.accept();
                        InputStream inputStream = accept.getInputStream();
                        BufferedReader br = null;
                        br = new BufferedReader(new InputStreamReader(inputStream));
                        String tmpStr = null;
                        while (null != (tmpStr = br.readLine())) {
                            Log.d("panzqww", "接收到tmpStr = " + tmpStr);
                            //if (tmpStr.equalsIgnoreCase("stop")) {
                            if (mHandler.hasMessages(DISCONNECT)) {
                                mHandler.removeMessages(DISCONNECT);
                            }
                            mHandler.sendEmptyMessageDelayed(DISCONNECT, 30000);
                            //}
                        }
                        br.close();
                        accept.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("panzqww", "===onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("panzqww", "===onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("panzqww", "===onDestroy");
        isRunning = false;
        try {
            mlocalServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("panzqww", "===onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("panzqww", "===onRebind");
        super.onRebind(intent);
    }
}
