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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fedor.belov on 08.08.13.
 */
public class Graph {

    final Map<String, TimeSeries> seriesByName = new HashMap<String, TimeSeries>();
    private GraphicalView view;
    private Context context;
    private long start;

    public Graph(Context context) {
        this.context = context;
    }

    public GraphicalView start() {
        start = System.currentTimeMillis();

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        for (String s : new String[] {"raw", "highAlpha", "highBeta", "lowAlpha", "lowBeta", "lowGamma", "midGamma", "theta", "delta"}) {
            TimeSeries series = new TimeSeries(s);
            seriesByName.put(s, series);
            dataset.addSeries(series);

            XYSeriesRenderer renderer = new XYSeriesRenderer();
            mRenderer.addSeriesRenderer(renderer);
        }

        view = ChartFactory.getLineChartView(context, dataset, mRenderer);
        return view;
    }

    public void add(String name, int value) {
        seriesByName.get(name).add(System.currentTimeMillis() - start, value);
        if (view != null) {
            view.repaint();
            System.out.println("Repaint " + name + ":" + value);
        }
    }

}
