package ru.gramant.thinkgear.phase;

import com.neurosky.thinkgear.TGDevice;

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
        for (Phase phase : phases) {
            if (phase.checkStateChange(params)) {
                flushEvent(phase.getName(), (phase.isActive()) ? "ENTER" : "EXIT");
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
