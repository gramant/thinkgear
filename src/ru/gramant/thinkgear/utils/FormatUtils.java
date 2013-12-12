package ru.gramant.thinkgear.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class FormatUtils {

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("k:mm:ss.SSS");

    public static String dateToHumanTime(Long millis) {
        return timeFormat.format(new Date(millis));
    }

    public static String arrayToString(Object[] s, String glue) {
        int k = s.length;
        if (k == 0)
            return null;
        StringBuilder out = new StringBuilder();
        out.append(s[0].toString());
        for (int x = 1; x < k; ++x) {
            out.append(glue).append((s[x] != null) ? s[x].toString() : " ");
        }

        return out.toString();
    }

}
