package fcu.app.FengChiaFood;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ShopDetails implements Parcelable {
    private int storeImageId;
    private String storeName;
    private String storeRating;

    public ShopDetails(int storeImageId, String storeName, String storeRating) {
        this.storeImageId = storeImageId;
        this.storeName = storeName;
        this.storeRating = storeRating;
    }

    protected ShopDetails(Parcel in) {
        storeImageId = in.readInt();
        storeName = in.readString();
        storeRating = in.readString();
    }

    public static final Creator<ShopDetails> CREATOR = new Creator<ShopDetails>() {
        @Override
        public ShopDetails createFromParcel(Parcel in) {
            return new ShopDetails(in);
        }

        @Override
        public ShopDetails[] newArray(int size) {
            return new ShopDetails[size];
        }
    };

    public int getStoreImageId() {
        return storeImageId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreRating() {
        return storeRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(storeImageId);
        dest.writeString(storeName);
        dest.writeString(storeRating);
    }
}
