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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class GeoChart extends JComponent {

    private final GeoChartPanel geoChartPanel;
    private final JScrollPane scroll;
    private final Map<String, Double> model = new HashMap<>();
    private final List<GeoData.Regions> geoRegions = new ArrayList<>();
    private Color gradientColor = null;
    private Color mapColor = new Color(200, 200, 200);
    private Color mapSelectedColor = new Color(100, 100, 100);
    private Color axisColorMax = new Color(0, 131, 245);
    private Color axisColorMin = new Color(128, 206, 255);
    private DecimalFormat format = new DecimalFormat("View : #,##0");
    private BufferedImage axisImage;
    private double axisValues = -1;

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
        setForeground(new Color(100, 100, 100));
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

    private void drawAxis(Graphics2D g) {
        Insets inset = getInsets();
        double width = getWidth();
        double height = getHeight();
        double axisWidth = width * 0.25f;
        double axisHeight = 15;
        double x = inset.left + 8;
        double y = height - inset.bottom - 8 - axisHeight;
        if (axisWidth > 150) {
            axisWidth = 250;
        } else if (axisWidth <= 50) {
            axisWidth = 50;
        }
        axisImage = new BufferedImage((int) axisWidth, (int) axisHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = axisImage.createGraphics();
        Rectangle2D rec = new Rectangle2D.Double(0, 0, axisWidth, axisHeight);
        g2.setPaint(new GradientPaint((float) rec.getX(), 0, axisColorMin, (float) (rec.getX() + rec.getWidth()), 0, axisColorMax));
        g2.fill(rec);
        g2.dispose();
        g.drawImage(axisImage, (int) x, (int) y, null);
        drawPoint(g, x, y, axisWidth);
    }

    private void drawPoint(Graphics2D g2, double x, double y, double axisWidth) {
        if (axisValues >= 0) {
            double min = getMinValue();
            double max = getMaxValue();
            double percentage = (axisValues - min) / (max - min);
            double xx = x + (percentage * axisWidth);
            double hh = 8;
            double ss = 5;
            Path2D p = new Path2D.Double();
            p.moveTo(xx - ss, y - hh);
            p.lineTo(xx + ss, y - hh);
            p.lineTo(xx, y);
            g2.setColor(new Color(50, 50, 50));
            g2.fill(p);
        }
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

    private double getMinValue() {
        double min = 0;
        boolean init = true;
        for (double v : model.values()) {
            if (init) {
                min = v;
                init = false;
            } else {
                min = Math.min(v, min);
            }
        }
        return min;
    }

    private double getMaxValue() {
        double max = 0;
        boolean init = true;
        for (double v : model.values()) {
            if (init) {
                max = v;
                init = false;
            } else {
                max = Math.max(v, max);
            }
        }
        return max;
    }

    public Color getColorOf(double value) {
        if (axisImage != null) {
            double min = getMinValue();
            double max = getMaxValue();
            double percentage = (value - min) / (max - min);
            int width = axisImage.getWidth();
            int x = (int) (width * percentage);
            if (x >= width) {
                x = width - 1;
            } else if (x <= 0) {
                x = 1;
            }
            return new Color(axisImage.getRGB(x, 1));
        } else {
            return axisColorMin;
        }
    }

    public void setRegions(GeoData.Regions... regions) {
        geoRegions.clear();
        for (GeoData.Regions r : regions) {
            geoRegions.add(r);
        }
    }

    public void putData(String country, double values) {
        model.put(country, values);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
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

    public Color getAxisColorMax() {
        return axisColorMax;
    }

    public void setAxisColorMax(Color axisColorMax) {
        this.axisColorMax = axisColorMax;
        repaint();
    }

    public Color getAxisColorMin() {
        return axisColorMin;
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = new DecimalFormat(format);
    }

    public void setAxisValues(double axisValues) {
        this.axisValues = axisValues;
    }

    public void setAxisColorMin(Color axisColorMin) {
        this.axisColorMin = axisColorMin;
        repaint();
    }

    public void setAxisColor(Color axisColorMin, Color axisColorMax) {
        this.axisColorMin = axisColorMin;
        this.axisColorMax = axisColorMax;
        repaint();
    }
}
