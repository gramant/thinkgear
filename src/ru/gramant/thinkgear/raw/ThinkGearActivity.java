package ru.gramant.thinkgear.raw;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ru.gramant.thinkgear.raw.phase.Phase;
import ru.gramant.thinkgear.raw.phase.PhaseConfig;
import ru.gramant.thinkgear.raw.phase.PhaseHistory;
import ru.gramant.thinkgear.raw.utils.FileNameUtils;
import ru.gramant.thinkgear.raw.utils.FormatUtils;

public class ThinkGearActivity extends Activity {
    PowerLock lock;
    BluetoothAdapter bluetoothAdapter;

    TextView tv;
    LinearLayout chartContainer;
    Button b;
    private volatile Graph graph;
    private volatile PhaseHistory history;
    private volatile DataFlusher flusher;
    private volatile Boolean log = false;

    TGDevice tgDevice;
    final boolean rawEnabled = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lock = new PowerLock(this);

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
                startHistory();
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.VISIBLE);
                lock.acquire();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopFlusher();
                stopHistory();
                buttonStop.setVisibility(View.GONE);
                buttonStart.setVisibility(View.VISIBLE);
                lock.release();
            }
        });

        initPhaseHistoryOrAlertError();
    }

    private void initPhaseHistoryOrAlertError() {
        String message = null;

        try {
            String fileName = "config.txt";
            String fileFullPath = App.ROOT_FOLDER + "/" + fileName;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + App.ROOT_FOLDER, fileName);

            if (!file.exists()) {
                message = "Config file " + fileFullPath + " is not found";
            } else {
                String config = FileUtils.readFileToString(file);
                if (config == null || config.equals("")) {
                    message = "Config file " + fileFullPath + " is empty";
                } else {
                    Phase[] phases = PhaseConfig.parseConfig(config);
                    if (phases == null) {
                        message = "Unable to parse config file " + fileFullPath;
                    } else {
                        history = new PhaseHistory(phases, config);
                    }
                }
            }
        } catch (Exception e) {
            message = "Exception on reading history phase config file - " + e.getMessage();
            e.printStackTrace();
        }

        if (message != null && !message.equals("")) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Unable to start phase history");
            alert.setMessage(message + "\n" + "Phase history file will not be saved!");
            alert.setNegativeButton("OK", null);
            alert.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            tgDevice.close();
            lock.release();
            stopFlusher();
        } else {
            //just orientation change
        }
    }

    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {

        private int LAST_ATTENTION = -1;
        private int LAST_MEDITATION = -1;
        private int LAST_RAW_COUNT = -1;
        private int LAST_POOR_SIGNAL = -1;
        private int LAST_RAW_VALUE = -1;
        private int LAST_HEART_RATE = -1;
        private int LAST_RRINT = -1;
        private int LAST_SLEEP_STAGE = -1;
        private final List<Integer> BLINK_EMPTY = Arrays.asList(0);
        private List<Integer> blink = new LinkedList<Integer>();
        private Params params = new Params();
        private TGEegPower LAST_POWER = new TGEegPower(0, 0, 0, 0, 0, 0, 0, 0);

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
                        case TGDevice.STATE_DISCONNECTED:
                            tv.append("Disconnected mang\n");
                            reconnectDevice();
                        default:
                            tv.append("Other MSG_STATE_CHANGE - " + msg.arg1);
                            break;
                    }

                    break;
                case TGDevice.MSG_POOR_SIGNAL:
                    //signal = msg.arg1;
                    doLog("PoorSignal: " + msg.arg1);
                    LAST_POOR_SIGNAL = msg.arg1;
                    break;
//                case TGDevice.MSG_ZONE:
//                    //don't really know what's this
//                    break;
                case TGDevice.MSG_RAW_DATA:
//                    raw1 = msg.arg1;
                    doLog("Got raw: " + msg.arg1);
                    doGraph("raw", msg.arg1);
                    LAST_RAW_VALUE = msg.arg1;

                    doFlushData();
                    break;
                case TGDevice.MSG_HEART_RATE:
                    doLog("Heart rate: " + msg.arg1);
                    LAST_HEART_RATE = msg.arg1;
                    break;
//                case TGDevice.MSG_EKG_RRINT:
//                    doLog("R-R Interval: " + msg.arg1);
//                    LAST_RRINT = msg.arg1;
//                    break;
                case TGDevice.MSG_ATTENTION:
                    //att = msg.arg1;
                    doLog("Attention: " + msg.arg1);
                    LAST_ATTENTION = msg.arg1;
                    break;
                case TGDevice.MSG_MEDITATION:
                    doLog("Mediation: " + msg.arg1);
                    LAST_MEDITATION = msg.arg1;
                    break;
                case TGDevice.MSG_BLINK:
                    doLog("Blink: " + msg.arg1);
                    blink.add(msg.arg1);
                    break;
                case TGDevice.MSG_RAW_COUNT:
                    doLog("Raw Count: " + msg.arg1);
                    LAST_RAW_COUNT = msg.arg1;
                    break;
                case TGDevice.MSG_SLEEP_STAGE:
                    doLog("Sleep stage: " + msg.arg1);
                    LAST_SLEEP_STAGE = msg.arg1;
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
                    LAST_POWER = power;
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
                    break;
                default:
                    tv.append("Other message - " + msg.what + "\n");
                    break;
            }
        }

        private void doFlushData() {
            params.flush(
                    LAST_POWER.delta,
                    LAST_POWER.highAlpha,
                    LAST_POWER.highBeta,
                    LAST_POWER.lowAlpha,
                    LAST_POWER.lowBeta,
                    LAST_POWER.lowGamma,
                    LAST_POWER.midGamma,
                    LAST_POWER.theta,
                    LAST_ATTENTION,
                    LAST_MEDITATION,
                    (blink.size() == 0) ? BLINK_EMPTY : blink,
                    LAST_RAW_COUNT,
                    LAST_RAW_VALUE,
                    LAST_POOR_SIGNAL,
                    LAST_SLEEP_STAGE
            );

            flushData(params);

            blink.clear();
            LAST_HEART_RATE = 0;
            LAST_RRINT = 0;
            LAST_SLEEP_STAGE = 0;
        }
    };

    private void reconnectDevice() {
        int tries = 120; //1 hour to reconnect
        boolean first = true;

        while (tries > 0) {
            boolean connecting = false;

            if (tgDevice.getState() == TGDevice.STATE_CONNECTED) {
                return;
            } else if (tgDevice.getState() == TGDevice.STATE_CONNECTING) {
                connecting = true;
            } else {
                tgDevice.connect(rawEnabled);
            }

            if (!connecting) {
                if (!first) {
                    flushHistoryString("failed to reconnect device");
                }

                tries--;
                first = false;
            }

            try {
                Thread.sleep(1000*30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        flushHistoryString("Unable to reconnect to device!");
    }

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
        flusher = new DataFlusher(FileNameUtils.getFileName(tgDevice, "log", rawEnabled));
        flusher.start();
        flusher.add(FormatUtils.arrayToString(Params.getLogParamNames(), ";"));
    }

    private void startHistory() {
        if (history != null) history.start(tgDevice);
    }

    private void stopFlusher() {
        if (flusher != null) {
            flusher.stop();
            flusher = null;
        }
    }

    private void stopHistory() {
        if (history != null) history.stop();
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

    private void flushData(Params params) {
        if (flusher != null) flusher.add(FormatUtils.arrayToString(params.getLogParams(), ";"));
        if (history != null) history.flushData(params);
    }

    private void flushData(String data) {
        if (flusher != null) flusher.add(data);
    }

    private void flushHistoryString(String message) {
        if (history != null) history.flushString(message);
    }
}