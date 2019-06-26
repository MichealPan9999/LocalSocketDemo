package com.panzq.applicationa;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

/**
 * LocalSocket 服务端
 */
public class MainActivity extends AppCompatActivity {


    private LocalServerSocket mlocalServerSocket = null;

    private boolean isRunning = false;

    private long recvtTime = SystemClock.elapsedRealtime();
    private TimerTask timerTask;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("panzqww", "AAAA###onCreate");
        setContentView(R.layout.activity_main);
        try {
            mlocalServerSocket = new LocalServerSocket("4A:4B:4C:4D:4E:4F");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        isRunning = false;
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("panzqww", "AAA####时间间隔+++++" + (SystemClock.elapsedRealtime() - recvtTime));
                if ((SystemClock.elapsedRealtime() - recvtTime) > 10000) {
                    MainActivity.this.finish();
                    System.gc();
                    cancel();
                }
            }
        };
        if (getIntent().getBooleanExtra("whiteboardAction", false)) {
            recvtTime = SystemClock.elapsedRealtime();
            //timer.schedule(timerTask, 200, 1000);
            startLocalSocketService();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("panzqww", "AAA###onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("panzqww", "AAA###onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("panzqww", "AAAA###onDestroy");
        isRunning = false;
        System.gc();
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    private void startLocalSocketService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                try {
                    while (true) {
                        LocalSocket accept = mlocalServerSocket.accept();
                        Log.d("panzqww", "========连接成功========");
                        InputStream inputStream = accept.getInputStream();
                        BufferedWriter bw = null;
                        BufferedReader br = null;
                        br = new BufferedReader(new InputStreamReader(inputStream));
                        bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("log.txt", MODE_PRIVATE)));
                        String tmpStr = null;
                        while (null != (tmpStr = br.readLine())) {
                            bw.write(tmpStr);
                            bw.newLine();
                            Log.d("panzqww", "接收数据：tmpStr======" + tmpStr);
                        }
                        bw.flush();
                        bw.close();
                        br.close();
                        accept.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
