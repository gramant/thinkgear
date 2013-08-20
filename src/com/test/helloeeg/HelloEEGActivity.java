package com.test.helloeeg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HelloEEGActivity extends Activity {
    BluetoothAdapter bluetoothAdapter;

    TextView tv;
    Button b;
    Graph graph;
    DataFlusher flusher;

    TGDevice tgDevice;
    final boolean rawEnabled = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        graph = new Graph(this);
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
    }

    @Override
    public void onDestroy() {
        tgDevice.close();
        if (flusher != null) flusher.stop();
        super.onDestroy();
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
                    tv.append("PoorSignal: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_RAW_DATA:
                    //raw1 = msg.arg1;
                    tv.append("Got raw: " + msg.arg1 + "\n");
                    graph.add("raw", msg.arg1);
                    break;
                case TGDevice.MSG_HEART_RATE:
                    tv.append("Heart rate: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_ATTENTION:
                    //att = msg.arg1;
                    tv.append("Attention: " + msg.arg1 + "\n");
                    //Log.v("HelloA", "Attention: " + att + "\n");
                    break;
                case TGDevice.MSG_MEDITATION:

                    break;
                case TGDevice.MSG_BLINK:
                    tv.append("Blink: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_RAW_COUNT:
                    tv.append("Raw Count: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_LOW_BATTERY:
                    Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
                    break;
                case TGDevice.MSG_RAW_MULTI:
                    TGRawMulti rawM = (TGRawMulti) msg.obj;
                    tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
                    break;
                case TGDevice.MSG_EEG_POWER:
                    TGEegPower power = (TGEegPower) msg.obj;
                    tv.append("Power: delta:" + power.delta + "; highAlpha:" + power.highAlpha
                            + "; highBeta:" + power.highBeta + "; lowAlpha:" + power.lowAlpha
                            + "; lowBeta:" + power.lowBeta + "; lowGamma:" + power.lowGamma
                            + "; midGamma:" + power.midGamma + "; theta: " + power.theta + "\n");
                    graph.add("delta", power.delta);
                    graph.add("highAlpha", power.highAlpha);
                    graph.add("highBeta", power.highBeta);
                    graph.add("lowAlpha", power.lowAlpha);
                    graph.add("lowBeta", power.lowBeta);
                    graph.add("lowGamma", power.lowGamma);
                    graph.add("midGamma", power.midGamma);
                    graph.add("theta", power.theta);

                    flusher.add(combine(new Object[]{power.delta, power.highAlpha, power.highBeta, power.lowAlpha, power.lowBeta, power.lowGamma, power.midGamma, power.theta}, ";"));
                    break;
                default:
                    break;
            }
        }
    };

    public void doStuff(View view) {
        if (tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);

        flusher = new DataFlusher(this, System.currentTimeMillis() + ".txt");
        flusher.start();

        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.graph);
        chartContainer.addView(graph.start());
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
}