package fcu.app.FengChiaFood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class ShopDetails {
    private String storeImage;
    private String storeName;
    private String storeRating;
    private String storeAddress;
    private String storeDescription;
    private String googleMapUrl;
    private String storeId;

    public ShopDetails(String storeId, String storeImage, String storeName, String storeRating, String storeAddress, String storeDescription, String googleMapUrl) {
        this.storeId = storeId;
        this.storeImage = storeImage;
        this.storeName = storeName;
        this.storeRating = storeRating;
        this.storeAddress = storeAddress;
        this.storeDescription = storeDescription;
        this.googleMapUrl = googleMapUrl;
    }

    /*protected ShopDetails(Parcel in) {
        storeImage = in.readParcelable(Bitmap.class.getClassLoader());
        storeName = in.readString();
        storeRating = in.readString();
        storeAddress = in.readString();
        storeDescription = in.readString();
        googleMapUrl = in.readString();
    }*/

    /*public static final Creator<ShopDetails> CREATOR = new Creator<ShopDetails>() {
        @Override
        public ShopDetails createFromParcel(Parcel in) {
            return new ShopDetails(in);
        }

        @Override
        public ShopDetails[] newArray(int size) {
            return new ShopDetails[size];
        }
    };*/

    public String getStoreId() {
        return storeId;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreRating() {
        return storeRating;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

  /*  @Override
    public int describeContents() {
        return 0;
    }*/

  /*  @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(storeImage, flags);
        dest.writeString(storeName);
        dest.writeString(storeRating);
        dest.writeString(storeAddress);
        dest.writeString(storeDescription);
        dest.writeString(googleMapUrl);
        dest.writeInt(storeId);
    }*/

}
