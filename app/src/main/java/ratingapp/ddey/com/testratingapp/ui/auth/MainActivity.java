package ratingapp.ddey.com.testratingapp.ui.auth;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import ratingapp.ddey.com.testratingapp.ui.fragments.FavouritesFragment;
import ratingapp.ddey.com.testratingapp.ui.fragments.HomeFragment;
import ratingapp.ddey.com.testratingapp.ui.fragments.ProfileFragment;
import ratingapp.ddey.com.testratingapp.ui.fragments.SearchFragment;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;
import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.utils.others.Session;

public class MainActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private FavouritesFragment favouritesFragment;
    private SearchFragment searchFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        homeFragment = new HomeFragment();
        favouritesFragment = new FavouritesFragment();
        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();

        Session session = new Session(this);
        if (getIntent().hasExtra("rememberMe")) {
            boolean isUserRemember = getIntent().getBooleanExtra("rememberMe", true);

            if (isUserRemember) {
                if (session.checkIfUserNeedsToBeLoggedOut())
                    finish();
            }
        }

        ConnectionStatus connection = ConnectionStatus.getInstance(getApplicationContext());
        if (connection.isOnline()) {
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection available!", Toast.LENGTH_SHORT).show();
        }

        setFragment(homeFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    setFragment(homeFragment);
                    return true;
                case R.id.nav_rated:
                    forceFragmentRefresh(favouritesFragment);
                    setFragment(favouritesFragment);
                    return true;
                case R.id.nav_search:
                    setFragment(searchFragment);
                    return true;
                case R.id.nav_profile:
                    forceFragmentRefresh(profileFragment);
                    setFragment(profileFragment);
                    return true;
                default:
                    return false;
            }

        }
    };

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void forceFragmentRefresh(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }
}
