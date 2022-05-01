package javaswingdev.geo.json;

import java.util.List;

public class Geometry {

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<List<Object>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<Object>>> coordinates) {
        this.coordinates = coordinates;
    }

    private String type;
    private List<List<List<Object>>> coordinates;
}
