package javaswingdev.geo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class GeoChart extends JComponent {

    private final GeoChartPanel geoChartPanel;
    private final JScrollPane scroll;

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

    public void init() {
        geoChartPanel.init();
    }
}
