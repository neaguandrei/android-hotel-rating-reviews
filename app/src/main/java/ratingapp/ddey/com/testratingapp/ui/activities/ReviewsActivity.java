package ratingapp.ddey.com.testratingapp.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.utils.adapters.GoogleReviewAdapter;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseConstants;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;

public class ReviewsActivity extends AppCompatActivity {
    private SQLiteDatabase mDb;

    private long hotelId;
    private String hotelToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        setBackButtonTitle();
        initializeComponents();
    }

    private void setBackButtonTitle() {
        setTitle("Reviews from Google");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeComponents() {


        DatabaseHelper mDbHelper = new DatabaseHelper(this);
        RecyclerView recyclerView = findViewById(R.id.review_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        hotelId = intent.getLongExtra("hotelToken", hotelId);
        hotelToken = mDbHelper.getHotelToken(hotelId);
        mDb = mDbHelper.getReadableDatabase();

        GoogleReviewAdapter mAdapter = new GoogleReviewAdapter(this, getAllItems());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    private Cursor getAllItems()
    {
        return mDb.query(DatabaseConstants.GOOGLE_REVIEW_TABLE, null,DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN + " = ?",new String[]{hotelToken},null,null,null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
