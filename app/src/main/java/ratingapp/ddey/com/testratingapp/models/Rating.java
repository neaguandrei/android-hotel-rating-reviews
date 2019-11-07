package ratingapp.ddey.com.testratingapp.models;

import java.io.Serializable;
import java.util.Date;

public class Rating implements Serializable {
    private String userToken;
    private String hotelToken;
    private String ratingToken;
    private float rateValue;
    private Date date;

    public Rating() {

    }

    public Rating(String userToken, String hotelToken, String ratingToken, float rateValue) {
        this.userToken = userToken;
        this.hotelToken = hotelToken;
        this.ratingToken = ratingToken;
        this.rateValue = rateValue;
    }

    public Rating(String userToken, String hotelToken, float rateValue) {
        this.userToken = userToken;
        this.hotelToken = hotelToken;
        this.rateValue = rateValue;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getHotelToken() {
        return hotelToken;
    }

    public void setHotelToken(String hotelToken) {
        this.hotelToken = hotelToken;
    }

    public float getRateValue() {
        return rateValue;
    }

    public void setRateValue(float rateValue) {
        this.rateValue = rateValue;
    }

    public String getRatingToken() {
        return ratingToken;
    }

    public void setRatingToken(String ratingToken) {
        this.ratingToken = ratingToken;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "userToken='" + userToken + '\'' +
                ", hotelToken='" + hotelToken + '\'' +
                ", ratingToken='" + ratingToken + '\'' +
                ", rateValue=" + rateValue +
                ", date=" + date +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
