package ru.gramant.thinkgear.phase;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class MinMax {

    public static final MinMax ZERO = new MinMax(0,0);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinMax minMax = (MinMax) o;

        if (max != minMax.max) return false;
        if (min != minMax.min) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = min;
        result = 31 * result + max;
        return result;
    }
}
