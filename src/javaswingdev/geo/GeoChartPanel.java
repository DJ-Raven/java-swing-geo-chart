package javaswingdev.geo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
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
    private double zoom = 6;

    public GeoChartPanel(JComponent component) {
        this.component = component;
    }
    private Shape shape_over;

    public void init() {
        data = GeoData.getInstance().getCountry();
        maxAndMin = getMaxAndMin(data);
        shape = new HashMap<>();
        initShape();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean over = false;
                Dimension size = maxAndMin.getTotalSize(zoom);
                double centerX = (getWidth() - size.getWidth()) / 2;
                double centerY = (getHeight() - size.getHeight()) / 2;
                for (Map.Entry<String, Shape> s : shape.entrySet()) {
                    if (s.getValue().contains(e.getPoint().getX() - centerX, e.getPoint().getY() - centerY)) {
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

    private void initShape() {
        shape.clear();
        data.forEach((t, u) -> {
            shape.put(t, toShap(u));
        });
        setPreferredSize(maxAndMin.getTotalSize(zoom));
        revalidate();
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

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double previousZoom = zoom;
                double zoomFactor = -0.1 * e.getPreciseWheelRotation() * zoom;
                zoom = Math.abs(zoom + zoomFactor);
                if (e.getWheelRotation() < 0) {
                    zoom += 0.5f;
                } else {
                    zoom -= 0.5f;
                }
                initShape();
                repaint();
                followMouseOrCenter(e.getPoint(), previousZoom);
            }
        };
        addMouseListener(mouseEvent);
        addMouseMotionListener(mouseEvent);
        addMouseWheelListener(mouseEvent);
    }

    public void followMouseOrCenter(Point2D point, double previousZoom) {
        Rectangle size = getBounds();
        Rectangle visibleRect = getVisibleRect();
        double scrollX = size.getCenterX();
        double scrollY = size.getCenterY();
        if (point != null) {
            scrollX = point.getX() / previousZoom * zoom - (point.getX() - visibleRect.getX());
            scrollY = point.getY() / previousZoom * zoom - (point.getY() - visibleRect.getY());
        }
        visibleRect.setRect(scrollX, scrollY, visibleRect.getWidth(), visibleRect.getHeight());
        scrollRectToVisible(visibleRect);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        Rectangle clip = g.getClipBounds();
        if (component.isOpaque()) {
            g2.setColor(component.getBackground());
            g2.fill(clip);
        }
        if (shape != null) {
            g2.setColor(new Color(194, 194, 194));
            Dimension size = maxAndMin.getTotalSize(zoom);
            double centerX = (getWidth() - size.getWidth()) / 2;
            double centerY = (getHeight() - size.getHeight()) / 2;
            g2.translate(centerX, centerY);
            shape.forEach((t, u) -> {
                if (u.intersects(clip)) {
                    drawCountry(g2, u);
                }
            });
            g2.dispose();
        }
        super.paintComponent(g);
    }

    private void drawCountry(Graphics2D g2, Shape shap) {
        g2.setColor(new Color(178, 178, 178));
        if (shap == shape_over) {
            g2.setColor(new Color(61, 149, 217));
            g2.fill(shap);
        } else {
            g2.fill(shap);
        }
    }

    private Shape toShap(List<List<Coordinates>> data) {
        double size = zoom;
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
