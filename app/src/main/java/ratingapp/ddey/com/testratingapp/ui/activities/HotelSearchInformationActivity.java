package ratingapp.ddey.com.testratingapp.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.adapters.PagerAdapter;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;

public class HotelSearchInformationActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private Bundle mBundle;
    private Hotel mHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_search_information);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        incomingIntent();
        initializeComponents();

    }

    private void incomingIntent() {
        if (getIntent().hasExtra(Constants.VIEW_REVIEWS_RATINGS_KEY)) {
            Intent recvIntent = getIntent();

            if (recvIntent != null) {
                mHotel = (Hotel) recvIntent.getSerializableExtra(Constants.VIEW_REVIEWS_RATINGS_KEY);
                if (mHotel != null)
                    this.setTitle(mHotel.getHotelName());
            }
        }
    }

    public void initializeComponents() {
        tabLayout = findViewById(R.id.tabLayout_myreviewsratings);
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));
        tabLayout.addTab(tabLayout.newTab().setText("Ratings"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = findViewById(R.id.viewPager_myreviewsratings);

        mBundle = new Bundle();
        mBundle.putSerializable(Constants.BUNDLE_HOTEL_KEY, mHotel);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), mBundle);

        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

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
}
