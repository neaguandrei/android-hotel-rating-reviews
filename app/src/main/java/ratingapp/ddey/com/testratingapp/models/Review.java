package ratingapp.ddey.com.testratingapp.models;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {
    private String userToken;
    private String reviewToken;
    private String hotelToken;
    private String title;
    private String description;
    private Date date;
    private boolean isPositive;

    public Review() {
    }

    public Review(String userToken, String reviewToken, String hotelToken, String title, String description, Date date, boolean isPositive) {
        this.userToken = userToken;
        this.reviewToken = reviewToken;
        this.hotelToken = hotelToken;
        this.title = title;
        this.description = description;
        this.date = date;
        this.isPositive = isPositive;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getReviewToken() {
        return reviewToken;
    }

    public void setReviewToken(String reviewToken) {
        this.reviewToken = reviewToken;
    }

    public String getHotelToken() {
        return hotelToken;
    }

    public void setHotelToken(String hotelToken) {
        this.hotelToken = hotelToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    @Override
    public String toString() {
        return "Review{" +
                "userToken='" + userToken + '\'' +
                ", reviewToken='" + reviewToken + '\'' +
                ", hotelToken='" + hotelToken + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", isPositive=" + isPositive +
                '}';
    }
}
