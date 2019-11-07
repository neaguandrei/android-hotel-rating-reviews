package ratingapp.ddey.com.testratingapp.models;


import java.io.Serializable;
import java.util.HashMap;

public class Hotel implements Serializable {
    private String hotelName;
    private float distanceFromCenter;
    private int price;
    private int stars;
    private String imageUrl;
    private float rating;
    private HashMap<String, Rating> ratingsList;
    private HashMap<String, Review> reviewsList;
    private String token;
    private String city;
    private boolean expandable;

    public Hotel() {

    }

    public Hotel(String hotelName, float distanceFromCenter, int price, int stars, String imageUrl, float rating, HashMap<String, Rating> ratingsList, HashMap<String, Review> reviewsList, String token, String city, boolean expandable) {
        this.hotelName = hotelName;
        this.distanceFromCenter = distanceFromCenter;
        this.price = price;
        this.stars = stars;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.ratingsList = ratingsList;
        this.reviewsList = reviewsList;
        this.token = token;
        this.city = city;
        this.expandable = expandable;
    }

    public Hotel(String hotelName, float distanceFromCenter, int price, int stars, String imageUrl, String city) {
        this.hotelName = hotelName;
        this.distanceFromCenter = distanceFromCenter;
        this.price = price;
        this.stars = stars;
        this.imageUrl = imageUrl;
        this.city = city;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public float getDistanceFromCenter() {
        return distanceFromCenter;
    }

    public void setDistanceFromCenter(float distanceFromCenter) {
        this.distanceFromCenter = distanceFromCenter;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    @Override
    public String toString() {
        return "Hotel{" +
                "hotelName='" + hotelName + '\'' +
                ", distanceFromCenter=" + distanceFromCenter +
                ", price=" + price +
                ", stars=" + stars +
                ", imageUrl='" + imageUrl + '\'' +
                ", rating=" + rating +
                ", ratingsList=" + ratingsList +
                ", reviewsList=" + reviewsList +
                ", token='" + token + '\'' +
                ", city='" + city + '\'' +
                ", expandable=" + expandable +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public HashMap<String, Rating> getRatingsList() {
        return ratingsList;
    }

    public void setRatingsList(HashMap<String, Rating> ratingsList) {
        this.ratingsList = ratingsList;
    }

    public HashMap<String, Review> getReviewsList() {
        return reviewsList;
    }

    public void setReviewsList(HashMap<String, Review> reviewsList) {
        this.reviewsList = reviewsList;
    }
}
