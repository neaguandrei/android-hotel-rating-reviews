package ratingapp.ddey.com.testratingapp.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.ui.activities.AboutActivity;
import ratingapp.ddey.com.testratingapp.ui.activities.ViewProfileActivity;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.utils.adapters.EditProfileAdapter;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Session;


public class ProfileFragment extends Fragment {
    private DatabaseHelper mDb;
    private Session mSession;

    private ListView listView;
    private ArrayList<Integer> listIds;
    private ArrayList<String> listTitles;

    private User user;

    @SuppressLint("CommitPrefEdits")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Profile");

        mSession = new Session(getActivity());
        mDb = new DatabaseHelper(getActivity());

        user = mDb.retrieveUser(mSession);

        listView = view.findViewById(R.id.listView);
        listIds = getListIds();
        listTitles = getListTitles();
        EditProfileAdapter editProfileAdapter = new EditProfileAdapter(getActivity(), listIds, listTitles);
        listView.setAdapter(editProfileAdapter);
        listView.setOnItemClickListener(menuSelectionEvent());

        return view;
    }

    private AdapterView.OnItemClickListener menuSelectionEvent() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    mSession.logoutUser();
                    Toast.makeText(getActivity(), R.string.message_logout_profile, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public ArrayList<String> getListTitles() {
        listTitles = new ArrayList<>();
        listTitles.add("View profile");
        listTitles.add("About");
        listTitles.add("Log out");

        return listTitles;
    }

    public ArrayList<Integer> getListIds() {
        listIds = new ArrayList<>();
        listIds.add(R.drawable.ic_editprofile);
        listIds.add(R.drawable.ic_about);
        listIds.add(R.drawable.ic_signout);

        return listIds;
    }

}
