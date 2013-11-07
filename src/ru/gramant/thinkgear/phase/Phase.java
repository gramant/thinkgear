package ru.gramant.thinkgear.phase;

import ru.gramant.thinkgear.Params;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class Phase {

    String name;
    boolean isActive = true;
    MinMax delta;
    MinMax highAlpha;
    MinMax highBeta;
    MinMax lowAlpha;
    MinMax lowBeta;
    MinMax lowGamma;
    MinMax midGamma;
    MinMax theta;
    MinMax attention;
    MinMax meditation;

    public Phase(String name) {
        this.name = name;
    }

    /**
     * @return true when state was changed
     */
    public boolean checkStateChange(Params params) {
        boolean fit = isFitValues(params);
        if (fit != isActive) {
            isActive = fit;
            return true;
        } else {
            return false;
        }
    }

    private boolean isFitValues(Params params) {
        return delta.fit(params.delta) &&
                highAlpha.fit(params.highAlpha) &&
                highBeta.fit(params.highBeta) &&
                lowAlpha.fit(params.lowAlpha) &&
                lowBeta.fit(params.lowBeta) &&
                lowGamma.fit(params.lowGamma) &&
                midGamma.fit(params.midGamma) &&
                theta.fit(params.theta) &&
                attention.fit(params.attention) &&
                meditation.fit(params.meditation);
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void set(String name, MinMax value) {
        if ("delta".equals(name)) {
            delta = value;
        } else if ("highAlpha".equals(name)) {
            highAlpha = value;
        } else if ("highBeta".equals(name)) {
            highBeta = value;
        } else if ("lowAlpha".equals(name)) {
            lowAlpha = value;
        } else if ("lowBeta".equals(name)) {
            lowBeta = value;
        } else if ("lowGamma".equals(name)) {
            lowGamma = value;
        } else if ("midGamma".equals(name)) {
            midGamma = value;
        } else if ("theta".equals(name)) {
            theta = value;
        } else if ("attention".equals(name)) {
            attention = value;
        } else if ("meditation".equals(name)) {
            meditation = value;
        }
    }
}
