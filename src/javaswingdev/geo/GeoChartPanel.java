package javaswingdev.geo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class GeoChartPanel extends JComponent {

    private final JComponent component;
    private HashMap<String, List<List<Coordinates>>> data;
    private HashMap<String, Shape> shape;
    private MaxAndMin maxAndMin;
    private float zoom = 6;

    public GeoChartPanel(JComponent component) {
        this.component = component;
    }
    private Shape shape_over;

    public void init() {
        data = new GeoData().getCountry();
        maxAndMin = getMaxAndMin(data);
        System.out.println(maxAndMin.toString());
        shape = new HashMap<>();
        data.forEach((t, u) -> {
            shape.put(t, toShap(u));
        });
        setPreferredSize(maxAndMin.getTotalSize(zoom));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean over = false;
                for (Map.Entry<String, Shape> s : shape.entrySet()) {
                    if (s.getValue().contains(e.getPoint())) {
                        over = true;
                        if (s.getValue() != shape_over) {
                            shape_over = s.getValue();
                            repaint();
                            System.out.println(s.getKey());
                            break;
                        }

                    }
                }
                if (!over) {
                    if (shape_over != null) {
                        shape_over = null;
                        repaint();
                    }
                }
            }
        });
        initMouseScroll();
    }

    private void initMouseScroll() {
        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
        MouseAdapter mouseEvent = new MouseAdapter() {
            Point origin;

            @Override
            public void mousePressed(MouseEvent e) {
                origin = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null) {
                    int deltaX = origin.x - e.getX();
                    int deltaY = origin.y - e.getY();
                    Rectangle view = viewPort.getViewRect();
                    view.x += deltaX;
                    view.y += deltaY;
                    GeoChartPanel.this.scrollRectToVisible(view);
                }
            }

        };
        addMouseListener(mouseEvent);
        addMouseMotionListener(mouseEvent);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (component.isOpaque()) {
            g.setColor(component.getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        if (shape != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.setColor(new Color(194, 194, 194));
            shape.forEach((t, u) -> {
                drawCountry(g2, u);
            });
            g2.dispose();
        }
        super.paintComponent(g);
    }

    private void drawCountry(Graphics2D g2, Shape shap) {
        g2.setColor(new Color(178, 178, 178));
        //  g2.fill(shap);
        if (shap == shape_over) {
            g2.setColor(new Color(61, 149, 217));
            g2.fill(shap);
        } else {
            g2.fill(shap);
        }
    }

    private Shape toShap(List<List<Coordinates>> data) {
        float size = zoom;
        double minHeight = maxAndMin.getMin_height() * -1;
        double minWidth = maxAndMin.getMin_width() * -1;
        double totalHeight = minHeight + maxAndMin.getMax_height();
        Path2D p2 = new Path2D.Double();
        for (List<Coordinates> d : data) {
            boolean move = true;
            for (Coordinates c : d) {
                double y = (c.getY() + minHeight);
                double x = (c.getX() + minWidth) * size;
                y = totalHeight - y;
                y *= size;
                if (move) {
                    p2.moveTo(x, y);
                    move = false;
                } else {
                    p2.lineTo(x, y);
                }
            }
        }
        return p2;
    }

    private MaxAndMin getMaxAndMin(HashMap<String, List<List<Coordinates>>> data) {
        double min_width = 0;
        double min_height = 0;
        double max_width = 0;
        double max_height = 0;
        for (List<List<Coordinates>> list : data.values()) {
            for (List<Coordinates> d : list) {
                for (Coordinates c : d) {
                    min_width = Math.min(min_width, c.getX());
                    min_height = Math.min(min_height, c.getY());
                    max_width = Math.max(max_width, c.getX());
                    max_height = Math.max(max_height, c.getY());
                }
            }
        }
        return new MaxAndMin(min_width, min_height, max_width, max_height);
    }
}
