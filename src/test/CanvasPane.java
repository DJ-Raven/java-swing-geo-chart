package test;

import javax.swing.*;
import java.awt.*;

public class CanvasPane extends JPanel {

    private static Canvas canvas;

    public CanvasPane(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        setLayout(new BorderLayout());
        canvas = new Canvas(1.0);
        JScrollPane pane = new JScrollPane(canvas);
        pane.getViewport().setBackground(Color.DARK_GRAY);
        add(pane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new CanvasPane(true), BorderLayout.CENTER);
        frame.setSize(new Dimension(1000, 800));
        frame.pack();
        frame.setVisible(true);

        //Initial scrolling of the canvas to its center
        Rectangle rect = canvas.getBounds();
        Rectangle visibleRect = canvas.getVisibleRect();
        double tx = (rect.getWidth() - visibleRect.getWidth()) / 2;
        double ty = (rect.getHeight() - visibleRect.getHeight()) / 2;
        visibleRect.setBounds((int) tx, (int) ty, visibleRect.width, visibleRect.height);
        canvas.scrollRectToVisible(visibleRect);
    }
}
