package ratingapp.ddey.com.testratingapp.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.models.Rating;
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;

public class ViewPublicProfileActivity extends AppCompatActivity {

    private LinearLayout llOptional;
    private CircleImageView ivProfile;
    private TextView tvName;
    private TextView tvCountry;
    private TextView tvEmail;
    private TextView tvTravelType;
    private TextView tvReviews;
    private TextView tvRatings;
    private TextView tvBirthdate;
    private ProgressBar pbProgress;
    private User currentUser;
    private FirebaseHelper mFirebaseHelper;
    private int contorRatings = 0;
    private int contorReviews = 0;

    private List<Hotel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_public_profile);
        initializeComponents();
    }

    private void initializeComponents() {
        llOptional = findViewById(R.id.ll_optional);
        ivProfile = findViewById(R.id.viewprofile_iv_user);
        tvName = findViewById(R.id.viewprofile_tv_name);
        tvCountry = findViewById(R.id.viewprofile_tv_country);
        tvEmail = findViewById(R.id.viewprofile_tv_email);
        tvTravelType = findViewById(R.id.viewprofile_tv_traveltype);
        tvReviews = findViewById(R.id.viewprofile_tv_reviews);
        tvRatings = findViewById(R.id.viewprofile_tv_ratings);
        tvBirthdate = findViewById(R.id.viewprofile_tv_birthdate);
        pbProgress = findViewById(R.id.viewpublicprofile_progressbar);

        this.setTitle("View profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        incIntent();
    }

    private void incIntent() {
        if (getIntent().hasExtra(Constants.VIEW_PROFILE_KEY)) {
            Intent recvIntent = getIntent();
            pbProgress.setVisibility(View.VISIBLE);
            if (recvIntent != null) {

                currentUser = (User) recvIntent.getSerializableExtra(Constants.VIEW_PROFILE_KEY);
                if (currentUser != null) {
                    if (currentUser.getName() != null) {
                        tvName.setText(currentUser.getName());
                    }
                    if (currentUser.getCountry() != null) {
                        tvCountry.setText(currentUser.getCountry());
                    }
                    if (currentUser.getEmail() != null) {
                        tvEmail.setText(currentUser.getEmail());
                    }
                    if (currentUser.getTravelGroup() != null) {
                        llOptional.setVisibility(View.VISIBLE);
                        tvTravelType.setText(currentUser.getTravelGroup());
                    } else {
                        llOptional.setVisibility(View.GONE);
                    }

                    if (currentUser.getFirebaseToken() != null) {
                        getReviewRatingNumberForUser();
                    }

                    if (currentUser.getProfilePictureURL() != null) {
                        ivProfile.setBorderWidth(1);
                        Picasso.with(this)
                                .load(currentUser.getProfilePictureURL())
                                .error(R.drawable.ic_error_120)
                                .into(ivProfile);
                    } else {
                        ivProfile.setImageResource(R.drawable.ic_profile_120);
                        ivProfile.setBorderWidth(0);
                    }
                }
            }
        }
    }

    private void getReviewRatingNumberForUser() {
        list = new ArrayList<>();
        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();
        mFirebaseHelper.getHotelsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Hotel h = data.getValue(Hotel.class);
                    if (h != null) {
                        list.add(h);
                    }
                }
                getRest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRest() {
        for (Hotel h : list) {
            Log.i("ViewProfile", h.toString());
            if (h.getRatingsList() != null) {
                mFirebaseHelper.getHotelsReference().child(h.getToken()).child("ratingsList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Rating rating = data.getValue(Rating.class);
                            Log.i("ViewProfile", rating.toString());
                            if (rating.getUserToken() != null) {
                                if (rating.getUserToken().equals(currentUser.getFirebaseToken())) {
                                    contorRatings++;
                                }
                            }
                        }

                        tvRatings.setText(String.valueOf(contorRatings));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                tvRatings.setText(String.valueOf(0));
            }

            if (h.getReviewsList() != null) {
                mFirebaseHelper.getHotelsReference().child(h.getToken()).child("reviewsList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Review review = data.getValue(Review.class);
                            Log.i("ViewProfile", review.toString());
                            if (review.getUserToken() != null) {
                                if (review.getUserToken().equals(currentUser.getFirebaseToken())) {
                                    contorReviews++;
                                }
                            }
                        }
                        tvReviews.setText(String.valueOf(contorReviews));
                        pbProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                tvReviews.setText(String.valueOf(0));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

}
