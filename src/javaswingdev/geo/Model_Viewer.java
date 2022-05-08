package javaswingdev.geo;

public class Model_Viewer {

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public Model_Viewer(String country, String values) {
        this.country = country;
        this.values = values;
    }

    public Model_Viewer() {
    }

    private String country;
    private String values;
}
