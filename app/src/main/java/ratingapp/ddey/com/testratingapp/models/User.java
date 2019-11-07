package ratingapp.ddey.com.testratingapp.models;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private long id;
    private String email;
    private String password;
    private String name;
    private Date birthDate;
    private String gender;
    private String country;
    private String travelGroup;
    private String roomTypes;
    private String firebaseToken;
    private boolean isProfilePublic;
    private String profilePictureURL;
    private String storageImageToken;

    public User() {
        id = 0;
        email = null;
        password = null;
        name = null;
        birthDate = null;
        gender = null;
        country = null;
        travelGroup = null;
        roomTypes = null;
        firebaseToken = null;
    }

    public User(long id, String email, String password, String name, Date birthDate, String gender, String country, String travelGroup, String roomTypes, String firebaseToken) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.country = country;
        this.travelGroup = travelGroup;
        this.roomTypes = roomTypes;
        this.firebaseToken = firebaseToken;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password; }

    public User(String name, String email, String password) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User(String email, String password, String name, Date birthDate, String firebaseToken, String gender, String country, boolean isProfilePublic) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.firebaseToken = firebaseToken;
        this.gender = gender;
        this.country = country;
        this.isProfilePublic = isProfilePublic;
    }

    public User(String name, String email, String password, String firebaseToken) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.firebaseToken = firebaseToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTravelGroup() {
        return travelGroup;
    }

    public void setTravelGroup(String travelGroup) {
        this.travelGroup = travelGroup;
    }

    public String getRoomTypes() {
        return roomTypes;
    }

    public void setRoomTypes(String roomTypes) {
        this.roomTypes = roomTypes;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isProfilePublic() {
        return isProfilePublic;
    }

    public void setProfilePublic(boolean profilePublic) {
        isProfilePublic = profilePublic;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public String getStorageImageToken() {
        return storageImageToken;
    }

    public void setStorageImageToken(String storageImageToken) {
        this.storageImageToken = storageImageToken;
    }
}
