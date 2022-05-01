package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Canvas extends JComponent implements MouseWheelListener, MouseMotionListener, MouseListener {

    private double zoom = 1.0;
    public static final double SCALE_STEP = 0.1d;
    private Dimension initialSize;
    private Point origin;
    private double previousZoom = zoom;
    private double scrollX = 0d;
    private double scrollY = 0d;
    private Rectangle2D rect = new Rectangle2D.Double(0, 0, 800, 600);
    private float hexSize = 3f;

    public Canvas(double zoom) {
        this.zoom = zoom;
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setAutoscrolls(true);

        //Set preferred size to be 100x bigger then drawing object
        //So the canvas will be scrollable until our drawing object gets 100x smaller then its natural size.
        //When the drawing object became so small, it is unnecessary to follow mouse on it,
        //and we only center it on the canvas
        setPreferredSize(new Dimension((int) (rect.getWidth() * 100), (int) (rect.getHeight() * 100)));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //Obtain a copy of graphics object without any transforms
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.clearRect(0, 0, getWidth(), getHeight());

        //Zoom graphics
        g2d.scale(zoom, zoom);

        //translate graphics to be always in center of the canvas
        Rectangle size = getBounds();
        double tx = ((size.getWidth() - rect.getWidth() * zoom) / 2) / zoom;
        double ty = ((size.getHeight() - rect.getHeight() * zoom) / 2) / zoom;
        g2d.translate(tx, ty);

        //Draw
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(rect);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(5.0f));
        g2d.draw(rect);

        //Forget all transforms
        g2d.dispose();
    }

    @Override
    public void setSize(Dimension size) {
        super.setSize(size);
        if (initialSize == null) {
            this.initialSize = size;
        }
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        if (initialSize == null) {
            this.initialSize = preferredSize;
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoomFactor = -SCALE_STEP * e.getPreciseWheelRotation() * zoom;
        zoom = Math.abs(zoom + zoomFactor);
        //Here we calculate new size of canvas relative to zoom.
        Dimension d = new Dimension(
                (int) (initialSize.width * zoom),
                (int) (initialSize.height * zoom));
        setPreferredSize(d);
        setSize(d);
        validate();
        followMouseOrCenter(e.getPoint());
        previousZoom = zoom;
    }

    public void followMouseOrCenter(Point2D point) {
        Rectangle size = getBounds();
        Rectangle visibleRect = getVisibleRect();
        scrollX = size.getCenterX();
        scrollY = size.getCenterY();
        if (point != null) {
            scrollX = point.getX() / previousZoom * zoom - (point.getX() - visibleRect.getX());
            scrollY = point.getY() / previousZoom * zoom - (point.getY() - visibleRect.getY());
        }

        visibleRect.setRect(scrollX, scrollY, visibleRect.getWidth(), visibleRect.getHeight());
        scrollRectToVisible(visibleRect);
    }

    public void mouseDragged(MouseEvent e) {
        if (origin != null) {
            int deltaX = origin.x - e.getX();
            int deltaY = origin.y - e.getY();
            Rectangle view = getVisibleRect();
            view.x += deltaX;
            view.y += deltaY;
            scrollRectToVisible(view);
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        origin = new Point(e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
