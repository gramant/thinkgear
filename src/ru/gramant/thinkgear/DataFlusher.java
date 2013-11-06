package ru.gramant.thinkgear;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    void start() {
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/thinkgear", file);
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
                                osw.println(e.second + ";" + e.data);
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

    void add(String data) {
        events.offer(new Event(System.currentTimeMillis(), data));
    }

    void stop() {
        active = false;
    }

    private static class Event {
        Long second;
        String data;

        private Event(Long second, String data) {
            this.second = second;
            this.data = data;
        }
    }
}
