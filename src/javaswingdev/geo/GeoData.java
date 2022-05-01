package javaswingdev.geo;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GeoData {

    private JSONParser parser = new JSONParser();
    private JSONObject data;

    public GeoData() {
        try {
            data = (JSONObject) parser.parse(new FileReader("countries.geojson"));
        } catch (IOException | ParseException e) {
            System.err.println(e);
        }
    }

    public HashMap<String, List<List<Coordinates>>> getCountry() {
        HashMap<String, List<List<Coordinates>>> hash = new HashMap<>();
        JSONArray features = (JSONArray) data.get("features");
        for (int i = 0; i < features.size(); i++) {
            JSONObject features_data = (JSONObject) features.get(i);
            JSONObject properties = (JSONObject) features_data.get("properties");
            String countryName = (String) properties.get("ADMIN");
            if (!countryName.equals("Antarctica")) {
                hash.put(countryName, getCoordinates((JSONObject) features_data.get("geometry")));
            }
        }
        return hash;
    }

    private List<List<Coordinates>> getCoordinates(JSONObject data) {
        List<List<Coordinates>> list = new ArrayList<>();
        JSONArray coordinates = (JSONArray) data.get("coordinates");
        for (int i = 0; i < coordinates.size(); i++) {
            List<Coordinates> l = new LinkedList<>();
            JSONArray arr = (JSONArray) coordinates.get(i);
            for (int j = 0; j < arr.size(); j++) {
                JSONArray obj = (JSONArray) arr.get(j);
                String texts[] = obj.toString().replace("[", "").replace("]", "").split(",");
                int index = 0;
                for (int k = 0; k < texts.length; k += 2) {
                    l.add(new Coordinates(Double.valueOf(texts[index++]), Double.valueOf(texts[index++])));
                }
            }
            list.add(l);
        }
        return list;
    }
}
