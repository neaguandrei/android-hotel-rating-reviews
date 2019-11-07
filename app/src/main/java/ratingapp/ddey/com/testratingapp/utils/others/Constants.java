package ratingapp.ddey.com.testratingapp.utils.others;
import java.text.SimpleDateFormat;

public interface Constants {
    String DATE_FORMAT = "dd-MM-yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    String SEARCH_KEY = "searchFilter";
    String VIEW_REVIEWS_RATINGS_KEY = "viewReviewsRatings";
    String BUNDLE_HOTEL_KEY =  "bundleHotelKey";
    String VIEW_PROFILE_KEY = "viewProfileKey";
    String HOTEL_VIEW_REVIEW_CHART_KEY = "viewChartReviews";
    String HOTEL_VIEW_RATING_CHART_KEY = "viewChartRatings";
    String HOTEL_VIEW_CHART_REVIEW_OR_RATING_KEY = "reviewOrRating";
    int REQUEST_CODE_FAVOURITE_VIEWPLACE_ACTIVITY = 1;
    int REQUEST_CODE_EDIT_PROFILE = 2;
    int REQUEST_CODE_UPLOAD_PHOTO = 3;
    int REQUEST_CODE_CAMERA_PHOTO = 4;
    int REQUEST_CODE_PERMISSION_READ_EXTERNAL_DATA = 5;
}
