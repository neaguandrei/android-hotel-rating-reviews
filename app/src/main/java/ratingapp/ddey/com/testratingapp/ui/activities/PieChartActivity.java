package ratingapp.ddey.com.testratingapp.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.models.Rating;
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;


public class PieChartActivity extends AppCompatActivity {
    private static final String TAG = "ChartFragment";
    private Hotel mHotel;
    private ProgressBar pbReviews;
    private FirebaseHelper mFirebaseHelper;
    private boolean hasReviews = true;
    private PieChart pieChart;

    private int nrPositive;
    private int nrNegative;
    private int[] eachRatingCount;
    private int isReviewOrRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        Intent recvIntent = getIntent();
        if (recvIntent.hasExtra(Constants.HOTEL_VIEW_CHART_REVIEW_OR_RATING_KEY)) {
            int whichTitle = recvIntent.getIntExtra(Constants.HOTEL_VIEW_CHART_REVIEW_OR_RATING_KEY, -1);
            if (whichTitle == 1) {
                setTitle("Ratings Chart");
            } else {
                setTitle("Reviews Chart");
            }
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeComponents();
        incIntent();
    }

    private void incIntent() {
        if (getIntent().hasExtra(Constants.HOTEL_VIEW_REVIEW_CHART_KEY) && getIntent().hasExtra(Constants.HOTEL_VIEW_CHART_REVIEW_OR_RATING_KEY)) {
            Intent intent = getIntent();
            if (intent != null) {
                mHotel = (Hotel) intent.getSerializableExtra(Constants.HOTEL_VIEW_REVIEW_CHART_KEY);
                isReviewOrRating = intent.getIntExtra(Constants.HOTEL_VIEW_CHART_REVIEW_OR_RATING_KEY, -1);
                if (mHotel != null && isReviewOrRating == 0) {
                    loadReviews();
                } else if (mHotel != null && isReviewOrRating == 1) {
                    loadRatings();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occured! Chart is not available for this hotel", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

    }


    private void initializeComponents() {
        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();
        pieChart = findViewById(R.id.chart_pieChart);
        pbReviews = findViewById(R.id.chart_pbProgress);
        incIntent();
    }

    private void loadReviews() {
        pbReviews.setVisibility(View.VISIBLE);
        nrNegative = 0;
        nrPositive = 0;
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
                                    if (review.isPositive()) {
                                        nrPositive++;
                                    } else {
                                        nrNegative++;
                                    }
                                }
                            }
                        }
                    }
                }
                if (hasReviews) {
                    initializeChart();
                }
                pbReviews.setVisibility(View.GONE);
                Log.d(TAG, "Loaded reviews successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, " " + databaseError.getMessage());
            }
        });
    }

    private void loadRatings() {
        pbReviews.setVisibility(View.VISIBLE);
        mFirebaseHelper.getHotelsReference().child(mHotel.getToken()).child("ratingsList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eachRatingCount =  new int[5];
                for (int i = 0; i < eachRatingCount.length; i++) {
                    eachRatingCount[i] = 0;
                }
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Rating r = data.getValue(Rating.class);
                        if (r != null) {
                            if (r.getHotelToken().equals(mHotel.getToken())) {
                                for (int i = 0; i < 5; i++) {
                                    if (r.getRateValue() == i+1) {
                                        eachRatingCount[i]++;
                                    }
                                }
                            }
                        }
                    }
                }
                initializeChart();
                pbReviews.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Hotel database check error");
            }
        });

    }


    private void initializeChart() {

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        if (isReviewOrRating == 0) {
            ArrayList<PieEntry> yValues = new ArrayList<>();
            if (nrNegative > 0) {
                yValues.add(new PieEntry(nrNegative, "Negative"));
            }
            if (nrPositive > 0) {
                yValues.add(new PieEntry(nrPositive, "Positive"));
            }

            Description chartDescription = new Description();
            chartDescription.setText("Reviews for " + mHotel.getHotelName());
            chartDescription.setTextSize(15);
            pieChart.setDescription(chartDescription);

            pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);


            Legend legend = pieChart.getLegend();
            legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            legend.setXEntrySpace(7);
            legend.setYEntrySpace(5);

            final int[] PERSONAL_COLOR_TEMPLATE = {
                    Color.rgb(139, 0, 0), Color.rgb(0, 100, 0)
            };
            ArrayList<Integer> colors = new ArrayList<>();

            if (nrNegative > 0) {
                colors.add(PERSONAL_COLOR_TEMPLATE[0]);
            }
            if (nrPositive > 0) {
                colors.add(PERSONAL_COLOR_TEMPLATE[1]);
            }

            List<LegendEntry> customLegend = new ArrayList<>();
            customLegend.add(new LegendEntry("Positive", legend.getForm(), legend.getFormSize(), legend.getFormLineWidth(), legend.getFormLineDashEffect(), PERSONAL_COLOR_TEMPLATE[1]));
            customLegend.add(new LegendEntry("Negative", legend.getForm(), legend.getFormSize(), legend.getFormLineWidth(), legend.getFormLineDashEffect(), PERSONAL_COLOR_TEMPLATE[0]));
            legend.setCustom(customLegend);


            PieDataSet dataSet = new PieDataSet(yValues, "Reviews");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setValueTextSize(11f);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextColor(Color.WHITE);

            pieChart.setData(data);
            pieChart.highlightValues(null);
            pieChart.invalidate();
        } else if (isReviewOrRating == 1) {
            ArrayList<PieEntry> yValues = new ArrayList<>();
            int v1 = eachRatingCount[0];
            int v2 = eachRatingCount[1];
            int v3 = eachRatingCount[2];
            int v4 = eachRatingCount[3];
            int v5 = eachRatingCount[4];


            if (v1 != 0) {
                yValues.add(new PieEntry(v1, "Rating 1"));
            }
            if (v2 != 0) {
                yValues.add(new PieEntry(v2, "Rating 2"));
            }
            if (v3 != 0) {
                yValues.add(new PieEntry(v3, "Rating 3"));
            }
            if (v4 != 0) {
                yValues.add(new PieEntry(v4, "Rating 4"));
            }
            if (v5 != 0) {
                yValues.add(new PieEntry(v5, "Rating 5"));
            }

            Description chartDescription = new Description();
            chartDescription.setText("Ratings for " + mHotel.getHotelName());
            chartDescription.setTextSize(15);
            pieChart.setDescription(chartDescription);

            pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);


            Legend legend = pieChart.getLegend();
            legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            legend.setXEntrySpace(7);
            legend.setYEntrySpace(5);

            // Trebuie sa fac custom legend ca sa nu se suprapuna pe chart valorile 0
            List<LegendEntry> customLegend = new ArrayList<>();
            customLegend.add(new LegendEntry("Rating 1", legend.getForm(), legend.getFormSize(), legend.getFormLineWidth(), legend.getFormLineDashEffect(), ColorTemplate.COLORFUL_COLORS[0]));
            customLegend.add(new LegendEntry("Rating 2", legend.getForm(), legend.getFormSize(), legend.getFormLineWidth(), legend.getFormLineDashEffect(), ColorTemplate.COLORFUL_COLORS[1]));
            customLegend.add(new LegendEntry("Rating 3", legend.getForm(), legend.getFormSize(), legend.getFormLineWidth(), legend.getFormLineDashEffect(), ColorTemplate.COLORFUL_COLORS[2]));
            customLegend.add(new LegendEntry("Rating 4", legend.getForm(), legend.getFormSize(), legend.getFormLineWidth(), legend.getFormLineDashEffect(), ColorTemplate.COLORFUL_COLORS[3]));
            customLegend.add(new LegendEntry("Rating 5", legend.getForm(), legend.getFormSize(), legend.getFormLineWidth(), legend.getFormLineDashEffect(), ColorTemplate.COLORFUL_COLORS[4]));
            legend.setCustom(customLegend);

            PieDataSet dataSet = new PieDataSet(yValues, "Ratings");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            PieData data = new PieData(dataSet);
            data.setValueTextSize(11f);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextColor(Color.WHITE);

            pieChart.setData(data);
            pieChart.highlightValues(null);
            pieChart.invalidate();

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
