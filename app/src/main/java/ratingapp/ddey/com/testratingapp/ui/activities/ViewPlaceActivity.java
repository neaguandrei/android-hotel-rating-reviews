package ratingapp.ddey.com.testratingapp.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.GoogleHotel;
import ratingapp.ddey.com.testratingapp.utils.retrofit.Common;
import ratingapp.ddey.com.testratingapp.models.GoogleReview;
import ratingapp.ddey.com.testratingapp.utils.retrofit.models.PlaceDetail;
import ratingapp.ddey.com.testratingapp.utils.retrofit.remote.MapsService;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlaceActivity extends AppCompatActivity {
    private static final int MAXIMUM_WIDTH = 1000;

    private ImageView photo;
    private RatingBar ratingBar;
    private TextView openingHours, place_address, place_name;
    private Button btnViewOnMap;
    private Button btnAddToFav;

    private GoogleHotel googleHotel;
    private GoogleReview[] googleReviewList;
    private MapsService mService;
    private PlaceDetail mPlace;
    private DatabaseHelper mDb;
    private Session mSession;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        mService = Common.getGoogleAPIService();
        photo = findViewById(R.id.viewplace_photo);
        ratingBar = findViewById(R.id.viewplace_ratingbar);
        place_address = findViewById(R.id.viewplace_address);
        place_name = findViewById(R.id.viewplace_placename);
        openingHours = findViewById(R.id.viewplace_openhours);
        btnViewOnMap = findViewById(R.id.viewplace_btn_showmap);
        btnAddToFav = findViewById(R.id.viewplace_btn_add_fav);

        mSession = new Session(this);
        mDb = new DatabaseHelper(this);
        googleHotel = new GoogleHotel();


        if (Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {
            Picasso.with(this)
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), MAXIMUM_WIDTH)) // luam primu element din array
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(photo);
            googleHotel.setImgUrl(Common.currentResult.getPhotos()[0].getPhoto_reference());
        }

        //Rating
        if (Common.currentResult.getRating() != null && !TextUtils.isEmpty(Common.currentResult.getRating())) {

            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
            googleHotel.setStarsNumber(Float.parseFloat(Common.currentResult.getRating()));
        } else
        {
            ratingBar.setVisibility(View.GONE);
        }

        //Opening hours
        if (Common.currentResult.getOpeningHours() != null) {

            openingHours.setText("Open now: " + Common.currentResult.getOpeningHours().getOpen_now());
        } else
        {
            openingHours.setVisibility(View.GONE);
        }

        //User service to fetch address and name
        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlace = response.body();
                        place_address.setText(mPlace.getResult().getFormatted_address());
                        place_name.setText(mPlace.getResult().getName());

                        googleHotel.setAddress(mPlace.getResult().getFormatted_address());
                        googleHotel.setName(mPlace.getResult().getName());
                        googleHotel.setContactNumber(mPlace.getResult().getFormatted_phone_number());
                        googleReviewList = mPlace.getResult().getReviews();
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });

        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(mapIntent);
            }
        });

        btnAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDb.insertHotels(googleHotel, mDb.getUserToken(mSession));
                for (GoogleReview r: googleReviewList) {
                    mDb.insertReview(r, googleHotel);
                }
                Intent returnIntent = getIntent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        this.setTitle("Selected hotel");
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        url.append("placeid="+place_id);
        url.append("&key="+"AIzaSyB1IkhBtQhXItMjslFNp3Poy3_dPNdAFuE");
        return url.toString();
    }
    private String getPhotoOfPlace(String photoReference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth="+maxWidth);
        url.append("&photoreference="+photoReference);
        url.append("&key=AIzaSyB1IkhBtQhXItMjslFNp3Poy3_dPNdAFuE");

        return url.toString();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }
}
