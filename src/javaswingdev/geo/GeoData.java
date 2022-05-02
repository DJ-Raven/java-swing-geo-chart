package javaswingdev.geo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javaswingdev.geo.json.Features;
import javaswingdev.geo.json.JsonData;

public class GeoData {

    private static GeoData instance;

    public static GeoData getInstance() {
        if (instance == null) {
            instance = new GeoData();
        }
        return instance;
    }

    private GeoData() {

    }

    public JsonData get() {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            InputStream in = getClass().getResourceAsStream("/javaswingdev/geo/json/countries.geo.medium.json");
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            JsonData data = gson.fromJson(bufferedReader, JsonData.class);
            reader.close();
            bufferedReader.close();
            in.close();
            return data;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            System.err.println(e);
        }
        return null;
    }

    public HashMap<String, List<List<Coordinates>>> getCountry() {
        HashMap<String, List<List<Coordinates>>> hash = new HashMap<>();
        JsonData data = get();
        for (int i = 0; i < data.getFeatures().length; i++) {
            Features f = data.getFeatures()[i];
            String countryName = f.getProperties().getName();
            if (checkCountry(countryName)) {
                hash.put(countryName, getCoordinates(f.getGeometry().getCoordinates(), f.getGeometry().getType()));
            }
        }
        return hash;
    }

    private List<List<Coordinates>> getCoordinates(Object[][][] data, String type) {
        List<List<Coordinates>> list = new ArrayList<>();
        if (type.equals("Polygon")) {
            for (int i = 0; i < data.length; i++) {
                List<Coordinates> coordinates = new ArrayList<>();
                for (int j = 0; j < data[i].length; j++) {
                    coordinates.add(new Coordinates(Double.valueOf(data[i][j][0].toString()), Double.valueOf(data[i][j][1].toString())));
                }
                list.add(coordinates);
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                List<Coordinates> coordinates = new ArrayList<>();
                for (int j = 0; j < data[i].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        String values[] = data[i][j][k].toString().replace("[", "").replace("]", "").split(",");
                        coordinates.add(new Coordinates(Double.valueOf(values[0]), Double.valueOf(values[1])));
                    }
                }
                list.add(coordinates);
            }
        }
        return list;
    }

    private boolean checkCountry(String countryName) {
        return !countryName.equals("");
    }
}
