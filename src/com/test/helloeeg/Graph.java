package com.test.helloeeg;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by fedor.belov on 08.08.13.
 */
public class Graph {

    volatile TimeSeries series;
    private GraphicalView view;
    private Context context;

    public Graph(Context context) {
        this.context = context;
    }

    public GraphicalView getView() {
        int[] x = {1,2};
        int[] y = {3,4};

        series = new TimeSeries("line1");
        for (int i = 0; i < x.length; i++) {
            series.add(x[i], y[i]);
        }

        Thread t = new Thread() {

            @Override
            public void run() {
                for (int i = 3; i < 100; i++) {
                    series.add(i, i*1.2);
                    if (view != null) view.repaint();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        t.start();

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        view = (GraphicalView) ChartFactory.getLineChartView(context, dataset, mRenderer);
        return view;
//        return ChartFactory.getLineChartIntent(context, dataset, mRenderer, "hello world");
    }



}
