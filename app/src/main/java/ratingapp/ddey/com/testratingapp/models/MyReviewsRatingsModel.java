package ratingapp.ddey.com.testratingapp.models;

public class MyReviewsRatingsModel {
    private String hotelName;
    private String city;
    private String reviewTitle;
    private String reviewDescription;
    private boolean isReviewPositive;
    private float ratingValue;
    private String userToken;
    private String reviewToken;
    private String ratingToken;

    public MyReviewsRatingsModel() {
        hotelName = null;
        city = null;
        reviewDescription = null;
        reviewTitle = null;
        ratingValue = -1;
        userToken = null;
        reviewToken = null;
        ratingToken = null;
    }

    public MyReviewsRatingsModel(String hotelName, String city, String reviewTitle, String reviewDescription, boolean isReviewPositive, float ratingValue, String userToken) {
        this.hotelName = hotelName;
        this.city = city;
        this.reviewTitle = reviewTitle;
        this.reviewDescription = reviewDescription;
        this.isReviewPositive = isReviewPositive;
        this.ratingValue = ratingValue;
        this.userToken = userToken;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public boolean isReviewPositive() {
        return isReviewPositive;
    }

    public void setReviewPositive(boolean reviewPositive) {
        isReviewPositive = reviewPositive;
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
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

    public String getRatingToken() {
        return ratingToken;
    }

    public void setRatingToken(String ratingToken) {
        this.ratingToken = ratingToken;
    }
}
