package ru.gramant.thinkgear;

import org.apache.commons.lang.ArrayUtils;

import java.util.List;

import ru.gramant.thinkgear.utils.FormatUtils;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class Params {

    public int delta;
    public int highAlpha;
    public int highBeta;
    public int lowAlpha;
    public int lowBeta;
    public int lowGamma;
    public int midGamma;
    public int theta;
    public int attention;
    public int meditation;
    public List<Integer> blink;
    public int rawCount;
    public int rawValue;
    public int poorSignal;
    public int sleepStage;

    public Params(int delta, int highAlpha, int highBeta, int lowAlpha, int lowBeta, int lowGamma, int midGamma, int theta, int attention, int meditation, List<Integer> blink, int rawCount, int rawValue, int poorSignal, int sleepStage) {
        this.delta = delta;
        this.highAlpha = highAlpha;
        this.highBeta = highBeta;
        this.lowAlpha = lowAlpha;
        this.lowBeta = lowBeta;
        this.lowGamma = lowGamma;
        this.midGamma = midGamma;
        this.theta = theta;
        this.attention = attention;
        this.meditation = meditation;
        this.blink = blink;
        this.rawCount = rawCount;
        this.rawValue = rawValue;
        this.poorSignal = poorSignal;
        this.sleepStage = sleepStage;
    }

    public static String[] getLogParamNames() {
        return new String[]{"delta", "highAlpha", "highBeta", "lowAlpha", "lowBeta", "lowGamma", "midGamma", "theta", "attention", "meditation", "rawCount", "rawValue", "poorSignal", "sleepStage", "blink"};
    }

    public Integer[] getLogParams() {
        return (Integer[]) ArrayUtils.addAll(new Integer[]{delta, highAlpha, highBeta, lowAlpha, lowBeta, lowGamma, midGamma, theta, attention, meditation, rawCount, rawValue, poorSignal, sleepStage}, blink.toArray(new Integer[blink.size()]));
    }

    public static String[] getHistoryConfigNames() {
        return new String[]{"delta", "highAlpha", "highBeta", "lowAlpha", "lowBeta", "lowGamma", "midGamma", "theta", "attention", "meditation"};
    }
}
