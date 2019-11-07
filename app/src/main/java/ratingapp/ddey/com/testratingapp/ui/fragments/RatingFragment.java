package ratingapp.ddey.com.testratingapp.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.ui.activities.PieChartActivity;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.models.Rating;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.others.Session;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;


public class RatingFragment extends Fragment {
    private static final String TAG = "RatingFragment";

    private View v;
    private Hotel mHotel;
    private String userToken;

    private FirebaseHelper mFirebaseHelper;
    private Session mSession;
    private DatabaseHelper mDbHelper;

    private RatingBar rbRating;
    private TextView tvTotalRaters;
    private TextView tvAvgRating;
    private TextView tvOutOf5;
    private FloatingActionButton btnViewChart;

    private Rating currentRating;
    private boolean hasRatings = true;
    private boolean alreadyRated = false;
    private float averageRating;
    private int totalRatings;

    private ProgressBar pb5;
    private ProgressBar pb4;
    private ProgressBar pb3;
    private ProgressBar pb2;
    private ProgressBar pb1;

    private boolean firstLoad = true;
    private boolean isRated = false;

    private DatabaseHelper mDb;
    private Hotel resultHotel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_rating, container, false);
        if (getArguments() != null) {
            mHotel = (Hotel) getArguments().getSerializable(Constants.BUNDLE_HOTEL_KEY);
            if (mHotel != null) {
                Log.i(TAG, "Hotel: " + mHotel.toString());
                initializeComponents();
                checkIfUserRatedThisBefore();
            }
        }
        return v;
    }

    private void initializeComponents() {
        currentRating = null;

        if (getActivity() != null) {
            mDb = new DatabaseHelper(getActivity());
        }
        pb1 = v.findViewById(R.id.progress1);
        pb2 = v.findViewById(R.id.progress2);
        pb3 = v.findViewById(R.id.progress3);
        pb4 = v.findViewById(R.id.progress4);
        pb5 = v.findViewById(R.id.progress5);
        if (getActivity() != null) {
            mSession = new Session(getActivity());
            mDbHelper = new DatabaseHelper(getActivity());
        }

        btnViewChart = v.findViewById(R.id.reviews_btn_view_chart);
        tvOutOf5 = v.findViewById(R.id.outof5);
        tvTotalRaters = v.findViewById(R.id.fragment_total_number_of_ratings);
        tvAvgRating = v.findViewById(R.id.fragment_average_rating);

        userToken = mDbHelper.getUserToken(mSession);
        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();

        rbRating = v.findViewById(R.id.rb_hotel_rating);
        rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                upsertRating(rating);
                setAvgTotalRatingsData();
                if (!firstLoad) {
                    displayDialog();
                }
                firstLoad = false;
            }
        });

        btnViewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHotel.getRatingsList() != null || isRated) {
                    Intent intent = new Intent(getActivity(), PieChartActivity.class);
                    intent.putExtra(Constants.HOTEL_VIEW_REVIEW_CHART_KEY, mHotel);
                    intent.putExtra(Constants.HOTEL_VIEW_CHART_REVIEW_OR_RATING_KEY, 1);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "You can't visualize the chart because the hotel doesn't have any ratings yet", Toast.LENGTH_LONG).show();
                }
            }
        });
        setAvgTotalRatingsData();
    }

    private void checkIfUserRatedThisBefore() {
        mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("ratingsList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    hasRatings = false;
                } else {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data != null) {
                            Rating r = data.getValue(Rating.class);
                            if (r != null) {
                                if (r.getHotelToken().equals(mHotel.getToken()) && r.getUserToken().equals(userToken)) {
                                    alreadyRated = true;
                                    currentRating = r;
                                }
                            }
                        }
                    }
                }
                // firstLoad devine false daca nu are rating ca sa apara din prima.
                if (hasRatings) {
                    if (alreadyRated && currentRating != null) {
                        rbRating.setRating(currentRating.getRateValue());
                    }
                } else {
                    firstLoad = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Hotel database check error");
            }
        });
    }

    // Preiau din Firebase total ratings + avg ratings
    private void setAvgTotalRatingsData() {
        mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("ratingsList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    List<Float> ratingsList = new ArrayList<>();
                    averageRating = 0;
                    totalRatings = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Rating r = data.getValue(Rating.class);
                        if (r != null) {
                            ratingsList.add(r.getRateValue());
                            averageRating += r.getRateValue();
                            totalRatings++;
                        }
                    }
                    setProgressBars(ratingsList);
                    averageRating /= dataSnapshot.getChildrenCount();
                    averageRating = roundToHalf(averageRating);
                    tvAvgRating.setTextSize(55);
                    tvAvgRating.setText(String.valueOf(averageRating));
                    tvOutOf5.setVisibility(View.VISIBLE);
                    tvTotalRaters.setText(totalRatings + " Ratings");
                    mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("rating").setValue(averageRating);
                    isRated = true;
                } else {
                    tvAvgRating.setText("No ratings yet");
                    tvOutOf5.setVisibility(View.GONE);
                    tvAvgRating.setTextSize(20);
                    tvTotalRaters.setText("Number of ratings: 0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Hotel database check error");
            }
        });
    }

    // Ca sa nu am valori de genul 3.66...
    private float roundToHalf(float f) {
        return (float) (Math.round(f * 2) / 2.0);
    }

    private void setProgressBars(List<Float> ratingsList) {
        final List<Integer> numberOfRatingsForEachStar = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Integer nr = new Integer(0);
            // Pentru poz 0 din numberOfRatingsForEachStar o sa am nr de ratinguri cu 1 stea
            for (Float f : ratingsList) {
                if (f == i) {   // Pentru restu de poz din numberOfRatingsForEachStar o sa am nr de ratinguri cu 2 si 2.5 etc...
                    nr++;
                } else if (f == (float) (i + 0.5)){
                    nr++;
                }

            }
            numberOfRatingsForEachStar.add(nr);
        }

        int percent1 = numberOfRatingsForEachStar.get(0) * 100 / totalRatings;
        int percent2 = numberOfRatingsForEachStar.get(1) * 100 / totalRatings;
        int percent3 = numberOfRatingsForEachStar.get(2) * 100 / totalRatings;
        int percent4 = numberOfRatingsForEachStar.get(3) * 100 / totalRatings;
        int percent5 = numberOfRatingsForEachStar.get(4) * 100 / totalRatings;

        pb1.setProgress(percent1);
        pb2.setProgress(percent2);
        pb3.setProgress(percent3);
        pb4.setProgress(percent4);
        pb5.setProgress(percent5);
    }

    private void displayDialog() {
        if (getActivity() != null) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                    .setMessage("Thanks for rating " + mHotel.getHotelName() + "!");
            final AlertDialog alert = dialog.create();
            alert.show();
            // Autoinchidere dupa cateva secunde
            final Handler handler  = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (alert.isShowing()) {
                        alert.dismiss();
                    }
                }
            };

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 1000);
        }

    }

    // Update sau insert daca exista sau nu ratingul. Dupa aceea am listener pentru reload la restul datelor din fragment
    private void upsertRating(float rating) {

        if (currentRating != null) {
            currentRating.setRateValue(rating);
            mDb.updateMyRatings(currentRating);
            mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("ratingsList").child(currentRating.getRatingToken()).setValue(currentRating);
        } else {
            currentRating = new Rating();
            currentRating.setHotelToken(mHotel.getToken());
            currentRating.setUserToken(userToken);
            currentRating.setRateValue(rating);
            currentRating.setDate(new Date());
            currentRating.setRatingToken(mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("ratingsList").push().getKey());
            mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("ratingsList").child(currentRating.getRatingToken()).setValue(currentRating);
            mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("ratingsList").child(currentRating.getRatingToken()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Rating r = dataSnapshot.getValue(Rating.class);
                    if (r != null) {
                        isRated = true;
                        setAvgTotalRatingsData();
                        mDb.insertMyRating(currentRating);
                        insertHotelIntoLocalDbIfItDoesntExist();
                        Log.i(TAG, "Loading other ratings data after successful upsert");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Hotel database check error");
                }
            });

        }
    }

    private void insertHotelIntoLocalDbIfItDoesntExist() {
        mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Hotel h = dataSnapshot.getValue(Hotel.class);
                    if (h != null) {
                        resultHotel = h;
                        if (!mDb.isHotelInDb(currentRating.getHotelToken())) {
                            mDb.insertMyHotel(resultHotel, mDb.getUserToken(mSession));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
