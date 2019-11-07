package ratingapp.ddey.com.testratingapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Filter implements Serializable {
    private float distanceFromCityCenter;
    private int pricePerNight;
    private List<Integer> hotelStars;
    private float rating;
    private String city;

    public Filter() {
        hotelStars = new ArrayList<>();
    }



    public Filter(float distanceFromCityCenter, int pricePerNight, List<Integer> hotelStars, float rating, String city) {
        this.distanceFromCityCenter = distanceFromCityCenter;
        this.pricePerNight = pricePerNight;
        this.hotelStars = hotelStars;
        this.rating = rating;
        this.city = city;
    }

    public Filter(int pricePerNight, List<Integer> hotelStars, float rating, String city) {
        this.pricePerNight = pricePerNight;
        this.hotelStars = hotelStars;
        this.rating = rating;
        this.city = city;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "distanceFromCityCenter=" + distanceFromCityCenter +
                ", pricePerNight=" + pricePerNight +
                ", hotelStars=" + hotelStars +
                ", rating=" + rating +
                ", city='" + city + '\'' +
                '}';
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(int pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public List<Integer> getHotelStars() {
        return hotelStars;
    }

    public void setHotelStars(List<Integer> hotelStars) {
        this.hotelStars = hotelStars;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getDistanceFromCityCenter() {
        return distanceFromCityCenter;
    }

    public void setDistanceFromCityCenter(float distanceFromCityCenter) {
        this.distanceFromCityCenter = distanceFromCityCenter;
    }
}
