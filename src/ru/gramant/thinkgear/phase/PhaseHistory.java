package ru.gramant.thinkgear.phase;

import com.neurosky.thinkgear.TGDevice;

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

    public void start(TGDevice device) {
        flusher = new DataFlusher(FileNameUtils.getFileName(device, "history"));
        flusher.start();
        flusher.addWithoutTime(config);
        flusher.addWithoutTime("----------------------");
    }

    public void stop() {
        flusher.stop();
    }

    public void flushData(Params params) {
        for (Phase phase : phases) {
            if (phase.checkStateChange(params)) {
                flushEvent(phase.getName(), (phase.isActive()) ? "ENTER" : "EXIT");
            }
        }
    }

    private void flushEvent(String phase, String action) {
        if (flusher.isActive()) flusher.add(phase + ": " + action);
    }

    public static Phase[] parseConfig(String config) {
        return null; //todo
    }
}
