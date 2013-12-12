package ru.gramant.thinkgear.phase;

import ru.gramant.thinkgear.Params;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class Phase {

    String name;
    boolean isActive = false;
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
        return delta.fit(params.delta.raw) &&
                highAlpha.fit(params.highAlpha.raw) &&
                highBeta.fit(params.highBeta.raw) &&
                lowAlpha.fit(params.lowAlpha.raw) &&
                lowBeta.fit(params.lowBeta.raw) &&
                lowGamma.fit(params.lowGamma.raw) &&
                midGamma.fit(params.midGamma.raw) &&
                theta.fit(params.theta.raw) &&
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Phase phase = (Phase) o;

        if (isActive != phase.isActive) return false;
        if (attention != null ? !attention.equals(phase.attention) : phase.attention != null)
            return false;
        if (delta != null ? !delta.equals(phase.delta) : phase.delta != null) return false;
        if (highAlpha != null ? !highAlpha.equals(phase.highAlpha) : phase.highAlpha != null)
            return false;
        if (highBeta != null ? !highBeta.equals(phase.highBeta) : phase.highBeta != null)
            return false;
        if (lowAlpha != null ? !lowAlpha.equals(phase.lowAlpha) : phase.lowAlpha != null)
            return false;
        if (lowBeta != null ? !lowBeta.equals(phase.lowBeta) : phase.lowBeta != null) return false;
        if (lowGamma != null ? !lowGamma.equals(phase.lowGamma) : phase.lowGamma != null)
            return false;
        if (meditation != null ? !meditation.equals(phase.meditation) : phase.meditation != null)
            return false;
        if (midGamma != null ? !midGamma.equals(phase.midGamma) : phase.midGamma != null)
            return false;
        if (!name.equals(phase.name)) return false;
        if (theta != null ? !theta.equals(phase.theta) : phase.theta != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + (delta != null ? delta.hashCode() : 0);
        result = 31 * result + (highAlpha != null ? highAlpha.hashCode() : 0);
        result = 31 * result + (highBeta != null ? highBeta.hashCode() : 0);
        result = 31 * result + (lowAlpha != null ? lowAlpha.hashCode() : 0);
        result = 31 * result + (lowBeta != null ? lowBeta.hashCode() : 0);
        result = 31 * result + (lowGamma != null ? lowGamma.hashCode() : 0);
        result = 31 * result + (midGamma != null ? midGamma.hashCode() : 0);
        result = 31 * result + (theta != null ? theta.hashCode() : 0);
        result = 31 * result + (attention != null ? attention.hashCode() : 0);
        result = 31 * result + (meditation != null ? meditation.hashCode() : 0);
        return result;
    }
}
