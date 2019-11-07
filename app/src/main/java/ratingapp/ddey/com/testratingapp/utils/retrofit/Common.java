package ratingapp.ddey.com.testratingapp.utils.retrofit;

import ratingapp.ddey.com.testratingapp.utils.retrofit.models.Results;
import ratingapp.ddey.com.testratingapp.utils.retrofit.remote.MapsService;
import ratingapp.ddey.com.testratingapp.utils.retrofit.remote.RetrofitClient;

public class Common {

    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";
    public static Results currentResult;
    public static MapsService getGoogleAPIService() {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(MapsService.class);
    }
}
