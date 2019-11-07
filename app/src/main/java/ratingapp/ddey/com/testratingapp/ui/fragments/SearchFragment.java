package ratingapp.ddey.com.testratingapp.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.ui.activities.ChartsActivity;
import ratingapp.ddey.com.testratingapp.ui.activities.SearchActivity;
import ratingapp.ddey.com.testratingapp.models.Filter;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;
import ratingapp.ddey.com.testratingapp.utils.remote.HotelsParser;
import ratingapp.ddey.com.testratingapp.utils.remote.HttpHelper;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private TextView tvMaxPrice;
    private SeekBar sbMaxPrice;
    private Button btnShowResults;
    private Button btnShowCharts;
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private Spinner spnRating;
    private Spinner spnCity;
    private View view;
    private ProgressBar progressBar;

    private Filter filter;
    private float valDistanceSeekbar = 0.1f;
    private int valPriceSeekbar = 100;

    private final String hotelsJsonAPI = "https://api.myjson.com/bins/14cj0g";
    private HttpHelper mHttp;
    private List<Hotel> hotelList;
    private FirebaseHelper mFirebaseHelper;
    private TextView tvMaxDistance;
    private SeekBar sbMaxDistance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Search");
        view = inflater.inflate(R.layout.fragment_search, container, false);
        progressBar = view.findViewById(R.id.search_progress);

        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();
        checkIfHotelDbExists();
        initializeComponents();
        return view;
    }

    public void initializeComponents() {

        tvMaxDistance = view.findViewById(R.id.tv_distancefromcenter_number);
        sbMaxDistance = view.findViewById(R.id.search_sb_distancefromcenter);
        tvMaxPrice = view.findViewById(R.id.search_tv_price);
        sbMaxPrice = view.findViewById(R.id.search_seekbar);

        cb1 = view.findViewById(R.id.cb1);
        cb2 = view.findViewById(R.id.cb2);
        cb3 = view.findViewById(R.id.cb3);
        cb4 = view.findViewById(R.id.cb4);
        cb5 = view.findViewById(R.id.cb5);
        spnRating = view.findViewById(R.id.search_spn_rating);
        spnCity = view.findViewById(R.id.search_spn_country);
        btnShowResults = view.findViewById(R.id.search_btn_results);
        btnShowCharts = view.findViewById(R.id.search_btn_charts);

        float stepDistance = 0.1f;
        float maxDistance = 10.0f;
        float minDistance = 0.1f;
        sbMaxDistance.setMax((int)((maxDistance - minDistance) / stepDistance));
        sbMaxDistance.setOnSeekBarChangeListener(sbDistanceChangeEvent());

        int step = 1;
        int max = 800;
        int min = 100;
        sbMaxPrice.setMax((max - min) / step);
        sbMaxPrice.setOnSeekBarChangeListener(sbPriceChangeEvent());

        btnShowResults.setOnClickListener(showResults());
        btnShowCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyConnection()) {
                    Intent intent = new Intent(getActivity(), ChartsActivity.class);
                    startActivity(intent);
                } else {
                    displayNoInternetDialog();
                }

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.arrays_rating, R.layout.support_simple_spinner_dropdown_item);
        spnRating.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(view.getContext(), R.array.cities_array, R.layout.support_simple_spinner_dropdown_item);
        spnCity.setAdapter(adapter2);

        spnRating.setSelected(false);
        spnCity.setSelected(false);


    }

    private View.OnClickListener showResults() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyConnection()) {
                    filter = new Filter();
                    if (isValid()) {
                        if (valPriceSeekbar > 100)
                            filter.setPricePerNight(valPriceSeekbar);
                        else
                            filter.setPricePerNight(-1);
                        if (valDistanceSeekbar > 0.1f)
                            filter.setDistanceFromCityCenter(valDistanceSeekbar);
                        else
                            filter.setDistanceFromCityCenter(-1);
                        addHotelStars();
                        if (spnRating.getSelectedItemPosition() != 0)
                            filter.setRating(Float.parseFloat(spnRating.getSelectedItem().toString()));
                        else
                            filter.setRating(0);

                        if (spnCity.getSelectedItemPosition() != 0)
                            filter.setCity(spnCity.getSelectedItem().toString());

                        Intent intent = new Intent(view.getContext(), SearchActivity.class);
                        intent.putExtra(Constants.SEARCH_KEY, filter);
                        startActivity(intent);
                    } else
                        Toast.makeText(view.getContext(), "Something's wrong with the filters! Error!", Toast.LENGTH_SHORT).show();
                } else {
                    displayNoInternetDialog();
                }
            }
        };
    }

    private void addHotelStars() {
        List<Integer> starsList = new ArrayList<>();
        if (cb1.isChecked()) {
            starsList.add(1);
        }
        if (cb2.isChecked()) {
            starsList.add(2);
        }
        if (cb3.isChecked()) {
            starsList.add(3);
        }
        if (cb4.isChecked()) {
            starsList.add(4);
        }
        if (cb5.isChecked()) {
            starsList.add(5);
        }
        if (!cb1.isChecked() && !cb2.isChecked() && !cb3.isChecked() && !cb4.isChecked() && !cb5.isChecked()) {
            starsList = null;
        }
        filter.setHotelStars(starsList);
    }


    private SeekBar.OnSeekBarChangeListener sbDistanceChangeEvent() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valDistanceSeekbar = (float)(0.1 + (progress * 0.1));
                if (valDistanceSeekbar != 0.1f)  {
                    String maxDistanceString = "Maximum distance " + String.valueOf(valDistanceSeekbar) + " km";
                    tvMaxDistance.setText(maxDistanceString);
                } else {
                    tvMaxDistance.setText("Maximum distance is no longer set");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener sbPriceChangeEvent() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valPriceSeekbar = 100 + (progress * 1);
                if (valPriceSeekbar != 100)
                    tvMaxPrice.setText("Maximum price " + String.valueOf(valPriceSeekbar) + " RON");
                else
                    tvMaxPrice.setText("Maximum price is no longer set");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }


    private void checkIfHotelDbExists() {
        if (verifyConnection()) {
            mFirebaseHelper.getHotelsReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        createFirebaseHotelsDatabase();
                        Log.i(TAG, "Hotel database doesn't exist. Starting upload.");
                    }
                    Log.i(TAG, "Hotel exists. Nothing to do.");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Hotel database check error");
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void createFirebaseHotelsDatabase() {
        if (verifyConnection()) {
            progressBar.setVisibility(View.VISIBLE);
            mFirebaseHelper = FirebaseHelper.getInstance();
            mFirebaseHelper.openConnection();
            mHttp = new HttpHelper() {
                @Override
                protected void onPostExecute(String s) {
                    try {
                        hotelList = new ArrayList<>();
                        hotelList = HotelsParser.getHotels(s);
                        if (hotelList.size() > 0) {
                            transferFromJsonApiToFirebase(hotelList);
                            Log.i(TAG, "Uploading to Firebase successfully");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            mHttp.execute(hotelsJsonAPI);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void transferFromJsonApiToFirebase(List<Hotel> list) {
        if (list != null) {
            for (Hotel h : list) {
                h.setToken(mFirebaseHelper.getHotelsReference().push().getKey());
                mFirebaseHelper.getHotelsReference().child(h.getToken()).setValue(h);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private boolean isValid() {
        if (valDistanceSeekbar < 0.1f)
            return false;
        if (valPriceSeekbar < 99) {
            return false;
        }
        return true;
    }

    public boolean verifyConnection() {
        if (getActivity() != null) {
            ConnectionStatus connection = ConnectionStatus.getInstance(getActivity());
            return connection.isOnline();
        }
        return false;
    }

    private void displayNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("No internet connection found")
                .setMessage("You need to have mobile data or WiFi connection to access this. Press OK to return or go to Wi-Fi settings")
                .setPositiveButton("Internet settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create().show();
    }
}
