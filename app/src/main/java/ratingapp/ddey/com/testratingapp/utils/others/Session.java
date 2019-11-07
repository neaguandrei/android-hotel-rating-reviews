package ratingapp.ddey.com.testratingapp.utils.others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import ratingapp.ddey.com.testratingapp.ui.auth.LoginActivity;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;

public class Session {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private FirebaseHelper mFirebaseHelper;
    private static final String PREFER_NAME = "myRatingApp";
    private static final String IS_USER_LOGIN = "IsUserLogged";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "pass";


    @SuppressLint("CommitPrefEdits")
    public Session(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        mFirebaseHelper = FirebaseHelper.getInstance();
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void createUserLoginSessionWithRemember(User user) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_PASS, user.getPassword());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.commit();
    }

    public void createUserLoginSessionWithoutRemember(User user) {
        editor.putString(KEY_PASS, user.getPassword());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.commit();
    }

    public boolean checkIfUserNeedsToBeLoggedOut() {
        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            return true;
        }
        return false;
    }

    public HashMap<String, String> getUserDetails() {


        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_EMAIL, prefs.getString(KEY_EMAIL, null));
        user.put(KEY_PASS, prefs.getString(KEY_PASS, null));

        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
        mFirebaseHelper.getAuth().signOut();
        Intent i = new Intent(context, LoginActivity.class);

        //inchidem toate activitatile
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // flag nou pt new activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    public boolean isUserLoggedIn() {
        return prefs.getBoolean(IS_USER_LOGIN, false);
    }

}
