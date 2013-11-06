package ru.gramant.thinkgear.utils;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class FormatUtils {

    public static String dateToHumanTime(Long millis) {
        return DateFormat.format("k:mm:ss", new Date(millis)).toString();
    }

}
