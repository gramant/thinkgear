package com.test.helloeeg;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by fedor.belov on 20.08.13.
 */
public class DataFlusher {

    private volatile boolean active = true;
    private String file;
    private Context context;
    private ConcurrentLinkedQueue<Event> events = new ConcurrentLinkedQueue<Event>();

    public DataFlusher(Context context, String file) {
        this.context = context;
        this.file = file;
    }

    void start() {
        try {
            File f = new File(Environment.getExternalStorageDirectory().toString() + "/thinkgear", file);
            f.getParentFile().mkdirs();
            FileOutputStream fOut = new FileOutputStream(f);
            final OutputStreamWriter osw = new OutputStreamWriter(fOut);

            new Thread() {
                @Override
                public void run() {
                    try {
                        while (active) {
                            Event e = events.poll();
                            while (e != null) {
                                osw.write(e.second + ";" + e.data + "\n");
                                osw.flush();

                                e = events.poll();
                            }

                            try {
                                sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } finally {
                        try {
                            osw.close();
                        } catch (IOException e) {
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
