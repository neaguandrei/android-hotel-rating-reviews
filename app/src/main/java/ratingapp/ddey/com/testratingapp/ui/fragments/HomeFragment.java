package ratingapp.ddey.com.testratingapp.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.ui.activities.GeneralMapActivity;
import ratingapp.ddey.com.testratingapp.ui.activities.MyReviewsRatingsActivity;
import ratingapp.ddey.com.testratingapp.ui.activities.NearbyHotelsMapActivity;
import ratingapp.ddey.com.testratingapp.ui.activities.NotesActivity;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        v = inflater.inflate(R.layout.fragment_home, container, false);

        initializeComponents();

        return v;
    }

    public void initializeComponents() {
        CardView cvMaps = v.findViewById(R.id.cardview_maps);
        CardView cvNotes = v.findViewById(R.id.cardview_notes);
        CardView cvNearby = v.findViewById(R.id.cardview_nearbyplaces);
        CardView cvMyReviewsAndRatings = v.findViewById(R.id.cardview_myreviewsandratings);

        if (isServicesOK())
            cvMaps.setOnClickListener(this);
        cvNotes.setOnClickListener(this);
        cvNearby.setOnClickListener(this);
        cvMyReviewsAndRatings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.cardview_maps:
                if (verifyConnection()) {
                    intent = new Intent(v.getContext(), GeneralMapActivity.class);
                    startActivity(intent);
                } else {
                    displayNoInternetDialog();
                }
                break;
            case R.id.cardview_myreviewsandratings:
                if (verifyConnection()) {
                    intent = new Intent(v.getContext(), MyReviewsRatingsActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(v.getContext(), MyReviewsRatingsActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "Showing offline data", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.cardview_nearbyplaces:
                if (verifyConnection()) {
                    intent = new Intent(v.getContext(), NearbyHotelsMapActivity.class);
                    startActivity(intent);
                } else {
                    displayNoInternetDialog();
                }

                break;
            case R.id.cardview_notes:
                intent = new Intent(v.getContext(), NotesActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(v.getContext()); //

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(v.getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(getActivity());
        return connection.isOnline();
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
