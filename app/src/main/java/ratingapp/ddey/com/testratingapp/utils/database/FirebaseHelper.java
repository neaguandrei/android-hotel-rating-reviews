package ratingapp.ddey.com.testratingapp.utils.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


public class FirebaseHelper {
    private static final String USERS_NODE = "Users";
    private static final String GOOGLE_PLACES_REVIEWS = "GooglePlacesReviews";
    private static final String GOOGLE_PLACES_HOTELS = "GooglePlacesHotels";
    private static final String USER_NOTES = "Notes";
    private static final String HOTELS = "Hotels";
    private static final String STORAGE_PATH = "Images";


    private FirebaseAuth auth;
    private DatabaseReference usersReference;
    private DatabaseReference googlePlacesReviewsReference;
    private DatabaseReference googlePlacesHotelsReference;
    private DatabaseReference userNotesReference;
    private DatabaseReference hotelsReference;
    private DatabaseReference imagesReference;
    private FirebaseDatabase helper;
    private static FirebaseHelper firebaseHelper;

    private FirebaseStorage storage;


    public FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        helper = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    // Creare singleton-class
    public static FirebaseHelper getInstance() {
        if (firebaseHelper == null) {
            synchronized (FirebaseHelper.class) {
                if (firebaseHelper == null) {
                    firebaseHelper = new FirebaseHelper();
                }
            }
        }
        return firebaseHelper;
    }

    public void openConnection() {
        usersReference = helper.getReference(USERS_NODE);
        googlePlacesReviewsReference = helper.getReference(GOOGLE_PLACES_REVIEWS);
        googlePlacesHotelsReference = helper.getReference(GOOGLE_PLACES_HOTELS);
        userNotesReference = helper.getReference(USER_NOTES);
        hotelsReference = helper.getReference(HOTELS);
        imagesReference = helper.getReference(STORAGE_PATH);
    }

    public DatabaseReference getImagesReference() {
        return imagesReference;
    }

    public void setImagesReference(DatabaseReference imagesReference) {
        this.imagesReference = imagesReference;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public void setStorage(FirebaseStorage storage) {
        this.storage = storage;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public FirebaseDatabase getHelper() {
        return helper;
    }

    public void setHelper(FirebaseDatabase helper) {
        this.helper = helper;
    }

    public static FirebaseHelper getFirebaseHelper() {
        return firebaseHelper;
    }

    public static void setFirebaseHelper(FirebaseHelper firebaseHelper) {
        FirebaseHelper.firebaseHelper = firebaseHelper;
    }

    public DatabaseReference getUsersReference() {
        return usersReference;
    }

    public void setUsersReference(DatabaseReference usersReference) {
        this.usersReference = usersReference;
    }

    public DatabaseReference getGooglePlacesReviewsReference() {
        return googlePlacesReviewsReference;
    }

    public void setGooglePlacesReviewsReference(DatabaseReference googlePlacesReviewsReference) {
        this.googlePlacesReviewsReference = googlePlacesReviewsReference;
    }

    public DatabaseReference getGooglePlacesHotelsReference() {
        return googlePlacesHotelsReference;
    }

    public void setGooglePlacesHotelsReference(DatabaseReference googlePlacesHotelsReference) {
        this.googlePlacesHotelsReference = googlePlacesHotelsReference;
    }

    public DatabaseReference getUserNotesReference() {
        return userNotesReference;
    }

    public void setUserNotesReference(DatabaseReference userNotesReference) {
        this.userNotesReference = userNotesReference;
    }

    public DatabaseReference getHotelsReference() {
        return hotelsReference;
    }

    public void setHotelsReference(DatabaseReference hotelsReference) {
        this.hotelsReference = hotelsReference;
    }
}
