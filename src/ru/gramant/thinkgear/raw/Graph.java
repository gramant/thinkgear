package ru.gramant.thinkgear.raw;

import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
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

        String[] seriesArray = new String[] {"raw", "highAlpha", "highBeta", "lowAlpha", "lowBeta", "lowGamma", "midGamma", "theta", "delta"};
        int[] colorsArray = new int[] {Color.DKGRAY, Color.YELLOW, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.WHITE, Color.BLUE, Color.MAGENTA};

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        for (int i = 0; i < seriesArray.length; i++) {
            String s = seriesArray[i];
            int color = colorsArray[i];

            TimeSeries series = new TimeSeries(s);
            seriesByName.put(s, series);
            dataset.addSeries(series);

            XYSeriesRenderer renderer = new XYSeriesRenderer();
            renderer.setColor(color);
            mRenderer.addSeriesRenderer(renderer);
        }

        view = ChartFactory.getLineChartView(context, dataset, mRenderer);
        return view;
    }

    public void add(String name, int value) {
        seriesByName.get(name).add(System.currentTimeMillis() - start, (value > 30000) ? 30000 : value);
        if (view != null) {
            view.repaint();
            System.out.println("Repaint " + name + ":" + value);
        }
    }

}
