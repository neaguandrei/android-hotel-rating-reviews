package ratingapp.ddey.com.testratingapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.models.Rating;
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.others.Session;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;

public class ViewProfileActivity extends AppCompatActivity {
    private View backgroundBlur;

    private LinearLayout llOptional;
    private LinearLayout ll2;
    private CircleImageView ivProfile;
    private TextView tvName;
    private TextView tvCountry;
    private TextView tvEmail;
    private TextView tvTravelType;
    private TextView tvReviews;
    private TextView tvRatings;
    private TextView tvBirthdate;
    private TextView tvPrivacy;
    private ProgressBar pbProgress;

    private User currentUser;
    private FirebaseHelper mFirebaseHelper;
    private int contorRatings = 0;
    private int contorReviews = 0;
    private List<Hotel> list;
    private DatabaseHelper mDb;
    private Session mSession;

    private Target targetCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        initializeComponents();
    }

    private void initializeComponents() {
        backgroundBlur = findViewById(R.id.root_viewprofile_view);
        mDb = new DatabaseHelper(this);
        mSession = new Session(this);
        mFirebaseHelper = FirebaseHelper.getInstance();

        ll2 = findViewById(R.id.ll2);
        pbProgress = findViewById(R.id.viewprofile_progressbar);
        llOptional = findViewById(R.id.ll_optional2);
        ivProfile = findViewById(R.id.viewprofile_iv_user);
        tvName = findViewById(R.id.viewprofile_tv_name);
        tvCountry = findViewById(R.id.viewprofile_tv_country);
        tvEmail = findViewById(R.id.viewprofile_tv_email);
        tvTravelType = findViewById(R.id.viewprofile_tv_traveltype);
        tvReviews = findViewById(R.id.viewprofile_tv_reviews);
        tvRatings = findViewById(R.id.viewprofile_tv_ratings);
        ImageView ivEditProfile = findViewById(R.id.viewprofile_iv_editprofile);
        tvPrivacy = findViewById(R.id.viewprofile_tv_privacy);
        tvBirthdate = findViewById(R.id.viewprofile_tv_birthdate);

        this.setTitle("View profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadUserData();
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyConnection()) {
                    Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_PROFILE);
                } else {
                    Toast.makeText(getApplicationContext(), "You can't edit your profile without an internet connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        initTargetCallback();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_CODE_EDIT_PROFILE && data != null) {
            loadUserData();
        }
    }


    private void loadUserData() {
        contorReviews = 0;
        contorRatings = 0;
        String userToken = mDb.getUserToken(mSession);
        pbProgress.setVisibility(View.VISIBLE);
        if (verifyConnection()) {
            loadUserFromFirebase(userToken);
        } else {
            loadFromLocal();
            Toast.makeText(getApplicationContext(), "No internet connection! Showing offline data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromLocal() {
        currentUser = mDb.retrieveUser(mSession);
        setInformation();
        pbProgress.setVisibility(View.GONE);
    }

    private void setInformation() {
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
            if (currentUser.getTravelGroup() != null && !currentUser.getTravelGroup().trim().isEmpty()) {
                llOptional.setVisibility(View.VISIBLE);
                tvTravelType.setText(currentUser.getTravelGroup());
            } else {
                llOptional.setVisibility(View.GONE);
            }

            if (currentUser.getFirebaseToken() != null) {
                getReviewRatingNumberForUser();
            }

            if (currentUser.isProfilePublic()) {
                tvPrivacy.setText("Public profile");
            } else {
                tvPrivacy.setText("Private profile");
            }

            if (currentUser.getBirthDate() != null) {
                ll2.setVisibility(View.VISIBLE);
                tvBirthdate.setText(Constants.simpleDateFormat.format(currentUser.getBirthDate()));
            } else {
                ll2.setVisibility(View.GONE);
            }
        }
    }

    private void setProfilePicture() {
        if (currentUser != null) {
            if (currentUser.getProfilePictureURL() != null) {
                ivProfile.setBorderWidth(1);
                pbProgress.setVisibility(View.VISIBLE);
                Picasso.with(this)
                        .load(currentUser.getProfilePictureURL())
                        .error(R.drawable.ic_error_120)
                        .into(targetCallback);
            } else {
                ivProfile.setImageResource(R.drawable.ic_profile_120);
                ivProfile.setBorderWidth(0);
                pbProgress.setVisibility(View.GONE);
                backgroundBlur.setBackgroundColor(Color.argb(0, 0, 0, 0));
            }
        }
    }

    private void initTargetCallback() {
        targetCallback = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ivProfile.setImageBitmap(bitmap);
                pbProgress.setVisibility(View.GONE);
                backgroundBlur.setBackgroundColor(Color.argb(0, 0, 0, 0));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                pbProgress.setVisibility(View.GONE);
                backgroundBlur.setBackgroundColor(Color.argb(0, 0, 0, 0));
                Toast.makeText(getApplicationContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

    public void loadUserFromFirebase(String userToken) {
        backgroundBlur.setBackgroundColor(Color.argb(234, 82, 84, 79));
        mFirebaseHelper.openConnection();
        mFirebaseHelper.getUsersReference().child(userToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    currentUser = user;
                    setInformation();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
                setProfilePicture();
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

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        contorRatings = 0;
        contorReviews = 0;
        setProfilePicture();
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(getApplicationContext());
        return connection.isOnline();
    }
}
