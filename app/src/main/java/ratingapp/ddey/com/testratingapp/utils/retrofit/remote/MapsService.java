package ratingapp.ddey.com.testratingapp.utils.retrofit.remote;

import ratingapp.ddey.com.testratingapp.utils.retrofit.models.MyPlaces;
import ratingapp.ddey.com.testratingapp.utils.retrofit.models.PlaceDetail;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MapsService {

    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);
}

