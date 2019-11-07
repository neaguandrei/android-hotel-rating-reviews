package ratingapp.ddey.com.testratingapp.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.GoogleHotel;
import ratingapp.ddey.com.testratingapp.utils.adapters.GoogleHotelAdapter;
import ratingapp.ddey.com.testratingapp.models.GoogleReview;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseConstants;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Session;

public class FavouritesFragment extends Fragment {
    private DatabaseHelper mDbHelper;
    private GoogleHotelAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Session mSession;
    private FirebaseHelper mFirebaseHelper;

    private String currentUserToken;
    private long currentTag;
    private List<GoogleHotel> tempListFirebase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Favourite hotels");
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        mSession = new Session(getActivity());
        mDbHelper = new DatabaseHelper(getActivity());
        mFirebaseHelper = FirebaseHelper.getInstance();
        currentUserToken = mDbHelper.getUserToken(mSession);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mDb = mDbHelper.getWritableDatabase();
        mAdapter = new GoogleHotelAdapter(getActivity(), getAllItems());
        recyclerView.setAdapter(mAdapter);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                currentTag = (long) viewHolder.itemView.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete this hotel?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        mAdapter.notifyDataSetChanged();

        syncFirebaseWithSQLite();
        return view;
    }

    public DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    removeItem(currentTag);
                    mAdapter.notifyDataSetChanged();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private Cursor getAllItems() {
        return mDb.query(DatabaseConstants.GOOGLE_HOTEL_TABLE, null, DatabaseConstants.COLUMN_USER_TOKEN + " = ?", new String[]{currentUserToken}, null, null, null);
    }

    private void removeItem(long id) {
        mDb.close();
        String hotelToken = mDbHelper.getHotelToken(id);

        if (verifyConnection()) {
            removeHotelFirebase(hotelToken);
            mDb = mDbHelper.getWritableDatabase();
            mDb.delete(DatabaseConstants.GOOGLE_HOTEL_TABLE, DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN + " = ?", new String[]{hotelToken});
            mDb.delete(DatabaseConstants.GOOGLE_REVIEW_TABLE, DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN + " = ?", new String[]{hotelToken});
            mAdapter.swapCursor(getAllItems());
        } else {
            Toast.makeText(getActivity(), "You can't remove hotels without an internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeHotelFirebase(final String hotelToken) {
        if (hotelToken == null || hotelToken.trim().isEmpty()) {
        } else {
            mFirebaseHelper.openConnection();
            mFirebaseHelper.getGooglePlacesHotelsReference().child(hotelToken).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity(), "Hotel successfully deleted", Toast.LENGTH_SHORT).show();
                    mFirebaseHelper.getGooglePlacesReviewsReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                GoogleReview review = data.getValue(GoogleReview.class);
                                if (review != null && review.getHotelToken().equals(hotelToken)) {
                                    mFirebaseHelper.getGooglePlacesReviewsReference().child(review.getReviewToken()).removeValue();
                                    mFirebaseHelper.getGooglePlacesReviewsReference().child(review.getReviewToken()).removeEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("FirebaseDelete: ", "Removing is working");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Review sync error: ", databaseError.toString());
                        }
                    });
                }
            });

        }
    }

    // Metode de sincronizare Firebase / SQLite
    public void syncFirebaseWithSQLite() {
        tempListFirebase = new ArrayList<>();
        mFirebaseHelper.openConnection();
        mFirebaseHelper.getGooglePlacesHotelsReference().orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final GoogleHotel googleHotel = data.getValue(GoogleHotel.class);

                    tempListFirebase.add(googleHotel);
                    if (googleHotel != null && googleHotel.getUserToken().equals(mDbHelper.getUserToken(mSession))) {
                        //Verific daca exista hotelul in baza de date locala si in cazul in care nu exista il introduc. Fac acest lucru pentru cazul in care este adaugat un nou hotel de pe alt device si doresc sa am si pe acest device acest lucru.
                        if (!mDbHelper.isGoogleHotelInDb(googleHotel.getHotelToken())) {
                            mDbHelper.insertHotels(googleHotel, mDbHelper.getUserToken(mSession));
                            Log.d("GoogleHotel sync: ", googleHotel.toString());
                            mFirebaseHelper.getGooglePlacesReviewsReference().orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        GoogleReview review = data.getValue(GoogleReview.class);
                                        if (review != null && review.getHotelToken().equals(googleHotel.getHotelToken())) {
                                            mDbHelper.insertReview(review, googleHotel);
                                            Log.d("Review sync: ", review.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("Review sync error: ", databaseError.toString());
                                }
                            });
                        }

                    }
                }
                syncSQLiteWithFirebase();
                // Sectiune de cod ca in metoda removeItem() pentru a nu da eroare. Readuc in lista toate elementele din SQLite pentru refreshul din urma sincronizarii.
                mDb.close();
                mDb = mDbHelper.getWritableDatabase();
                mAdapter.swapCursor(getAllItems());
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GoogleHotel error: ", databaseError.toString());
            }
        });
    }

    // Sincronizare in cazul in care in firebase nu exista si in local da, ceea ce inseamna ca a fost sters in alta parte si local nu inca pe respectivul device
    public void syncSQLiteWithFirebase() {

        List<GoogleHotel> tempSQL = mDbHelper.checkLocalDb(mDbHelper.getUserToken(mSession));
        for (GoogleHotel gh : tempSQL) {
            boolean mustBeDeleted = true;
            for (GoogleHotel ghFB : tempListFirebase) {
                if (ghFB.getHotelToken().equals(gh.getHotelToken())) {
                    mustBeDeleted = false;
                }
            }
            if (mustBeDeleted) {
                removeItem(gh.getId());
            }
        }
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(getActivity());
        boolean isConnected = connection.isOnline();
        return isConnected;
    }
}
