package javaswingdev.geo;

public class ModelFontSize {

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getAscent() {
        return ascent;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    public ModelFontSize(double width, double height, int ascent) {
        this.width = width;
        this.height = height;
        this.ascent = ascent;
    }

    public ModelFontSize() {
    }

    private double width;
    private double height;
    private int ascent;
}
