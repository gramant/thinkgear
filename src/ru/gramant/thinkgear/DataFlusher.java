package ru.gramant.thinkgear;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.gramant.thinkgear.utils.FormatUtils;

/**
 * Created by fedor.belov on 20.08.13.
 */
public class DataFlusher {

    private static final String LOG_CATEGORY = "ru.gramant.thinkgear.DataFlusher";

    private volatile boolean active = true;
    private String file;
    private ConcurrentLinkedQueue<Event> events = new ConcurrentLinkedQueue<Event>();

    public DataFlusher(String file) {
        this.file = file;
    }

    public void start() {
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + App.ROOT_FOLDER, file);
            f.getParentFile().mkdirs();
            FileOutputStream fOut = new FileOutputStream(f);
            final PrintWriter osw = new PrintWriter(new OutputStreamWriter(fOut));

            Log.w(LOG_CATEGORY, "Saving data to file " + f.getAbsolutePath());

            new Thread() {
                @Override
                public void run() {
                    try {
                        while (active) {
                            Event e = events.poll();
                            while (e != null) {
                                if (e.millis > 0) {
                                    osw.println(FormatUtils.dateToHumanTime(e.millis) + ";" + e.data);
                                } else {
                                    osw.println(e.data);
                                }

                                osw.flush();

                                e = events.poll();
                            }

                            try {
                                sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (Exception ioe) {
                        Log.e(LOG_CATEGORY, "Exception on saving thingear file!", ioe);
                        ioe.printStackTrace();
                    } finally {
                        try {
                            osw.close();
                        } catch (Exception e) {
                            Log.e(LOG_CATEGORY, "Exception on saving thingear file!", e);
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String data) {
        events.offer(new Event(System.currentTimeMillis(), data));
    }

    public void addWithoutTime(String data) {
        events.offer(new Event(-1, data));
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    private static class Event {
        Long millis;
        String data;

        private Event(long millis, String data) {
            this.millis = millis;
            this.data = data;
        }
    }
}
