package ratingapp.ddey.com.testratingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GoogleHotel implements Parcelable {
    private long id;
    private String name;
    private float starsNumber;
    private String address;
    private String contactNumber;
    private String imgUrl;
    private String userToken;
    private String hotelToken;

    public GoogleHotel() {
        this.name = "";
        this.starsNumber = 0;
        this.address = "";
        this.contactNumber = "";
        this.imgUrl = "";
    }

    public GoogleHotel(long id, String name, float starsNumber, String address, String contactNumber, String imgUrl, String userToken, String hotelToken) {
        this.id = id;
        this.name = name;
        this.starsNumber = starsNumber;
        this.address = address;
        this.contactNumber = contactNumber;
        this.imgUrl = imgUrl;
        this.userToken = userToken;
        this.hotelToken = hotelToken;
    }

    protected GoogleHotel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        starsNumber = in.readFloat();
        address = in.readString();
        contactNumber = in.readString();
        imgUrl = in.readString();
        userToken = in.readString();
        hotelToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeFloat(starsNumber);
        dest.writeString(address);
        dest.writeString(contactNumber);
        dest.writeString(imgUrl);
        dest.writeString(userToken);
        dest.writeString(hotelToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GoogleHotel> CREATOR = new Creator<GoogleHotel>() {
        @Override
        public GoogleHotel createFromParcel(Parcel in) {
            return new GoogleHotel(in);
        }

        @Override
        public GoogleHotel[] newArray(int size) {
            return new GoogleHotel[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getHotelToken() {
        return hotelToken;
    }

    public void setHotelToken(String hotelToken) {
        this.hotelToken = hotelToken;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getStarsNumber() {
        return starsNumber;
    }

    public void setStarsNumber(float starsNumber) {
        this.starsNumber = starsNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}


