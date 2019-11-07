package ratingapp.ddey.com.testratingapp.utils.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.models.Hotel;

public class HotelsParser {
    public static List<Hotel> getHotels(String jsonString) throws JSONException {
        List<Hotel> resultList = new ArrayList<>();

        if (jsonString != null) {
            JSONArray array = new JSONArray(jsonString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    if (object != null) {
                        String hotelName = object.getString("hotelName");
                        float distanceFromCenter = (float) object.getDouble("distanceFromCenter");
                        int price = object.getInt("price");
                        String imageUrl = object.getString("image");
                        int stars = object.getInt("stars");
                        String city = object.getString("city");
                        Hotel h = new Hotel(hotelName, distanceFromCenter, price, stars, imageUrl, city);
                        h.setExpandable(true);
                        resultList.add(h);
                    }
                }
        }

        return resultList;
    }
}
