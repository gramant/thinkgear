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
        flusher = new DataFlusher(FileNameUtils.getFileName(device, "history"));
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

    private void flushEvent(String phase, String action) {
        if (flusher != null && flusher.isActive()) flusher.add(phase + ": " + action);
    }

}
