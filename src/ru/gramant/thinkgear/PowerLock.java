package ru.gramant.thinkgear;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by fedor.belov on 14.11.13.
 */
public class PowerLock {

    PowerManager pm;
    PowerManager.WakeLock lock;

    public PowerLock(Context context) {
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ThinkGearApp");
    }

    public void acquire() {
        lock.acquire();
    }

    public void release() {
        lock.release();
    }
}
