package ru.gramant.thinkgear.utils;

import android.os.Build;

import com.neurosky.thinkgear.TGDevice;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.gramant.thinkgear.FileNameCleaner;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class FileNameUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");

    public static String getFileName(TGDevice tgDevice) {
        return getFileName(tgDevice, null);
    }

    public static String getFileName(TGDevice tgDevice, String fileType) {
        Date now = new Date();
        String date = dateFormat.format(now);
        String time = timeFormat.format(now);
        String androidId = (Build.MODEL + "-" + Build.VERSION.RELEASE);
        String bluetoothName = BluetoothUtils.getTargetBluetoothName(tgDevice);

        return FileNameCleaner.cleanFileName(FormatUtils.arrayToString(new Object[]{androidId, bluetoothName, fileType, date, time}, "-") + ".txt");
    }

    public static String getHistoryFileName(TGDevice tgDevice) {
        Date now = new Date();
        String date = dateFormat.format(now);
        String time = timeFormat.format(now);
        String androidId = (Build.MODEL + "-" + Build.VERSION.RELEASE);
        String bluetoothName = BluetoothUtils.getTargetBluetoothName(tgDevice);

        return "History_" + FileNameCleaner.cleanFileName(FormatUtils.arrayToString(new Object[]{date, time, androidId, bluetoothName}, "-") + ".txt");
    }

}
