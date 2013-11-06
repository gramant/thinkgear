package ru.gramant.thinkgear.phase;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class MinMax {

    int min;
    int max;

    public MinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean fit(int x) {
        if (min == 0 && max == 0) {
            return true;
        } else if (x >= min && x <= max) {
            return true;
        } else {
            return false;
        }
    }
}
