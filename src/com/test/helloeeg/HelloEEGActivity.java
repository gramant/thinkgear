package com.test.helloeeg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HelloEEGActivity extends Activity {
    BluetoothAdapter bluetoothAdapter;

    TextView tv;
    LinearLayout chartContainer;
    Button b;
    private volatile Graph graph;
    private volatile DataFlusher flusher;
    private volatile Boolean log = false;

    TGDevice tgDevice;
    final boolean rawEnabled = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        chartContainer = (LinearLayout) findViewById(R.id.graph);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("");
        tv.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Alert user that Bluetooth is not available
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            /* create the TGDevice */
            tgDevice = new TGDevice(bluetoothAdapter, handler);
        }

        final Button buttonStart = (Button) findViewById(R.id.buttonStart);
        final Button buttonStop = (Button) findViewById(R.id.buttonStop);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFlusher();
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.VISIBLE);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopFlusher();
                buttonStop.setVisibility(View.GONE);
                buttonStart.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            tgDevice.close();
            stopFlusher();
        } else {
            //just orientation change
        }
    }

    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:

                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            tv.append("Connecting...\n");
                            break;
                        case TGDevice.STATE_CONNECTED:
                            tv.append("Connected.\n");
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            tv.append("Can't find\n");
                            break;
                        case TGDevice.STATE_NOT_PAIRED:
                            tv.append("not paired\n");
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            tv.append("Disconnected mang\n");
                    }

                    break;
                case TGDevice.MSG_POOR_SIGNAL:
                    //signal = msg.arg1;
                    doLog("PoorSignal: " + msg.arg1);
                    break;
                case TGDevice.MSG_RAW_DATA:
                    //raw1 = msg.arg1;
                    doLog("Got raw: " + msg.arg1);
                    doGraph("raw", msg.arg1);
                    break;
                case TGDevice.MSG_HEART_RATE:
                    doLog("Heart rate: " + msg.arg1);
                    break;
                case TGDevice.MSG_ATTENTION:
                    //att = msg.arg1;
                    doLog("Attention: " + msg.arg1);
                    //Log.v("HelloA", "Attention: " + att + "\n");
                    break;
                case TGDevice.MSG_MEDITATION:

                    break;
                case TGDevice.MSG_BLINK:
                    doLog("Blink: " + msg.arg1);
                    break;
                case TGDevice.MSG_RAW_COUNT:
                    doLog("Raw Count: " + msg.arg1);
                    break;
                case TGDevice.MSG_LOW_BATTERY:
                    Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
                    break;
                case TGDevice.MSG_RAW_MULTI:
                    TGRawMulti rawM = (TGRawMulti) msg.obj;
                    doLog("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
                    break;
                case TGDevice.MSG_EEG_POWER:
                    TGEegPower power = (TGEegPower) msg.obj;
                    doLog("Power: delta:" + power.delta + "; highAlpha:" + power.highAlpha
                            + "; highBeta:" + power.highBeta + "; lowAlpha:" + power.lowAlpha
                            + "; lowBeta:" + power.lowBeta + "; lowGamma:" + power.lowGamma
                            + "; midGamma:" + power.midGamma + "; theta: " + power.theta);
                    doGraph("delta", power.delta);
                    doGraph("highAlpha", power.highAlpha);
                    doGraph("highBeta", power.highBeta);
                    doGraph("lowAlpha", power.lowAlpha);
                    doGraph("lowBeta", power.lowBeta);
                    doGraph("lowGamma", power.lowGamma);
                    doGraph("midGamma", power.midGamma);
                    doGraph("theta", power.theta);

                    flushData(power.delta, power.highAlpha, power.highBeta, power.lowAlpha, power.lowBeta, power.lowGamma, power.midGamma, power.theta);
                    break;
                default:
                    break;
            }
        }
    };

    public void doStuff(View view) {
        if (tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);
    }

    private void doLog(String data) {
        if (log) {
            tv.append(data + "\n");
        }
    }

    private void doGraph(String param, Integer value) {
        if (graph != null) {
            graph.add(param, value);
        }
    }

    private void startFlusher() {
        flusher = new DataFlusher(getFileName());
        flusher.start();
        flushData("delta", "highAlpha", "highBeta", "lowAlpha", "lowBeta", "lowGamma", "midGamma", "theta");
    }

    private void stopFlusher() {
        if (flusher != null) {
            flusher.stop();
            flusher = null;
        }
    }

    public synchronized void onLogClick(View view) {
        Button b = (Button) view;
        log = !log;

        if (log) {
            b.setText("Hide data");
        } else {
            b.setText("Show data");
        }
    }

    public synchronized void onGraphClick(View view) {
        Button b = (Button) view;

        if (graph == null) {
            Graph graph = new Graph(this);
            chartContainer.addView(graph.start());

            this.graph = graph;
            b.setText("Hide graph");
        } else {
            chartContainer.removeAllViews();
            graph = null;
            b.setText("Show graph");
        }
    }

    private void flushData(Object delta, Object highAlpha, Object highBeta, Object lowAlpha, Object lowBeta, Object lowGamma, Object midGamma, Object theta) {
        if (flusher != null) flusher.add(combine(new Object[]{delta, highAlpha, highBeta, lowAlpha, lowBeta, lowGamma, midGamma, theta}, ";"));
    }

    private String getFileName() {
        Date now = new Date();
        String date = dateFormat.format(now);
        String time = timeFormat.format(now);
        String androidId = (Build.MODEL + "-" + Build.VERSION.RELEASE);
        String bluetoothName = getTargetBluetoothName();

        return FileNameCleaner.cleanFileName(combine(new Object[]{date, androidId, bluetoothName, time}, "-") + ".txt");
    }

    private String combine(Object[] s, String glue)
    {
        int k=s.length;
        if (k==0)
            return null;
        StringBuilder out=new StringBuilder();
        out.append(s[0].toString());
        for (int x=1;x<k;++x)
            out.append(glue).append(s[x].toString());
        return out.toString();
    }

    //http://stackoverflow.com/questions/6662216/display-android-bluetooth-device-name
    public String getTargetBluetoothName(){
        BluetoothDevice device = tgDevice.getConnectedDevice();
        String name = (device != null) ? tgDevice.getConnectedDevice().getName() : null;
        return (name != null) ? name : "-";
    }
}