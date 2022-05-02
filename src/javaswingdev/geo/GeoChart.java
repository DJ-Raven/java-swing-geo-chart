package javaswingdev.geo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class GeoChart extends JComponent {

    private final GeoChartPanel geoChartPanel;
    private final JScrollPane scroll;
    private List<GeoData.Regions> geoRegions = new ArrayList<>();

    public GeoChart() {
        scroll = new JScrollPane();
        geoChartPanel = new GeoChartPanel(this);
        scroll.setViewportView(geoChartPanel);
        scroll.getViewport().setOpaque(false);
        scroll.setViewportBorder(null);
        scroll.setBorder(null);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new BorderLayout());
        add(scroll);
        setPreferredSize(new Dimension(250, 200));
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    public void setRegions(GeoData.Regions... regions) {
        geoRegions.clear();
        for (GeoData.Regions r : regions) {
            geoRegions.add(r);
        }
    }

    public void clearRegions() {
        geoRegions.clear();
    }

    public void load() {
        geoChartPanel.init(geoRegions);
    }

    public GeoChartPanel getGeoChart() {
        return geoChartPanel;
    }
}
