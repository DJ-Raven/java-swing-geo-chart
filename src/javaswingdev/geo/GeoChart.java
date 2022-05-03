package javaswingdev.geo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class GeoChart extends JComponent {

    private final GeoChartPanel geoChartPanel;
    private final JScrollPane scroll;
    private final Map<String, Double> model = new HashMap<>();
    private final List<GeoData.Regions> geoRegions = new ArrayList<>();
    private Color gradientColor = new Color(101, 196, 255);
    private Color mapColor = new Color(255, 255, 255);
    private Color mapSelectedColor = new Color(229, 229, 229);

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
        setBackground(new Color(178, 225, 255));
        init();
    }

    private void init() {
        geoChartPanel.initMouse();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (gradientColor != null) {
                g2.setPaint(getGradient());
            } else {
                g2.setColor(getBackground());
            }
            g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
            g2.dispose();
        }
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (geoChartPanel.isHasData()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawAxis(g2);
            g2.dispose();
        }
    }

    private void drawAxis(Graphics2D g2) {
        Insets inset = getInsets();
        double width = getWidth();
        double height = getHeight();
        double axisWidth = width * 0.25f;
        double axisHeight = height * 0.03f;
        double x = inset.left + 5;
        double y = height - inset.bottom - 5 - axisHeight;
        Rectangle2D rec = new Rectangle2D.Double(x, y, axisWidth, axisHeight);
        g2.setColor(new Color(0, 0, 0, 70));
        g2.translate(2, 2);
        g2.fill(new Area(rec));
        g2.translate(-2, -2);
        g2.setPaint(new GradientPaint((float) rec.getX(), 0, new Color(210, 255, 235), (float) (rec.getX() + rec.getWidth()), 0, new Color(27, 158, 101)));
        g2.fill(rec);
    }

    private RadialGradientPaint getGradient() {
        int width = getWidth();
        int height = getHeight();
        Point2D center = new Point2D.Double(width / 2, height / 2);
        float radius = (float) Math.max(width, height) / 2;
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {getBackground(), gradientColor};
        return new RadialGradientPaint(center, radius, dist, colors);
    }

    public void setRegions(GeoData.Regions... regions) {
        geoRegions.clear();
        for (GeoData.Regions r : regions) {
            geoRegions.add(r);
        }
    }

    public void putData(String country, double values) {
        model.put(country, values);
        revalidate();
    }

    public void clearData() {
        model.clear();
        repaint();
    }

    public void clearRegions() {
        geoRegions.clear();
    }

    public void load(GeoData.Resolution resolution) {
        geoChartPanel.init(geoRegions, resolution);
    }

    public void load() {
        geoChartPanel.init(geoRegions, GeoData.Resolution.MEDIUM);
    }

    public GeoChartPanel getGeoChart() {
        return geoChartPanel;
    }

    public Color getGradientColor() {
        return gradientColor;
    }

    public void setGradientColor(Color gradientColor) {
        this.gradientColor = gradientColor;
        repaint();
    }

    public Color getMapColor() {
        return mapColor;
    }

    public void setMapColor(Color mapColor) {
        this.mapColor = mapColor;
        repaint();
    }

    public Color getMapSelectedColor() {
        return mapSelectedColor;
    }

    public void setMapSelectedColor(Color mapSelectedColor) {
        this.mapSelectedColor = mapSelectedColor;
        repaint();
    }

    public Map<String, Double> getModel() {
        return model;
    }
}
