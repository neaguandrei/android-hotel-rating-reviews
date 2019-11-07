package ratingapp.ddey.com.testratingapp.utils.database;

public interface DatabaseConstants {
    String TAG = DatabaseHelper.class.getSimpleName();
    String DB_NAME = "myapp.db";
    int DB_VERSION = 34;

    //USERS
    String USER_TABLE = "users";
    String COLUMN_ID_USER = "idUser";
    String COLUMN_EMAIL = "email";
    String COLUMN_PASS = "password";
    String COLUMN_NAME = "name";
    String COLUMN_BIRTHDATE = "birthdate";
    String COLUMN_GENDER = "gender";
    String COLUMN_COUNTRY = "country";
    String COLUMN_TRAVELGROUP = "travelgroup";
    String COLUMN_ROOMTYPE = "roomtype";
    String COLUMN_USER_TOKEN = "firebaseToken";
    String COLUMN_IS_PROFILE_PUBLIC = "isProfilePublic";
    String CREATE_TABLE_USERS = "CREATE TABLE " + USER_TABLE + "("
            + COLUMN_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_PASS + " TEXT NOT NULL,"
            + COLUMN_BIRTHDATE + " TEXT,"
            + COLUMN_GENDER + " TEXT,"
            + COLUMN_COUNTRY + " TEXT,"
            + COLUMN_TRAVELGROUP + " TEXT,"
            + COLUMN_ROOMTYPE + " TEXT,"
            + COLUMN_IS_PROFILE_PUBLIC + " INTEGER,"
            + COLUMN_USER_TOKEN + " TEXT NOT NULL);";

    //NOTES
    String NOTE_TABLE = "notes";
    String COLUMN_ID_NOTE = "idNote";
    String COLUMN_NAME_NOTE = "name";
    String COLUMN_NOTE_TOKEN = "noteToken";
    String CREATE_TABLE_NOTES = "CREATE TABLE " + NOTE_TABLE + "("
            + COLUMN_ID_NOTE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_NOTE + " TEXT NOT NULL,"
            + COLUMN_NOTE_TOKEN + " TEXT,"
            + COLUMN_USER_TOKEN + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_USER_TOKEN + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_TOKEN + "));";

    // GOOGLE HOTELS
    String GOOGLE_HOTEL_TABLE = "googleHotels";
    String COLUMN_ID_GOOGLE_HOTEL = "idHotel";
    String COLUMN_NAME_HOTEL = "name";
    String COLUMN_GOOGLE_RATING = "googleRating";
    String COLUMN_ADDRESS = "address";
    String COLUMN_PHONE = "phone";
    String COLUMN_URL_HOTEL = "imgUrl";
    String COLUMN_GOOGLE_HOTEL_TOKEN = "hotelToken";
    String CREATE_TABLE_GOOGLE_HOTELS = "CREATE TABLE " + GOOGLE_HOTEL_TABLE + "("
            + COLUMN_ID_GOOGLE_HOTEL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_HOTEL + " TEXT NOT NULL,"
            + COLUMN_GOOGLE_RATING + " TEXT NOT NULL,"
            + COLUMN_ADDRESS + " TEXT,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_URL_HOTEL + " TEXT NOT NULL, "
            + COLUMN_GOOGLE_HOTEL_TOKEN + " TEXT,"
            + COLUMN_USER_TOKEN + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_USER_TOKEN + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_TOKEN + "));";

    // GOOGLE REVIEWS
    String GOOGLE_REVIEW_TABLE = "googleReviews";
    String COLUMN_ID_REVIEW = "idReview";
    String COLUMN_NAME_AUTHOR = "name";
    String COLUMN_RATING = "rating";
    String COLUMN_TIME_DESCRIPTION = "time";
    String COLUMN_TEXT_REVIEW = "text";
    String COLUMN_URL_REVIEW = "imgURL";
    String COLUMN_REVIEW_TOKEN = "reviewToken";
    String CREATE_TABLE_GOOGLE_HOTEL_REVIEWS = "CREATE TABLE " + GOOGLE_REVIEW_TABLE + "("
            + COLUMN_ID_REVIEW + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_AUTHOR + " TEXT NOT NULL,"
            + COLUMN_RATING + " TEXT NOT NULL,"
            + COLUMN_TIME_DESCRIPTION + " TEXT,"
            + COLUMN_TEXT_REVIEW + " TEXT,"
            + COLUMN_URL_REVIEW + " TEXT NOT NULL, "
            + COLUMN_REVIEW_TOKEN + " TEXT, "
            + COLUMN_GOOGLE_HOTEL_TOKEN + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_GOOGLE_HOTEL_TOKEN + ") REFERENCES " + GOOGLE_HOTEL_TABLE + "(" + COLUMN_GOOGLE_HOTEL_TOKEN + "));";

    // HOTELS
    String HOTEL_TABLE = "hotels";
    String COLUMN_ID_HOTEL = "idHotel";
    String COLUMN_HOTEL_NAME = "name";
    String COLUMN_CITY = "city";
    String COLUMN_DISTANCE_FROM_CITY_CENTER = "distanceFromCityCentre";
    String COLUMN_IMG_URL = "imgUrl";
    String COLUMN_PRICE = "price";
    String COLUMN_AVG_RATING = "avgRating";
    String COLUMN_STARS = "stars";
    String COLUMN_HOTEL_TOKEN = "hotelToken";
    String CREATE_TABLE_HOTELS = "CREATE TABLE " + HOTEL_TABLE + "("
            + COLUMN_ID_HOTEL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_HOTEL_NAME + " TEXT NOT NULL,"
            + COLUMN_CITY + " TEXT NOT NULL,"
            + COLUMN_DISTANCE_FROM_CITY_CENTER + " TEXT NOT NULL,"
            + COLUMN_IMG_URL + " TEXT NOT NULL,"
            + COLUMN_PRICE + " INTEGER NOT NULL, "
            + COLUMN_AVG_RATING + " REAL,"
            + COLUMN_STARS + " INTEGER,"
            + COLUMN_HOTEL_TOKEN + " TEXT,"
            + COLUMN_USER_TOKEN + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_USER_TOKEN + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_TOKEN + "));";

    //REVIEWS
    String REVIEW_TABLE = "reviews";
    String COLUMN_REVIEW_ID = "idReview";
    String COLUMN_DESCRIPTION = "description";
    String COLUMN_TITLE = "title";
    String COLUMN_REVIEW_DATE = "date";
    String COLUMN_IS_POSITIVE = "isPositive";
    String COLUMN_TOKEN_REVIEW = "reviewToken";
    String CREATE_TABLE_REVIEWS = "CREATE TABLE " + REVIEW_TABLE + "("
            + COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT NOT NULL,"
            + COLUMN_REVIEW_DATE + " TEXT NOT NULL,"
            + COLUMN_IS_POSITIVE + " INTEGER NOT NULL,"
            + COLUMN_TOKEN_REVIEW + " TEXT NOT NULL,"
            + COLUMN_USER_TOKEN + " TEXT NOT NULL, "
            + COLUMN_HOTEL_TOKEN + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_HOTEL_TOKEN + ") REFERENCES " + HOTEL_TABLE + "(" + COLUMN_HOTEL_TOKEN + "));";

    //RATINGS
    String RATINGS_TABLE = "ratings";
    String COLUMN_RATING_ID = "idRating";
    String COLUMN_VALUE = "value";
    String COLUMN_RATING_DATE = "date";
    String COLUMN_RATING_TOKEN = "ratingToken";
    String CREATE_TABLE_RATINGS = "CREATE TABLE " + RATINGS_TABLE + "("
            + COLUMN_RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_VALUE + " TEXT NOT NULL,"
            + COLUMN_RATING_DATE + " TEXT NOT NULL,"
            + COLUMN_RATING_TOKEN + " TEXT NOT NULL,"
            + COLUMN_USER_TOKEN + " TEXT NOT NULL, "
            + COLUMN_HOTEL_TOKEN + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_HOTEL_TOKEN + ") REFERENCES " + HOTEL_TABLE + "(" + COLUMN_HOTEL_TOKEN + "));";

}
