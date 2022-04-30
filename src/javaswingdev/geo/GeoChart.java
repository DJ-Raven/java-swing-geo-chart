package javaswingdev.geo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComponent;

public class GeoChart extends JComponent {

    private HashMap<String, Shape> shape;

    public GeoChart() {

    }
    private Shape over;

    public void init() {
        HashMap<String, List<List<Coordinates>>> data = new GeoData().getCountry();
        shape = new HashMap<>();
        data.forEach((t, u) -> {
            shape.put(t, toShap(u));
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                for (Shape s : shape.values()) {
                    if (s.contains(e.getPoint())) {
                        over = s;
                        repaint();
                        break;
                    }
                }
            }

        });
    }

    @Override
    protected void paintComponent(Graphics g) {
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
        System.out.println(minX + " " + minY);
        super.paintComponent(g);
    }

    private void drawCountry(Graphics2D g2, Shape shap) {
        g2.setColor(new Color(141, 141, 141));
        //  g2.fill(shap);
        if (shap == over) {
            g2.setColor(new Color(61, 149, 217));
            g2.fill(shap);
        } else {
            g2.fill(shap);
        }

    }

    private double minX = 0;
    private double minY = 0;

    private Shape toShap(List<List<Coordinates>> data) {
        float size = 5;
        float addy = 90;
        float addx = 180 + 5;
        double totalWidth = 360.0000000000002;
        double totalHeight = 173.6341006530001;
        Path2D p2 = new Path2D.Double();
        for (List<Coordinates> d : data) {
            boolean move = true;
            for (Coordinates c : d) {
                double y = (c.getY() + addy);
                double x = (c.getX() + addx) * size;
                y = totalHeight - y;
                y *= size;
                if (move) {
                    p2.moveTo(x, y);
                    minX = Math.max(minX, c.getX());
                    minY = Math.max(this.minY, c.getY());
                    move = false;
                } else {
                    p2.lineTo(x, y);
                    minX = Math.max(minX, c.getX());
                    minY = Math.max(this.minY, c.getY());
                }
            }
        }
        return p2;
    }
}
