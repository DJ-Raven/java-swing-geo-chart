package javaswingdev.geo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javaswingdev.geo.json.Features;
import javaswingdev.geo.json.JsonData;

public class GeoData {

    public GeoData() {

    }

    public JsonData get() {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            FileReader file = new FileReader("countries.geojson");
            BufferedReader bufferedReader = new BufferedReader(file);
            JsonData data = gson.fromJson(bufferedReader, JsonData.class);
            bufferedReader.close();
            file.close();
            return data;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            System.err.println(e);
        }
        return null;
    }

    public HashMap<String, List<List<Coordinates>>> getCountry() {
        HashMap<String, List<List<Coordinates>>> hash = new HashMap<>();
        JsonData data = get();
        for (Features f : data.getFeatures()) {
            String countryName = f.getProperties().getADMIN();
            if (countryName.equals("Cambodia")) {
                hash.put(countryName, getCoordinates(f.getGeometry().getCoordinates(), f.getGeometry().getType()));
            }
        }
        data.getFeatures().clear();
        data = null;
        return hash;
    }

    private List<List<Coordinates>> getCoordinates(List<List<List<Object>>> data, String type) {
        List<List<Coordinates>> list = new ArrayList<>();
        if (type.equals("Polygon")) {
            for (List<List<Object>> d : data) {
                List<Coordinates> coordinates = new ArrayList<>();
                for (List<Object> o : d) {
                    coordinates.add(new Coordinates(Double.valueOf(o.get(0).toString()), Double.valueOf(o.get(1).toString())));
                }
                list.add(coordinates);
            }
        } else {
            for (List<List<Object>> d : data) {
                List<Coordinates> coordinates = new ArrayList<>();
                for (List<Object> o : d) {
                    for (Object i : o) {
                        String values[] = i.toString().replace("[", "").replace("]", "").split(",");
                        if (values.length == 2) {
                            coordinates.add(new Coordinates(Double.valueOf(values[0]), Double.valueOf(values[1])));
                        }
                    }
                }
                list.add(coordinates);
            }
        }
        return list;
    }
}
