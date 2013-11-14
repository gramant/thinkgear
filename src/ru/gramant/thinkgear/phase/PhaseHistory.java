package ru.gramant.thinkgear.phase;

import com.neurosky.thinkgear.TGDevice;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

import ru.gramant.thinkgear.DataFlusher;
import ru.gramant.thinkgear.Params;
import ru.gramant.thinkgear.utils.FileNameUtils;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class PhaseHistory {

    private DataFlusher flusher;
    private Phase[] phases;
    private String config;
    private boolean multipleActive = false;
    private boolean zeroActive = false;

    public PhaseHistory(Phase[] phases, String config) {
        this.phases = phases;
        this.config = config;
    }

    public synchronized void start(TGDevice device) {
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
        if (flusher != null && flusher.isActive()) flusher.add(message);
    }

    private void flushEvent(String phase, String action) {
        flushString(phase + ": " + action);
    }

}
