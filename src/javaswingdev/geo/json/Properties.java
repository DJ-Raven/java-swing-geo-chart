package javaswingdev.geo.json;

public class Properties {

    public String getADMIN() {
        return ADMIN;
    }

    public void setADMIN(String ADMIN) {
        this.ADMIN = ADMIN;
    }

    public String getISO_A3() {
        return ISO_A3;
    }

    public void setISO_A3(String ISO_A3) {
        this.ISO_A3 = ISO_A3;
    }

    public Properties(String ADMIN, String ISO_A3) {
        this.ADMIN = ADMIN;
        this.ISO_A3 = ISO_A3;
    }

    public Properties() {
    }

    private String ADMIN;
    private String ISO_A3;
}
