package ru.gramant.thinkgear.raw.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by fedor.belov on 12.12.13.
 */
public class LimitedQueueValue {

    private static final int MAX_VALUE = 16740000;

    public int raw;
    public int clear;
    private int maxCount;
    private int currentCount;
    private int sum = 0;
    private List<Integer> values = new LinkedList<Integer>();

    public LimitedQueueValue(int count) {
        this.maxCount = count;
        this.currentCount = 0;
    }

    public synchronized void add(int value) {
        raw = value;
        clear = getClear(value);

        if (currentCount >= maxCount) {
            sum -= values.remove(0);
            currentCount--;
        }

        values.add(clear);
        sum += clear;
        currentCount++;
    }

    public int getAverage() {
        return sum/currentCount;
    }

    public int getSum() {
        return sum;
    }

    private int getClear(int raw) {
        if (raw < MAX_VALUE) {
            return raw;
        } else {
            //average of two previous values
            int a = (currentCount >= 1) ? values.get(currentCount - 1) : 0;
            int b = (currentCount >= 2) ? values.get(currentCount - 2) : a;

            return (a+b)/2;
        }
    }

}
