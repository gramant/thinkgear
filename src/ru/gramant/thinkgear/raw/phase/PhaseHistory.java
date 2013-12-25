package ru.gramant.thinkgear.raw.phase;

import com.neurosky.thinkgear.TGDevice;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

import ru.gramant.thinkgear.raw.DataFlusher;
import ru.gramant.thinkgear.raw.Params;
import ru.gramant.thinkgear.raw.utils.FileNameUtils;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class PhaseHistory {

    private Integer CHECK_FOR_EVENTS_EACH_MS = 3000;
    private DataFlusher flusher;
    private Phase[] phases;
    private String config;
    private boolean multipleActive = false;
    private boolean zeroActive = false;
    private volatile boolean noDataForLastSeconds = false;
    private long lastEventTime = -1;

    public PhaseHistory(Phase[] phases, String config) {
        this.phases = phases;
        this.config = config;

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(CHECK_FOR_EVENTS_EACH_MS);

                        if (isActive() && (System.currentTimeMillis() - lastEventTime) > CHECK_FOR_EVENTS_EACH_MS) {
                            if (!noDataForLastSeconds) {
                                flushString("no data for last 3 secs");
                                noDataForLastSeconds = true;
                            }
                        } else {
                            noDataForLastSeconds = false;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public synchronized void start(TGDevice device) {
        lastEventTime = System.currentTimeMillis();
        flusher = new DataFlusher(FileNameUtils.getHistoryFileName(device));
        flusher.start();
        flusher.addWithoutTime(config);
        flusher.addWithoutTime("----------------------");

        //reset phases state
        for (Phase phase : phases) {
            phase.setActive(false);
        }
    }

    public synchronized void stop() {
        flusher.stop();
    }

    public synchronized void flushData(Params params) {
        lastEventTime = System.currentTimeMillis();

        List<String> active = new LinkedList<String>();

        for (Phase phase : phases) {
            if (phase.checkStateChange(params)) {
                flushEvent(phase.getName(), (phase.isActive()) ? "ENTER" : "EXIT");
            }

            if (phase.isActive()) active.add(phase.getName());
        }

        if (active.size() == 0) {
            if (!zeroActive) {
                flushString("Zero active states!");
                zeroActive = true;
            }
        } else if (active.size() == 1) {
            multipleActive = false;
            zeroActive = false;
        } else {
            if (!multipleActive) {
                flushString("Multiple active states: " + StringUtils.join(active, ", "));
                multipleActive = true;
            }
        }
    }

    public synchronized void flushString(String message) {
        if (isActive()) flusher.add(message);
    }

    private boolean isActive() {
        return flusher != null && flusher.isActive();
    }

    private void flushEvent(String phase, String action) {
        flushString(phase + ": " + action);
    }

}
