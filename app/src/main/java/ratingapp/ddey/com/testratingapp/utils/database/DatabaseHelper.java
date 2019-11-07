package ratingapp.ddey.com.testratingapp.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ratingapp.ddey.com.testratingapp.models.GoogleHotel;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.models.MyReviewsRatingsModel;
import ratingapp.ddey.com.testratingapp.models.Note;
import ratingapp.ddey.com.testratingapp.models.Rating;
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.models.GoogleReview;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.others.Session;

public class DatabaseHelper extends SQLiteOpenHelper {
    FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();

    public DatabaseHelper(Context context) {
        super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.CREATE_TABLE_USERS);
        db.execSQL(DatabaseConstants.CREATE_TABLE_NOTES);

        db.execSQL(DatabaseConstants.CREATE_TABLE_GOOGLE_HOTELS);
        db.execSQL(DatabaseConstants.CREATE_TABLE_GOOGLE_HOTEL_REVIEWS);

        db.execSQL(DatabaseConstants.CREATE_TABLE_HOTELS);
        db.execSQL(DatabaseConstants.CREATE_TABLE_REVIEWS);
        db.execSQL(DatabaseConstants.CREATE_TABLE_RATINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.GOOGLE_HOTEL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.NOTE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.GOOGLE_REVIEW_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.HOTEL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.REVIEW_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.RATINGS_TABLE);

        onCreate(db);
    }

    // Methods for My Reviews and Hotels
    public void insertMyHotel(Hotel h, String currentUserToken) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_HOTEL_NAME, h.getHotelName());
        values.put(DatabaseConstants.COLUMN_CITY, h.getCity());
        values.put(DatabaseConstants.COLUMN_DISTANCE_FROM_CITY_CENTER, h.getDistanceFromCenter());
        values.put(DatabaseConstants.COLUMN_IMG_URL, h.getImageUrl());
        values.put(DatabaseConstants.COLUMN_PRICE, h.getPrice());
        values.put(DatabaseConstants.COLUMN_AVG_RATING, h.getRating());
        values.put(DatabaseConstants.COLUMN_STARS, h.getStars());
        values.put(DatabaseConstants.COLUMN_HOTEL_TOKEN, h.getToken());
        values.put(DatabaseConstants.COLUMN_USER_TOKEN, currentUserToken);
        db.insert(DatabaseConstants.HOTEL_TABLE, null, values);
        db.close();
    }

    public void insertMyReview(Review r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_TITLE, r.getTitle());
        values.put(DatabaseConstants.COLUMN_DESCRIPTION, r.getDescription());
        if (r.getDate() != null) {
            values.put(DatabaseConstants.COLUMN_REVIEW_DATE, Constants.simpleDateFormat.format(r.getDate()));
        }
        if (r.isPositive()) {
            values.put(DatabaseConstants.COLUMN_IS_POSITIVE, 1);
        } else {
            values.put(DatabaseConstants.COLUMN_IS_POSITIVE, 0);
        }
        values.put(DatabaseConstants.COLUMN_TOKEN_REVIEW, r.getReviewToken());
        values.put(DatabaseConstants.COLUMN_HOTEL_TOKEN, r.getHotelToken());
        values.put(DatabaseConstants.COLUMN_USER_TOKEN, r.getUserToken());

        db.insert(DatabaseConstants.REVIEW_TABLE, null, values);
        db.close();
    }

    public void insertMyRating(Rating r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_VALUE, r.getRateValue());
        if (r.getDate() != null) {
            values.put(DatabaseConstants.COLUMN_RATING_DATE, Constants.simpleDateFormat.format(r.getDate()));
        }
        values.put(DatabaseConstants.COLUMN_RATING_TOKEN, r.getRatingToken());
        values.put(DatabaseConstants.COLUMN_HOTEL_TOKEN, r.getHotelToken());
        values.put(DatabaseConstants.COLUMN_USER_TOKEN, r.getUserToken());

        db.insert(DatabaseConstants.RATINGS_TABLE, null, values);
        db.close();
    }

    public void updateMyRatings(Rating r) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_VALUE, r.getRateValue());
        if (r.getDate() != null) {
            values.put(DatabaseConstants.COLUMN_RATING_DATE, Constants.simpleDateFormat.format(r.getDate()));
        }

        db.update(DatabaseConstants.RATINGS_TABLE, values, DatabaseConstants.COLUMN_HOTEL_TOKEN + " = ? AND " + DatabaseConstants.COLUMN_USER_TOKEN + " = ? ", new String[]{r.getHotelToken(), r.getUserToken()});
        db.close();
    }

    public boolean isHotelInDb(String hotelToken) {
        String selectQuery = "SELECT * FROM " + DatabaseConstants.HOTEL_TABLE + " WHERE "
                + DatabaseConstants.COLUMN_HOTEL_TOKEN + " = " + "'" + hotelToken + "';";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0)
            return true;

        cursor.close();
        db.close();

        return false;
    }

    public boolean isGoogleHotelInDb(String hotelToken) {
        String selectQuery = "SELECT * FROM " + DatabaseConstants.GOOGLE_HOTEL_TABLE + " WHERE "
                + DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN + " = " + "'" + hotelToken + "';";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 1) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();

        return false;
    }



    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseConstants.COLUMN_PASS, user.getPassword());
        values.put(DatabaseConstants.COLUMN_NAME, user.getName());

        if (user.getBirthDate() != null)
            values.put(DatabaseConstants.COLUMN_BIRTHDATE, Constants.simpleDateFormat.format(user.getBirthDate()));
        else
            values.put(DatabaseConstants.COLUMN_BIRTHDATE, "");

        if (user.getGender() != null)
            values.put(DatabaseConstants.COLUMN_GENDER, user.getGender());
        else
            values.put(DatabaseConstants.COLUMN_GENDER, "");
        if (user.getCountry() != null)
            values.put(DatabaseConstants.COLUMN_COUNTRY, user.getCountry());
        else
            values.put(DatabaseConstants.COLUMN_COUNTRY, "");
        if (user.getTravelGroup() != null)
            values.put(DatabaseConstants.COLUMN_TRAVELGROUP, user.getTravelGroup());
        else
            values.put(DatabaseConstants.COLUMN_TRAVELGROUP, "");
        if (user.getRoomTypes() != null)
            values.put(DatabaseConstants.COLUMN_ROOMTYPE, user.getRoomTypes());
        else
            values.put(DatabaseConstants.COLUMN_ROOMTYPE, "");
        if (user.getFirebaseToken() != null)
            values.put(DatabaseConstants.COLUMN_USER_TOKEN, user.getFirebaseToken());
        else
            values.put(DatabaseConstants.COLUMN_USER_TOKEN, "");

        if (user.isProfilePublic())
            values.put(DatabaseConstants.COLUMN_IS_PROFILE_PUBLIC, 1);
        else
            values.put(DatabaseConstants.COLUMN_IS_PROFILE_PUBLIC, 0);

        long id = db.insert(DatabaseConstants.USER_TABLE, null, values);
        user.setId(id);
        db.close();

        Log.d(DatabaseConstants.TAG, "user inserted " + id);
    }

    public void insertHotels(GoogleHotel googleHotel, String currentUserToken) {
        SQLiteDatabase db = this.getWritableDatabase();

        firebaseHelper.openConnection();
        //verificare sa vad ca e insert
        if (googleHotel.getHotelToken() == null || googleHotel.getHotelToken().trim().isEmpty()) {
            googleHotel.setHotelToken(firebaseHelper.getGooglePlacesHotelsReference().push().getKey());
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_NAME_HOTEL, googleHotel.getName());
        values.put(DatabaseConstants.COLUMN_GOOGLE_RATING, googleHotel.getStarsNumber());
        values.put(DatabaseConstants.COLUMN_ADDRESS, googleHotel.getAddress());
        values.put(DatabaseConstants.COLUMN_PHONE, googleHotel.getContactNumber());
        values.put(DatabaseConstants.COLUMN_URL_HOTEL, googleHotel.getImgUrl());

        if (googleHotel.getHotelToken() != null)
            values.put(DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN, googleHotel.getHotelToken());

        values.put(DatabaseConstants.COLUMN_USER_TOKEN, currentUserToken);
        long id = db.insert(DatabaseConstants.GOOGLE_HOTEL_TABLE, null, values);
        googleHotel.setUserToken(currentUserToken);
        googleHotel.setId(id);
        firebaseHelper.getGooglePlacesHotelsReference().child(googleHotel.getHotelToken()).setValue(googleHotel);
        db.close();

        Log.d(DatabaseConstants.TAG, "googleHotel inserted " + id);
    }

    public void insertReview(GoogleReview review, GoogleHotel h) {
        SQLiteDatabase db = this.getWritableDatabase();
        firebaseHelper.openConnection();
        //daca fac insert
        if (review.getReviewToken() == null || review.getReviewToken().trim().isEmpty()) {
            review.setReviewToken(firebaseHelper.getGooglePlacesReviewsReference().push().getKey());
        }


        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_NAME_AUTHOR, review.getAuthor_name());
        values.put(DatabaseConstants.COLUMN_RATING, review.getRating());
        values.put(DatabaseConstants.COLUMN_TIME_DESCRIPTION, review.getRelative_time_description());
        values.put(DatabaseConstants.COLUMN_TEXT_REVIEW, review.getText());
        values.put(DatabaseConstants.COLUMN_URL_REVIEW, review.getProfile_photo_url());
        values.put(DatabaseConstants.COLUMN_REVIEW_TOKEN, review.getReviewToken());
        values.put(DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN, h.getHotelToken());

        long id = db.insert(DatabaseConstants.GOOGLE_REVIEW_TABLE, null, values);
        review.setHotelToken(h.getHotelToken());
        review.setId(id);
        firebaseHelper.getGooglePlacesReviewsReference().child(review.getReviewToken()).setValue(review);
        db.close();

        Log.d(DatabaseConstants.TAG, "review inserted " + id);
    }

    public void insertNote(Note note, String currentUserToken) {
        SQLiteDatabase db = this.getWritableDatabase();

        firebaseHelper.openConnection();
        if (note.getNoteToken() == null || note.getNoteToken().trim().isEmpty()) {
            note.setNoteToken(firebaseHelper.getUserNotesReference().push().getKey());
        }


        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_NAME_NOTE, note.getText());
        values.put(DatabaseConstants.COLUMN_NOTE_TOKEN, note.getNoteToken());
        values.put(DatabaseConstants.COLUMN_USER_TOKEN, currentUserToken);
        long id = db.insert(DatabaseConstants.NOTE_TABLE, null, values);
        db.close();
        note.setUserToken(currentUserToken);
        note.setIdNote(id);
        firebaseHelper.getUserNotesReference().child(note.getNoteToken()).setValue(note);
        Log.d(DatabaseConstants.TAG, "note inserted " + id);
    }


    public void updateNoteToken(Note note, String currentUserToken) {
        SQLiteDatabase db = this.getWritableDatabase();

        firebaseHelper.openConnection();
        //SA EXECUTAM STRICT PE INSERT

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_NOTE_TOKEN, note.getNoteToken());
        db.update(DatabaseConstants.NOTE_TABLE, values, DatabaseConstants.COLUMN_ID_NOTE + " = ?", new String[]{String.valueOf(note.getIdNote())});
        db.close();
        firebaseHelper.getUserNotesReference().child(note.getNoteToken()).setValue(note);
        Log.d(DatabaseConstants.TAG, "Note token updated");
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseConstants.NOTE_TABLE, DatabaseConstants.COLUMN_NOTE_TOKEN + " = ?", new String[]{note.getNoteToken()});
        db.close();
    }

    public List<Note> getNotesList(String currentUserToken) {
        List<Note> noteResult = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(DatabaseConstants.NOTE_TABLE, new String[]{DatabaseConstants.COLUMN_ID_NOTE, DatabaseConstants.COLUMN_NAME_NOTE, DatabaseConstants.COLUMN_NOTE_TOKEN, DatabaseConstants.COLUMN_USER_TOKEN}, DatabaseConstants.COLUMN_USER_TOKEN + " = ?", new String[]{currentUserToken}, null, null, null);
        while (c.moveToNext()) {
            int index = c.getColumnIndex(DatabaseConstants.COLUMN_ID_NOTE);
            long idNote = c.getLong(index);

            index = c.getColumnIndex(DatabaseConstants.COLUMN_NAME_NOTE);
            String noteText = c.getString(index);

            index = c.getColumnIndex(DatabaseConstants.COLUMN_NOTE_TOKEN);
            String noteToken = c.getString(index);

            index = c.getColumnIndex(DatabaseConstants.COLUMN_USER_TOKEN);
            String userToken = c.getString(index);

            noteResult.add(new Note(idNote, noteText, noteToken, userToken));
        }
        c.close();
        db.close();

        return noteResult;
    }


    public boolean getUser(User user) {
        String selectQuery = "SELECT * FROM " + DatabaseConstants.USER_TABLE + " WHERE "
                + DatabaseConstants.COLUMN_EMAIL + " = " + "'" + user.getEmail() + "';";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0)
            return true;

        cursor.close();
        db.close();

        return false;
    }


    public String getName(Session session) {
        HashMap<String, String> user = session.getUserDetails();
        String email = user.get(Session.KEY_EMAIL);
        String pass = user.get(Session.KEY_PASS);

        String selectQuery = "SELECT name FROM " + DatabaseConstants.USER_TABLE + " WHERE "
                + DatabaseConstants.COLUMN_EMAIL + " = " + "'" + email + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        String rez = "";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            rez = cursor.getString(cursor.getColumnIndex("name"));
        }

        cursor.close();
        db.close();

        return rez;
    }

    public String getHotelToken(long idHotel) {

        String selectQuery = "SELECT " + DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN + " FROM " + DatabaseConstants.GOOGLE_HOTEL_TABLE + " WHERE "
                + DatabaseConstants.COLUMN_ID_GOOGLE_HOTEL + " = " + "'" + idHotel + "';";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String hotelToken = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_GOOGLE_HOTEL_TOKEN));
        cursor.close();
        db.close();
        return hotelToken;
    }

    public String getUserToken(Session session) {
        HashMap<String, String> user = session.getUserDetails();
        String email = user.get(Session.KEY_EMAIL);

        String selectQuery = "SELECT " + DatabaseConstants.COLUMN_USER_TOKEN + " FROM " + DatabaseConstants.USER_TABLE + " WHERE "
                + DatabaseConstants.COLUMN_EMAIL + " = " + "'" + email + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_USER_TOKEN));
        cursor.close();
        db.close();

        return result;
    }

    public User retrieveUser(Session session) {
        SQLiteDatabase db = this.getReadableDatabase();

        HashMap<String, String> user = session.getUserDetails();
        String email = user.get(Session.KEY_EMAIL);
        String pass = user.get(Session.KEY_PASS);
        String selectQuery = "SELECT * FROM " + DatabaseConstants.USER_TABLE + " WHERE "
                + DatabaseConstants.COLUMN_EMAIL + " = " + "'" + email + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        User userRetrieved = new User();
        if (cursor.moveToFirst()) {
            do {
                userRetrieved.setId(cursor.getLong(cursor.getColumnIndex(DatabaseConstants.COLUMN_ID_USER)));
                userRetrieved.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_EMAIL)));
                userRetrieved.setName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_NAME)));
                userRetrieved.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_PASS)));
                try {
                    userRetrieved.setBirthDate(Constants.simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_BIRTHDATE))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                userRetrieved.setGender(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_GENDER)));
                userRetrieved.setCountry(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_COUNTRY)));
                userRetrieved.setTravelGroup(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_TRAVELGROUP)));
                userRetrieved.setRoomTypes(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_ROOMTYPE)));
                userRetrieved.setFirebaseToken(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_USER_TOKEN)));
                int x = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_IS_PROFILE_PUBLIC));

                if (x == 1)
                    userRetrieved.setProfilePublic(true);
                else
                    userRetrieved.setProfilePublic(false);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userRetrieved;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseConstants.COLUMN_NAME, user.getName());
        values.put(DatabaseConstants.COLUMN_PASS, user.getPassword());
        if (user.getBirthDate() != null) {
            values.put(DatabaseConstants.COLUMN_BIRTHDATE, Constants.simpleDateFormat.format(user.getBirthDate()));
        } else {
            values.put(DatabaseConstants.COLUMN_BIRTHDATE, "");
        }
        values.put(DatabaseConstants.COLUMN_GENDER, user.getGender());
        values.put(DatabaseConstants.COLUMN_COUNTRY, user.getCountry());
        values.put(DatabaseConstants.COLUMN_TRAVELGROUP, user.getTravelGroup());
        values.put(DatabaseConstants.COLUMN_ROOMTYPE, user.getRoomTypes());
        values.put(DatabaseConstants.COLUMN_USER_TOKEN, user.getFirebaseToken());

        if (user.isProfilePublic())
            values.put(DatabaseConstants.COLUMN_IS_PROFILE_PUBLIC, true);
        else
            values.put(DatabaseConstants.COLUMN_IS_PROFILE_PUBLIC, false);
        db.update(DatabaseConstants.USER_TABLE, values, DatabaseConstants.COLUMN_ID_USER + " = " + user.getId(), null);
        db.close();
    }

    public List<MyReviewsRatingsModel> getMyReviewsRatingsModels(String userToken) {
        List<MyReviewsRatingsModel> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        boolean doesItHaveReviewOrRating = false;
        Cursor c = db.query(DatabaseConstants.HOTEL_TABLE, new String[]{DatabaseConstants.COLUMN_HOTEL_NAME, DatabaseConstants.COLUMN_CITY, DatabaseConstants.COLUMN_HOTEL_TOKEN}, DatabaseConstants.COLUMN_USER_TOKEN + " = ?", new String[]{userToken}, null, null, null);
        while (c.moveToNext()) {
            MyReviewsRatingsModel newModel = new MyReviewsRatingsModel();
            String hotelName = c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_HOTEL_NAME));
            String city = c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_CITY));
            String hotelToken = c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_HOTEL_TOKEN));
            Hotel h = new Hotel();
            h.setHotelName(hotelName);
            h.setCity(city);
            Rating tempRating = null;
            Review tempReview = null;

            Cursor c1 = db.query(DatabaseConstants.REVIEW_TABLE, new String[]{DatabaseConstants.COLUMN_TITLE, DatabaseConstants.COLUMN_DESCRIPTION, DatabaseConstants.COLUMN_IS_POSITIVE, DatabaseConstants.COLUMN_REVIEW_TOKEN},
                    DatabaseConstants.COLUMN_USER_TOKEN + " = ? AND " + DatabaseConstants.COLUMN_HOTEL_TOKEN + " = ?", new String[]{userToken, hotelToken}, null, null, null);

            while (c1.moveToNext()) {
                String title = c1.getString(c1.getColumnIndex(DatabaseConstants.COLUMN_TITLE));
                String desc = c1.getString(c1.getColumnIndex(DatabaseConstants.COLUMN_DESCRIPTION));
                int temp = c1.getInt(c1.getColumnIndex(DatabaseConstants.COLUMN_IS_POSITIVE));
                String reviewToken = c1.getString(c1.getColumnIndex(DatabaseConstants.COLUMN_REVIEW_TOKEN));

                boolean isPositive;
                if (temp == 1) {
                    isPositive = true;
                } else {
                    isPositive = false;
                }

                if (title != null && desc != null && reviewToken != null && temp != -1) {
                    tempReview = new Review();
                    tempReview.setTitle(title);
                    tempReview.setDescription(desc);
                    tempReview.setReviewToken(reviewToken);
                    tempReview.setPositive(isPositive);
                    doesItHaveReviewOrRating = true;
                    break;
                }
            }

            Cursor c2 = db.query(DatabaseConstants.RATINGS_TABLE, new String[]{DatabaseConstants.COLUMN_VALUE, DatabaseConstants.COLUMN_RATING_TOKEN},
                    DatabaseConstants.COLUMN_USER_TOKEN + " = ? AND " + DatabaseConstants.COLUMN_HOTEL_TOKEN + " = ?", new String[]{userToken, hotelToken}, null, null, null);

            while (c2.moveToNext()) {
                int rateValue = c2.getInt(c2.getColumnIndex(DatabaseConstants.COLUMN_VALUE));
                String ratingToken = c2.getString(c2.getColumnIndex(DatabaseConstants.COLUMN_RATING_TOKEN));
                if (rateValue != -1 && ratingToken != null) {
                    tempRating = new Rating();
                    tempRating.setRateValue(rateValue);
                    tempRating.setRatingToken(ratingToken);
                    doesItHaveReviewOrRating = true;
                    break;
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
                resultList.add(newModel);
            }
        }
        db.close();
        return resultList;
    }

    public List<GoogleHotel> checkLocalDb(String userToken) {
        List<GoogleHotel> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(DatabaseConstants.GOOGLE_HOTEL_TABLE, new String[]{DatabaseConstants.COLUMN_HOTEL_TOKEN, DatabaseConstants.COLUMN_ID_GOOGLE_HOTEL}, DatabaseConstants.COLUMN_USER_TOKEN + " = ?", new String[]{userToken}, null, null, null);

        while (c.moveToNext()) {
            GoogleHotel gh = new GoogleHotel();
            gh.setHotelToken(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_HOTEL_TOKEN)));
            gh.setId(c.getLong(c.getColumnIndex(DatabaseConstants.COLUMN_ID_GOOGLE_HOTEL)));
            resultList.add(gh);
        }

        db.close();
        return resultList;
    }

}
