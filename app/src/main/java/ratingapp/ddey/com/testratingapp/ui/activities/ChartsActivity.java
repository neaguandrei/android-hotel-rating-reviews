package ratingapp.ddey.com.testratingapp.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.models.ChartFilter;
import ratingapp.ddey.com.testratingapp.models.Rating;
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.ChartValueFormatter;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;



public class ChartsActivity extends AppCompatActivity {
    private static final String TAG = "ChartsActivity";
    private static final int[] PERSONAL_COLOR_TEMPLATE = {
            Color.rgb(139, 0, 0), Color.rgb(0, 100, 0)
    };
    private LineChart mLineChart;
    private BarChart mBarChart;
    private TextView tvDescription;
    private Spinner spnChartName;

    private Dialog mDialog;
    private ChartFilter mFilter;

    private ProgressBar pbProgress;
    private List<Hotel> mHotelList;
    private FirebaseHelper mFirebaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        setTitle("Charts");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeComponents();
    }

    private void initializeComponents() {
        tvDescription = findViewById(R.id.tv_chart_description);
        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();
        mHotelList = new ArrayList<>();
        pbProgress = findViewById(R.id.statistics_progressbar);
        mLineChart = findViewById(R.id.statistics_line_chart);
        mBarChart = findViewById(R.id.statistics_bar_chart);
        FloatingActionButton fabGenerateChart = findViewById(R.id.statistics_fab_editchart);
        fabGenerateChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildInputDialog();
            }
        });

        loadData();
        mFilter = new ChartFilter();
    }

    private void generateLineChartNumberReviewHotelStars() {
        mLineChart.invalidate();
        mLineChart.clear();
        tvDescription.setText("");
        mLineChart.setVisibility(View.VISIBLE);
        // Nr
        int nrTotalReviews = 0;
        int nrPositive = 0;
        List<Review> mReviewsList = new ArrayList<>();

        int[] nrPositiveByStars = new int[5];
        int[] nrNegativeByStars = new int[5];
        for (Hotel h : mHotelList) {
            if (h.getReviewsList() != null) {
                List<Review> valuesList = new ArrayList<>(h.getReviewsList().values());
                for (Review r : valuesList) {
                    if (r.isPositive()) {
                        nrPositive++;
                        nrPositiveByStars[h.getStars() - 1]++;
                    } else {
                        nrNegativeByStars[h.getStars() - 1]++;
                    }
                    nrTotalReviews++;
                    mReviewsList.add(r);
                }
            }
        }
        ArrayList<Entry> yVals1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int nrStars = i + 1;
            yVals1.add(new Entry(nrStars, nrPositiveByStars[i]));
        }

        ArrayList<Entry> yVals2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int nrStars = i + 1;
            yVals2.add(new Entry(nrStars, nrNegativeByStars[i]));
        }

        LineDataSet set1, set2;
        set1 = new LineDataSet(yVals1, "Total positive reviews");
        set1.setColor(PERSONAL_COLOR_TEMPLATE[1]);
        set1.setDrawCircles(true);
        set1.setLineWidth(3f);
        set2 = new LineDataSet(yVals2, "Total negative reviews");
        set2.setColor(PERSONAL_COLOR_TEMPLATE[0]);
        set2.setDrawCircles(true);
        set2.setLineWidth(3f);

        mLineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value)) + " stars";
            }
        });

        // Ca sa nu mai exista duplicates si sa fie cum trebuie punctate intersectiile.
        mLineChart.getXAxis().setGranularityEnabled(true);
        //Scot axa cu valori din dreapta
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        mLineChart.getAxisLeft().setGranularityEnabled(true);
        mLineChart.setDescription(null);

        LineData data = new LineData(set1, set2);
        data.setValueFormatter(new ChartValueFormatter());
        mLineChart.setData(data);
        mLineChart.animateX(1000);


        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setGranularityEnabled(true);

        tvDescription.setText("Horizontal represents hotel stars \nVertical represents reviews count");
    }

    private void generateBarChartNumberRatingsHotelStars() {
        tvDescription.setText("");
        mBarChart.setVisibility(View.VISIBLE);
        //nr1 -> nr 5 sunt vectori ce retin ratingul de 1 pt fiecare nr de stele in parte, ratingul 2 pt fiecare nr de stele in parte
        int[] nr1 = new int[5];
        int[] nr2 = new int[5];
        int[] nr3 = new int[5];
        int[] nr4 = new int[5];
        int[] nr5 = new int[5];

        for (Hotel h : mHotelList) {
            if (h.getRatingsList() != null) {
                List<Rating> valuesList = new ArrayList<>(h.getRatingsList().values());
                for (Rating r : valuesList) {
                    if (r.getRateValue() == 1) {
                        nr1[h.getStars() - 1]++;
                    } else if (r.getRateValue() == 2) {
                        nr2[h.getStars() - 1]++;
                    } else if (r.getRateValue() == 3) {
                        nr3[h.getStars() - 1]++;
                    } else if (r.getRateValue() == 4) {
                        nr4[h.getStars() - 1]++;
                    } else if (r.getRateValue() == 5) {
                        nr5[h.getStars() - 1]++;
                    }
                }
            }
        }

        mBarChart.animateXY(3000, 3000);

        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int nrStars = i + 1;
            barEntries1.add(new BarEntry(nrStars, nr1[i]));
        }

        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int nrStars = i + 1;
            barEntries2.add(new BarEntry(nrStars, nr2[i]));
        }

        ArrayList<BarEntry> barEntries3 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int nrStars = i + 1;
            barEntries3.add(new BarEntry(nrStars, nr3[i]));
        }

        ArrayList<BarEntry> barEntries4 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int nrStars = i + 1;
            barEntries4.add(new BarEntry(nrStars, nr4[i]));
        }

        ArrayList<BarEntry> barEntries5 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int nrStars = i + 1;
            barEntries5.add(new BarEntry(nrStars, nr5[i]));
        }


        BarDataSet barDataSet1 = new BarDataSet(barEntries1, "Rating 1");
        barDataSet1.setColor(Color.LTGRAY);

        BarDataSet barDataSet2 = new BarDataSet(barEntries2, "Rating 2");
        barDataSet2.setColor(Color.GREEN);

        BarDataSet barDataSet3 = new BarDataSet(barEntries3, "Rating 3");
        barDataSet3.setColor(Color.RED);

        BarDataSet barDataSet4 = new BarDataSet(barEntries4, "Rating 4");
        barDataSet4.setColor(Color.DKGRAY);

        BarDataSet barDataSet5 = new BarDataSet(barEntries5, "Rating 5");
        barDataSet5.setColor(Color.BLUE);

        BarData data = new BarData(barDataSet1, barDataSet2, barDataSet3, barDataSet4, barDataSet5);
        data.setValueFormatter(new ChartValueFormatter());
        mBarChart.setData(data);

        String[] hotelStars = new String[]{"One star", "Two stars", "Three stars", "Four stars", "Five stars"};
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(hotelStars));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        mBarChart.setDragEnabled(true);
        mBarChart.setVisibleXRangeMaximum(3);

        // Trebuie aplicata formula (barwidth + barspace) * number of bars + groupSpace = 1 altfel sunt decalate
        float barSpace = 0.05f;
        float groupSpace = 0.35f;
        int groupCount = 5;

        data.setBarWidth(0.08f);
        mBarChart.getXAxis().setAxisMinimum(0);
        mBarChart.getXAxis().setAxisMaximum(0 + mBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        mBarChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping

        mBarChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        mBarChart.setDescription(null);
        mBarChart.getAxisRight().setEnabled(false);

        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setGranularityEnabled(true);

        mBarChart.invalidate();
        tvDescription.setText("Horizontal represents hotel stars \nVertical represents ratings count");
    }


    private void buildInputDialog() {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.custom_dialog_edit_chart);
        mDialog.setTitle("Generate chart");


        spnChartName = mDialog.findViewById(R.id.statistics_spn_choosechart);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.view_chart_array, R.layout.support_simple_spinner_dropdown_item);
        spnChartName.setAdapter(adapter);

        if (mFilter != null) {
            selectSpinnerChoice(mFilter.getChartName(), spnChartName);
        }

        Button btnOk = mDialog.findViewById(R.id.statistics_btn_add);
        Button btnCancel = mDialog.findViewById(R.id.statistics_btn_cancel);

        btnOk.setEnabled(true);
        btnCancel.setEnabled(true);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyConnection()) {
                    mFilter.setChartName(spnChartName.getSelectedItem().toString());
                    mBarChart.setVisibility(View.INVISIBLE);
                    mLineChart.setVisibility(View.INVISIBLE);
                    if (mFilter.getChartName().equals("Reviews related bar chart")) {
                    } else if (mFilter.getChartName().equals("Ratings related bar chart")) {
                        generateBarChartNumberRatingsHotelStars();

                    } else if (mFilter.getChartName().equals("Reviews related line chart")) {
                        generateLineChartNumberReviewHotelStars();
                    } else {
                        Toast.makeText(getApplicationContext(), "Choice invalid! Error", Toast.LENGTH_SHORT).show();
                    }
                    mDialog.cancel();
                } else {
                    displayNoInternetDialog();
                }
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

    private void selectSpinnerChoice(String selection, Spinner currentSpinner) {
        Adapter adapter = currentSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(selection)) {
                currentSpinner.setSelection(i);
                break;
            }
        }
    }


    private void loadData() {
        pbProgress.setVisibility(View.VISIBLE);
        mHotelList.clear();
        mFirebaseHelper.getHotelsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Hotel h = data.getValue(Hotel.class);
                    if (h != null) {
                        mHotelList.add(h);
                    }
                }
                if (mHotelList.isEmpty())
                    Toast.makeText(getApplicationContext(), "No hotel found! Change your filters!", Toast.LENGTH_SHORT).show();
                pbProgress.setVisibility(View.GONE);
                buildInputDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", " " + databaseError.getMessage());
            }
        });
    }


    //Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(this);
        return connection.isOnline();
    }

    private void displayNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("No internet connection found")
                .setMessage("You need to have mobile data or WiFi connection to access this. Press OK to return or go to Wi-Fi settings")
                .setPositiveButton("Internet settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.create().show();
    }
}
