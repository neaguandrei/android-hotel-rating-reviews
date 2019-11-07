package ratingapp.ddey.com.testratingapp.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
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
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.others.Session;
import ratingapp.ddey.com.testratingapp.utils.adapters.ReviewAdapter;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;

public class ReviewFragment extends Fragment {
    private static final String TAG = "ReviewFragment";
    private View v;
    private Hotel mHotel;

    private TextView tvPositive;
    private TextView tvNegative;
    private FloatingActionButton btnAddReview;
    private FloatingActionButton btnViewChart;
    private RecyclerView rvReviews;
    private ProgressBar pbReviews;


    private List<Review> mList;
    private ReviewAdapter mAdapter;

    private boolean hasReviews = true;
    private FirebaseHelper mFirebaseHelper;
    private DatabaseHelper mDb;
    private Session mSession;
    private Dialog mDialog;
    private Review newReview;
    private TextInputEditText tieTitle;
    private TextInputEditText tieDesc;
    private Switch swPositive;
    private ImageView ivPositive;


    private Hotel resultHotel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_review, container, false);
        if (getArguments() != null) {
            mHotel = (Hotel) getArguments().getSerializable(Constants.BUNDLE_HOTEL_KEY);
            if (mHotel != null) {
                Log.i(TAG, "Hotel: " + mHotel.toString());
                initializeComponents();
            }
        }
        return v;
    }

    private void initializeComponents() {
        tvPositive = v.findViewById(R.id.reviews_tv_positivereviews);
        tvNegative = v.findViewById(R.id.reviews_tv_negativereviews);
        btnAddReview = v.findViewById(R.id.reviews_btn_add_review);
        pbReviews = v.findViewById(R.id.reviews_progress_bar);
        rvReviews = v.findViewById(R.id.reviews_rv_reviews);
        btnViewChart = v.findViewById(R.id.reviews_btn_view_chart);

        btnViewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    if (mHotel.getReviewsList() != null || mList.size() > 0) {
                        Intent intent = new Intent(getActivity(), PieChartActivity.class);
                        intent.putExtra(Constants.HOTEL_VIEW_REVIEW_CHART_KEY, mHotel);
                        intent.putExtra(Constants.HOTEL_VIEW_CHART_REVIEW_OR_RATING_KEY, 0);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "You can't visualize the chart because the hotel doesn't have any reviews yet", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildInputDialog();
            }
        });
        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();
        if (getActivity() != null) {
            mDb = new DatabaseHelper(getActivity());
            mSession = new Session(getActivity());
            loadReviews();
        }
    }


    private void buildInputDialog() {
        if (getActivity() != null) {
            mDialog = new Dialog(getActivity());
            mDialog.setContentView(R.layout.custom_dialog_add_review);
            mDialog.setTitle("Add a review");

            tieTitle = mDialog.findViewById(R.id.tie_title);
            tieDesc = mDialog.findViewById(R.id.tie_desc);
            swPositive = mDialog.findViewById(R.id.dialog_switch);
            ivPositive = mDialog.findViewById(R.id.dialog_iv_isPositive);

            // default
            swPositive.setChecked(true);
            ivPositive.setImageResource(R.drawable.ic_thumb_up);
            swPositive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ivPositive.setImageResource(R.drawable.ic_thumb_up);
                        swPositive.setText("Positive");
                    } else {
                        ivPositive.setImageResource(R.drawable.ic_thumb_down);
                        swPositive.setText("Negative");
                    }
                }
            });

            Button btnOk = mDialog.findViewById(R.id.dialog_btn_add);
            Button btnCancel = mDialog.findViewById(R.id.dialog_btn_cancel);

            btnOk.setEnabled(true);
            btnCancel.setEnabled(true);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertReview();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.cancel();
                }
            });
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.show();

        }
    }

    private void insertReview() {
        if (isDialogValid()) {
            newReview = new Review();
            newReview.setTitle(tieTitle.getText().toString());
            newReview.setDescription(tieDesc.getText().toString());
            newReview.setHotelToken(mHotel.getToken());
            newReview.setUserToken(mDb.getUserToken(mSession));
            newReview.setDate(new Date());
            if (swPositive.isChecked()){
                newReview.setPositive(true);
            } else {
                newReview.setPositive(false);
            }
            newReview.setReviewToken(mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("reviewsList").push().getKey());
            mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("reviewsList").child(newReview.getReviewToken()).setValue(newReview);
            loadReviews();
            mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("reviewsList").child(newReview.getReviewToken()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Review r = dataSnapshot.getValue(Review.class);
                    if (r != null) {
                        Log.i(TAG, "Upsert successful" + r.toString());
                    }
                    mDb.insertMyReview(newReview);
                    insertHotelIntoLocalDbIfItDoesntExist();
                    mDialog.cancel();
                    mList.add(newReview);
                    loadReviews();
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
                        if (!mDb.isHotelInDb(newReview.getHotelToken())) {
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


    private boolean isDialogValid() {
        if (tieTitle.getText() == null || tieTitle.getText().toString().trim().isEmpty()) {
            tieTitle.setFocusable(true);
            tieTitle.setError("Insert a title before adding the review");
            return false;
        }

        if (tieDesc.getText() == null || tieDesc.getText().toString().trim().isEmpty()) {
            tieDesc.setFocusable(true);
            tieDesc.setError("Insert a description before adding the review");
            return false;
        }
        return true;
    }

    private void loadReviews() {
        pbReviews.setVisibility(View.VISIBLE);
        mList = new ArrayList<>();
        mList.clear();
        mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("reviewsList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    hasReviews = false;
                } else {
                    hasReviews = true;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data != null) {
                            Review review = data.getValue(Review.class);
                            if (review != null) {
                                if (review.getHotelToken().equals(mHotel.getToken())) {
                                    mList.add(review);
                                }
                            }
                        }
                    }
                }
                if (hasReviews) {
                    initializeReviewsRecyclerView();
                }
                setTotalReviews();
                pbReviews.setVisibility(View.GONE);
                Log.d(TAG, "Loaded reviews successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, " " + databaseError.getMessage());
            }
        });
    }

    private void initializeReviewsRecyclerView() {
        if (getActivity() != null) {
            mAdapter = new ReviewAdapter(getActivity().getApplicationContext(), mList, getActivity());
            rvReviews.setAdapter(mAdapter);
            rvReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void setTotalReviews() {
        mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("reviewsList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    int positive = 0;
                    int negative = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Review review = data.getValue(Review.class);
                        if (review != null) {
                            if (review.isPositive()) {
                                positive++;
                            } else {
                                negative++;
                            }
                        }
                    }

                    if (positive != 0) {
                        tvPositive.setText("Positive reviews (" + positive + ")");
                    } else {
                        tvPositive.setText("No positive reviews yet");
                    }
                    if (negative != 0) {
                        tvNegative.setText("Negative reviews (" + negative + ")");
                    } else {
                        tvNegative.setText("No positive reviews yet");

                    }
                } else {
                    tvPositive.setText("No positive reviews yet");
                    tvNegative.setText("No positive reviews yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Hotel database check error");
            }
        });
    }



}
