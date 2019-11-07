package ratingapp.ddey.com.testratingapp.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Filter;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.adapters.HotelAdapter;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;


public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private RecyclerView rvHotels;
    private ProgressBar searchProgress;
    private EditText etSearch;
    private FloatingActionButton btnSearch;
    private List<Hotel> hotelsList = new ArrayList<>();
    private Filter receivedSearchFilter;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private HotelAdapter mAdapter;
    private FirebaseHelper mFirebaseHelper;
    private boolean isResume = false;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initializeComponents();
    }

    private void incomingIntent() {
        if (getIntent().hasExtra(Constants.SEARCH_KEY)) {
            Intent intent = getIntent();
            if (intent != null) {
                receivedSearchFilter = (Filter) intent.getSerializableExtra(Constants.SEARCH_KEY);

                if (!checkIfFilterExists(receivedSearchFilter)) {
                    Toast.makeText(getApplicationContext(), "No filters added!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "incIntent: " + receivedSearchFilter.toString());
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Error passing the filters", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeComponents() {
        mActivity = this;
        this.setTitle("Hotels");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!verifyConnection()) {
            displayNoInternetDialog();
        }
        incomingIntent();

        searchProgress = findViewById(R.id.search_progress_bar);
        rvHotels = findViewById(R.id.rv_search_hotels);
        rvHotels.setHasFixedSize(true);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();

        etSearch = findViewById(R.id.search_edittext_text);
        btnSearch = findViewById(R.id.search_ib_search);
        btnSearch.setOnClickListener(searchListEvent());
        loadData();

    }


    @NonNull
    private View.OnClickListener searchListEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyConnection()) {
                    if (etSearch.getText() != null && !etSearch.getText().toString().trim().isEmpty()) {
                        String searchText = etSearch.getText().toString();
                        searchProgress.setVisibility(View.VISIBLE);
                        hotelsList.clear();
                        mFirebaseHelper.getHotelsReference()
                                .orderByChild("hotelName")
                                .startAt(searchText)
                                .endAt(searchText + "\uf8ff")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            Hotel h = data.getValue(Hotel.class);
                                            if (h != null) {
                                                if (checkIfFilterExists(receivedSearchFilter)) {
                                                    if (isHotelMatchingFilter(receivedSearchFilter, h)) {
                                                        hotelsList.add(h);
                                                    }
                                                } else {
                                                    hotelsList.add(h);
                                                }
                                            }
                                        }
                                        initializeHotelRecyclerView();
                                        if (hotelsList.isEmpty())
                                            Toast.makeText(getApplicationContext(), "No hotel found! Change your filters!", Toast.LENGTH_SHORT).show();
                                        searchProgress.setVisibility(View.GONE);
                                        isResume = false;
                                        hideKeyboard(mActivity);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("ERROR", " " + databaseError.getMessage());
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Insert something into the search bracket before searching", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    displayNoInternetDialog();
                }
            }
        };
    }

    private void loadData() {
        searchProgress.setVisibility(View.VISIBLE);
        hotelsList.clear();
        mFirebaseHelper.getHotelsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Hotel h = data.getValue(Hotel.class);
                    if (h != null) {
                        if (checkIfFilterExists(receivedSearchFilter)) {
                            if (isHotelMatchingFilter(receivedSearchFilter, h)) {
                                hotelsList.add(h);
                            }
                        } else {
                            hotelsList.add(h);
                        }
                    }
                }
                initializeHotelRecyclerView();
                if (hotelsList.isEmpty())
                    Toast.makeText(getApplicationContext(), "No hotel found! Change your filters!", Toast.LENGTH_SHORT).show();
                searchProgress.setVisibility(View.GONE);
                isResume = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", " " + databaseError.getMessage());
            }
        });
    }

    private boolean isHotelMatchingFilter(Filter filter, Hotel hotel) {
        if (hotel.getDistanceFromCenter() > filter.getDistanceFromCityCenter() && filter.getDistanceFromCityCenter() != -1)
            return false;
        if (hotel.getPrice() > filter.getPricePerNight() && filter.getPricePerNight() != -1)
            return false;
        if (hotel.getRating() < filter.getRating() && filter.getRating() != 0)
            return false;
        if (!hotel.getCity().equals(filter.getCity()) && filter.getCity() != null)
            return false;
        return doHotelStarsMatch(filter, hotel);
    }

    private boolean checkIfFilterExists(Filter f) {
        if (f != null) {
            if (f.getCity() != null) {
                return true;
            } else if (f.getHotelStars() != null) {
                return true;
            } else if (f.getRating() != -1) {
                return true;
            } else if (f.getDistanceFromCityCenter() != -1) {
                return true;
            } else return f.getPricePerNight() != -1;
        } else {
            return false;
        }
    }


    private boolean doHotelStarsMatch(Filter filter, Hotel hotel) {
        if (filter.getHotelStars() != null) {
            for (Integer nrStars : filter.getHotelStars()) {
                if (hotel.getStars() == nrStars) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }

    }

    private void initializeHotelRecyclerView() {
        if (!isResume) {
            expandState.clear();
            for (int i = 0; i < hotelsList.size(); i++) {
                expandState.append(i, false);
            }
        }

        rvHotels.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new HotelAdapter(this, hotelsList, this, expandState);
        rvHotels.setAdapter(mAdapter);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (verifyConnection()) {
            isResume = true;
            loadData();
        } else {
            displayNoInternetDialog();
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
