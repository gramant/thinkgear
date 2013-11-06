package ru.gramant.thinkgear.utils;

import android.bluetooth.BluetoothDevice;

import com.neurosky.thinkgear.TGDevice;

/**
 * Created by fedor.belov on 06.11.13.
 */
public class BluetoothUtils {

    //http://stackoverflow.com/questions/6662216/display-android-bluetooth-device-name
    public static String getTargetBluetoothName(TGDevice tgDevice){
        BluetoothDevice device = tgDevice.getConnectedDevice();
        String name = (device != null) ? tgDevice.getConnectedDevice().getName() : null;
        return (name != null) ? name : "-";
    }

}
