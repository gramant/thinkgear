package ru.gramant.thinkgear.raw.phase;

import java.util.LinkedList;
import java.util.List;

import ru.gramant.thinkgear.raw.Params;

/**
 * Created by fedor.belov on 07.11.13.
 */
public class PhaseConfig {

    public static Phase[] parseConfig(String config) {
        if (config == null || "".equals(config)) return null;

        String[] paramNames = Params.getHistoryConfigNames();
        List<Phase> phases = new LinkedList<Phase>();

        String lines[] = config.split("[\\r\\n]+");
        for (String line : lines) {
            String name = getPhaseName(line);
            String[] settings = getPhaseSettings(line);

            if (name == null || "".equals(name) || settings == null || settings.length != paramNames.length) {
                return null;
            }

            Phase phase = new Phase(name);

            for (int i = 0; i < settings.length; i++) {
                MinMax minMax = getMinMax(settings[i]);
                if (minMax == null) {
                    return null;
                } else {
                    phase.set(paramNames[i], minMax);
                }
            }

            phases.add(phase);
        }

        return (phases.size() > 0) ? phases.toArray(new Phase[phases.size()]) : null;
    }

    private static String getPhaseName(String line) {
        int pos = line.indexOf(":");
        if (pos != -1) {
            return line.substring(0, pos);
        } else {
            return null;
        }
    }

    private static String[] getPhaseSettings(String line) {
        int pos = line.indexOf(":");
        if (pos != -1) {
            return line.substring(pos + 1).trim().split(";");
        } else {
            return null;
        }
    }

    private static MinMax getMinMax(String s) {
        try {
            String[] values = s.trim().split("-");

            if (values.length == 2) {
                return new MinMax(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
