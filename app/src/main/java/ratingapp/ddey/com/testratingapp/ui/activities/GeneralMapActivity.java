package ratingapp.ddey.com.testratingapp.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.utils.adapters.CustomInfoWindowAdapter;
import ratingapp.ddey.com.testratingapp.utils.adapters.PlaceAutocompleteAdapter;
import ratingapp.ddey.com.testratingapp.models.GooglePlace;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;


public class GeneralMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GeneralMapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1004;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71,136));
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int PROXIMITY_RADIUS = 10000;

    private AutoCompleteTextView acTvSearch;
    private ImageView ivGps;
    private ImageView ivInfo;
    private ImageView ivPlacePicker;

    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private GooglePlace mGooglePlace;
    private Marker mMarker;
    private double mCurrentLatitude, mCurrentLongitude;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false); //dezactivam buttoinu din dr sus
            init(); // daca permissions sunt granted atunci pun search bracket!
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyConnection();
        setContentView(R.layout.activity_general_map);
        this.setTitle("Maps");
        acTvSearch = findViewById(R.id.et_search);
        acTvSearch.setSingleLine();
        ivGps = findViewById(R.id.ic_gps);
        ivInfo = findViewById(R.id.ic_place_info);
        ivPlacePicker = findViewById(R.id.ic_picker);
        getLocationPermission();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        //creare mGoogleApiClient pentru autoComplete + setOnEditor e pentru ENTER -> geoLocate() pt gasirea locatiei
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        acTvSearch.setOnItemClickListener(autoClickListener);
        mAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        acTvSearch.setAdapter(mAutocompleteAdapter);
        acTvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });

        //intoarcere la pozitia initiala
        ivGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: click gps icon");
                getDeviceLocation();
            }
        });

        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: click info icon");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        mMarker.showInfoWindow();
                        Log.d(TAG, "onClick: place info " + mGooglePlace.toString());
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "Pick a place to show its information!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: NullPointerException " + e.getMessage());
                }
            }
        });


        //ca sa dau pop up, dupa am nevoie de onActivityResult pt ca folosesc startActivityForResult
        ivPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(GeneralMapActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
            }
        });

        hideSoftKeyboard();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (data != null) {
                Place place = PlacePicker.getPlace(this, data);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(updatePlaceDetailsCallback);
            }
        }
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        //geocoder transforma din adresa in latLng (intr-un obiect address care are lat, long, adresa -> preia in list<address> si doar primul element e cel gasit
        String searchString = acTvSearch.getText().toString();
        Geocoder geocoder = new Geocoder(GeneralMapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "geoLocation: IOException");
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location" + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location!");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();


                            if (currentLocation != null) {
                                mCurrentLatitude = currentLocation.getLatitude();
                                mCurrentLongitude = currentLocation.getLongitude();
                                moveCamera(new LatLng(mCurrentLatitude, mCurrentLongitude), DEFAULT_ZOOM, "My location");
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null!");
                            Toast.makeText(getApplicationContext(), "Unable to get current location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    //mut camera si pun mMarker doar in cazul in care nu este locatia mea
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat " + latLng.latitude + ", lng " + latLng.longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My location")) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mGoogleMap.addMarker(markerOptions);
        }
        hideSoftKeyboard();
    }

    //override la moveCamera() cu alt parametru (GooglePlace pt mMarker/info)
    private void moveCamera(LatLng latLng, float zoom, GooglePlace googlePlace) {
        Log.d(TAG, "moveCamera: moving the camera to: lat " + latLng.latitude + ", lng " + latLng.longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mGoogleMap.clear();

        mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(GeneralMapActivity.this));

        if (googlePlace != null) {
            try {
                //salvez in mMarker informatiile legate de locatie
                String tempStr = "";
                if (googlePlace.getAddress() != null)
                    tempStr +=  "Address: " + googlePlace.getAddress() + "\n";
                if (googlePlace.getPhoneNumber() != null && !googlePlace.getPhoneNumber().equals(""))
                    tempStr += "Phone Number: " + googlePlace.getPhoneNumber() + "\n";
                if (googlePlace.getWebsiteUri() != null)
                    tempStr += "Website: " + googlePlace.getWebsiteUri() + "\n";
                if (googlePlace.getRating() != -1)
                    tempStr += "Price Rating (1-5): " + googlePlace.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(googlePlace.getName())
                        .snippet(tempStr);
                mMarker = mGoogleMap.addMarker(options);

            } catch (NullPointerException e) {
                Log.d(TAG, "moveCamera: NullPointerException " + e.getMessage());
            }
        } else {
            //tot adaug mMarker dar fara alte informatii in cazul in care mGooglePlace este null
            mGoogleMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideSoftKeyboard();
    }

    //initializez fragmentul SupportMapFragment, si apelez async callbacku onMapReady
    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(GeneralMapActivity.this);
        }
    }

    //verific daca am primit permisiunea pentru a accessa google locations
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}; //explicitly check permissions

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initializeMap();
            }
            else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initializeMap();
                }
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(acTvSearch.getWindowToken(), 0);
    }

    //extragere location object (1)
    private AdapterView.OnItemClickListener autoClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(updatePlaceDetailsCallback);
        }
    };

    //extragere location object (2)
    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            //verificam daca nu a putut lua locatia
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
            }

            final Place place = places.get(0);

            //cream obiectul mGooglePlace pt a nu pierde referinta la place in momentul in care dezaloc mai jos cu places.release()
            try {
                mGooglePlace = new GooglePlace(place.getName().toString(), place.getAddress().toString(), place.getPhoneNumber().toString(),
                        place.getId(), place.getWebsiteUri(), place.getLatLng(), place.getRating(), null);
                Log.d(TAG, "onResult: place details: " + mGooglePlace.toString());
            } catch (NullPointerException e) {
                Log.d(TAG, "onResult: NullPointerException " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mGooglePlace);

            places.release();
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }


    public void verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(getApplicationContext());
        boolean isConnected = connection.isOnline();
        if (isConnected) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("No internet connection found")
                    .setMessage("You need to have mobile data or WiFi connection to access this. Press OK to return.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.create().show();
        }
    }

}
