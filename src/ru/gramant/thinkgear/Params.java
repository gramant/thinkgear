package ru.gramant.thinkgear;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.gramant.thinkgear.data.LimitedQueueValue;
import ru.gramant.thinkgear.utils.CollectionUtils;
import ru.gramant.thinkgear.utils.FormatUtils;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class Params {

    private static int STAGE_SECONDS = 30;
    private static int SLEEP_STAGE_SECONDS = 30;
    private static int STAGE_10_SECONDS = 300;
    private int sleepStageCount = -1;

    public LimitedQueueValue delta = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue delta_10 = new LimitedQueueValue(STAGE_10_SECONDS);
    public LimitedQueueValue theta = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue theta_10 = new LimitedQueueValue(STAGE_10_SECONDS);
    public LimitedQueueValue lowAlpha = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue lowAlpha_10 = new LimitedQueueValue(STAGE_10_SECONDS);
    public LimitedQueueValue highAlpha = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue highAlpha_10 = new LimitedQueueValue(STAGE_10_SECONDS);
    public LimitedQueueValue lowBeta = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue lowBeta_10 = new LimitedQueueValue(STAGE_10_SECONDS);
    public LimitedQueueValue highBeta = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue highBeta_10 = new LimitedQueueValue(STAGE_10_SECONDS);
    public LimitedQueueValue lowGamma = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue lowGamma_10 = new LimitedQueueValue(STAGE_10_SECONDS);
    public LimitedQueueValue midGamma = new LimitedQueueValue(STAGE_SECONDS);
    public LimitedQueueValue midGamma_10 = new LimitedQueueValue(STAGE_10_SECONDS);

    public int attention;
    public int meditation;
    public List<Integer> blink;
    public int rawCount;
    public int rawValue;
    public int poorSignal;
    public int sleepStage;

    public Params() {
    }

    public void flush(int delta, int highAlpha, int highBeta, int lowAlpha, int lowBeta, int lowGamma, int midGamma, int theta, int attention, int meditation, List<Integer> blink, int rawCount, int rawValue, int poorSignal, int sleepStage) {
        this.delta.add(delta);
        this.delta_10.add(delta);
        this.highAlpha.add(highAlpha);
        this.highAlpha_10.add(highAlpha);
        this.highBeta.add(highBeta);
        this.highBeta_10.add(highBeta);
        this.lowAlpha.add(lowAlpha);
        this.lowAlpha_10.add(lowAlpha);
        this.lowBeta.add(lowBeta);
        this.lowBeta_10.add(lowBeta);
        this.lowGamma.add(lowGamma);
        this.lowGamma_10.add(lowGamma);
        this.midGamma.add(midGamma);
        this.midGamma_10.add(midGamma);
        this.theta.add(theta);
        this.theta_10.add(theta);

        this.attention = attention;
        this.meditation = meditation;
        this.blink = blink;
        this.rawCount = rawCount;
        this.rawValue = rawValue;
        this.poorSignal = poorSignal;
        this.sleepStage = sleepStage;

        if (this.sleepStageCount < 0) {
            if (sleepStage > 0) this.sleepStageCount = 0;
        } else if (this.sleepStageCount >= SLEEP_STAGE_SECONDS) {
            this.sleepStageCount = 1;
        } else {
            this.sleepStageCount++;
        }
    }

    public static String[] getLogParamNames() {
        return new String[] {
                "delta", "theta", "lowAlpha", "highAlpha", "lowBeta", "highBeta", "lowGamma", "midGamma",
                "delta_Clear", "theta_Clear", "lowAlpha_Clear", "highAlpha_Clear", "lowBeta_Clear", "highBeta_Clear", "lowGamma_Clear", "midGamma_Clear", "total_Clear",
                "Delta_1", "Delta_10", "Theta_1", "Theta_10", "lowAlpha_1", "lowAlpha_10", "highAlpha_1", "highAlpha_10", "lowBeta_1", "lowBeta_10", "highBeta_1", "highBeta_10", "lowGamma_1", "lowGamma_10", "midGamma_1", "midGamma_10",
                "Delta_Max", "Theta_Max", "lowAlpha_Max", "highAlpha_Max", "lowBeta_Max", "highBeta_Max", "lowGamma_Max", "midGamma_Max",
                "Delta_Sum", "Theta_Sum", "lowAlpha_Sum", "highAlpha_Sum", "lowBeta_Sum", "highBeta_Sum", "lowGamma_Sum", "midGamma_Sum", "Total_Sum",
                "attention", "meditation", "rawCount", "rawValue", "poorSignal", "sleepStage", "blink"
        };
    }

    public Integer[] getLogParams() {
        ArrayList<Integer> answer = new ArrayList<Integer>();

        answer.addAll(getRawData());
        answer.addAll(getClearData());
        answer.addAll(getAverageEraData());
        answer.addAll(getMaxEraData());
        answer.addAll(getSumEraData());
        answer.addAll(Arrays.asList(attention, meditation, rawCount, rawValue, poorSignal, sleepStage));
        answer.addAll(blink);

        return answer.toArray(new Integer[answer.size()]);
    }

    public static String[] getHistoryConfigNames() {
        return new String[]{"delta", "highAlpha", "highBeta", "lowAlpha", "lowBeta", "lowGamma", "midGamma", "theta", "attention", "meditation"};
    }

    public List<Integer> getRawData() {
        return Lists.transform(getEras(false), new Function<LimitedQueueValue, Integer>() {
            @Override
            public Integer apply(LimitedQueueValue limitedQueueValue) {
                return limitedQueueValue.raw;
            }
        });
    }

    public List<Integer> getClearData() {
        List<Integer> clear = Lists.newArrayList(Lists.transform(getEras(false), new Function<LimitedQueueValue, Integer>() {
            @Override
            public Integer apply(LimitedQueueValue limitedQueueValue) {
                return limitedQueueValue.clear;
            }
        }));

        Integer total = CollectionUtils.sum(clear);
        clear.add(total);

        return clear;
    }

    private List<Integer> getAverageEraData() {
        return Lists.transform(getEras(true), new Function<LimitedQueueValue, Integer>() {
            @Override
            public Integer apply(LimitedQueueValue limitedQueueValue) {
                return limitedQueueValue.getAverage();
            }
        });
    }

    private List<Integer> getMaxEraData() {
        List<LimitedQueueValue> eras = getEras(false);
        List<Integer> values = Lists.transform(eras, new Function<LimitedQueueValue, Integer>() {
            @Override
            public Integer apply(LimitedQueueValue limitedQueueValue) {
                return limitedQueueValue.clear;
            }
        });

        final Integer max = Collections.max(values);

        return Lists.transform(eras, new Function<LimitedQueueValue, Integer>() {
            @Override
            public Integer apply(LimitedQueueValue limitedQueueValue) {
                if (limitedQueueValue.clear >= max) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private List<Integer> getSumEraData() {
        List<Integer> sumEraData;

        if (this.sleepStageCount >= SLEEP_STAGE_SECONDS) {
            sumEraData = Lists.newArrayList(Lists.transform(getEras(false), new Function<LimitedQueueValue, Integer>() {
                @Override
                public Integer apply(LimitedQueueValue limitedQueueValue) {
                    return limitedQueueValue.getSum();
                }
            }));

            Integer total = CollectionUtils.sum(sumEraData);
            sumEraData.add(total);
        } else {
            sumEraData = Lists.newArrayList(Lists.transform(getEras(false), new Function<LimitedQueueValue, Integer>() {
                @Override
                public Integer apply(LimitedQueueValue limitedQueueValue) {
                    return 0;
                }
            }));

            Integer total = 0;
            sumEraData.add(total);
        }

        return sumEraData;
    }

    private List<LimitedQueueValue> getEras(boolean include10) {
        List<LimitedQueueValue> eras = Arrays.asList(delta, theta, lowAlpha, highAlpha, lowBeta, highBeta, lowGamma, midGamma);

        if (include10) {
            List<LimitedQueueValue> answer = new ArrayList<LimitedQueueValue>();
            List<LimitedQueueValue> eras10 = Arrays.asList(delta_10, theta_10, lowAlpha_10, highAlpha_10, lowBeta_10, highBeta_10, lowGamma_10, midGamma_10);

            for (int i = 0; i < eras.size(); i++) {
                answer.add(eras.get(i));
                answer.add(eras10.get(i));
            }

            return answer;
        } else {
            return eras;
        }
    }
}
