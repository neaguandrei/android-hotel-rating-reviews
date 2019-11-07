package ratingapp.ddey.com.testratingapp.ui.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.models.MyReviewsRatingsModel;
import ratingapp.ddey.com.testratingapp.models.Rating;
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.utils.adapters.MyReviewsRatingsAdapter;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Session;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;

public class MyReviewsRatingsActivity extends AppCompatActivity {

    private List<MyReviewsRatingsModel> mList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyReviewsRatingsAdapter mAdapter;

    private DatabaseHelper mDb;
    private Session mSession;
    private FirebaseHelper mFirebaseHelper;
    private String userToken;

    private Review tempReview;
    private Rating tempRating;

    private ProgressBar pbProgress;

    private boolean isSyncing = false;
    private List<MyReviewsRatingsModel> mSyncList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews_ratings);
        initializeComponents();
    }

    private void initializeComponents() {
        this.setTitle("My reviews and ratings");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = new DatabaseHelper(this);
        mSession = new Session(this);
        userToken = mDb.getUserToken(mSession);

        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();
        pbProgress = findViewById(R.id.my_progressbar);

        loadData();
    }

    private void loadData() {
        if (verifyConnection()) {
            // o fac true ca sa sincronizez eventual ce nu e in local database
            isSyncing = true;
            loadDataFromLocal();
            loadDataFromFirebase();
        } else {
            loadDataFromLocal();
            Toast.makeText(getApplicationContext(), "Showing offline data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataFromLocal() {
        // daca issyncing e false, inseamna ca loaduiesc offline. altfel vreau sa verific daca ce am in firebase si in local este la fel.
        if (!isSyncing) {
            mList.clear();
            mList = mDb.getMyReviewsRatingsModels(userToken);
            initRecyclerView();
        } else {
            mSyncList.clear();
            mSyncList = mDb.getMyReviewsRatingsModels(userToken);
        }

    }

    private void loadDataFromFirebase() {
        mList.clear();
        pbProgress.setVisibility(View.VISIBLE);
        mFirebaseHelper.getHotelsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    boolean doesItHaveReviewOrRating = false;
                    MyReviewsRatingsModel newModel = new MyReviewsRatingsModel();
                    Hotel h = data.getValue(Hotel.class);
                    if (h != null) {
                        if (h.getReviewsList() != null) {
                            List<Review> tempReviewList = new ArrayList<>(h.getReviewsList().values());
                            for (Review r : tempReviewList) {
                                if (r.getUserToken().equals(userToken)) {
                                    tempReview = r;
                                    doesItHaveReviewOrRating = true;
                                    break;
                                }
                            }
                        }
                        if (h.getRatingsList() != null) {
                            List<Rating> tempRatingsList = new ArrayList<>(h.getRatingsList().values());
                            for (Rating r : tempRatingsList) {
                                if (r.getUserToken().equals(userToken)) {
                                    doesItHaveReviewOrRating = true;
                                    tempRating = r;
                                    break;
                                }
                            }
                        }
                        if (doesItHaveReviewOrRating) {
                            if (tempReview != null || tempRating != null) {
                                newModel.setHotelName(h.getHotelName());
                                newModel.setCity(h.getCity());
                                if (tempReview != null) {
                                    newModel.setReviewTitle(tempReview.getTitle());
                                    newModel.setReviewDescription(tempReview.getDescription());
                                    newModel.setReviewPositive(tempReview.isPositive());
                                    newModel.setReviewToken(tempReview.getReviewToken());
                                }
                                if (tempRating != null) {
                                    newModel.setRatingValue(tempRating.getRateValue());
                                    newModel.setRatingToken(tempRating.getRatingToken());
                                }
                            }
                            mList.add(newModel);

                            if (isSyncing) {
                                // daca nu exista in lista preluata din baza de date locala merg mai departe cu inserarea. altel merg la urmatorul obiect
                                boolean existsAlready = false;
                                for (MyReviewsRatingsModel model : mSyncList) {
                                    if (newModel.getHotelName().equals(model.getHotelName())) {
                                        existsAlready = true;
                                    }
                                }

                                if (!existsAlready) {
                                    if (tempRating != null) {
                                        if (!mDb.isHotelInDb(tempRating.getHotelToken())){
                                            mDb.insertMyHotel(h, mDb.getUserToken(mSession));
                                        }
                                        mDb.insertMyRating(tempRating);
                                    }

                                    if (tempReview != null) {
                                        if (!mDb.isHotelInDb(tempReview.getHotelToken())) {
                                            mDb.insertMyHotel(h, mDb.getUserToken(mSession));
                                        }
                                        mDb.insertMyReview(tempReview);
                                    }
                                }
                            }
                        }
                        tempRating = null;
                        tempReview = null;
                    }

                }
                initRecyclerView();
                pbProgress.setVisibility(View.GONE);
                isSyncing = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.my_recycler);
        mAdapter = new MyReviewsRatingsAdapter(this, mList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(this);
        return connection.isOnline();
    }

}