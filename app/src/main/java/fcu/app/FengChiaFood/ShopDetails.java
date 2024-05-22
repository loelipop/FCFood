package fcu.app.FengChiaFood;

public class ShopDetails {
    private int storeImageId;
    private String storeName;
    private String storeRating;

    public ShopDetails(int storeImageId, String storeName, String storeRating) {
        this.storeImageId = storeImageId;
        this.storeName = storeName;
        this.storeRating = storeRating;
    }

    public int getStoreImageId() {
        return storeImageId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreRating() {
        return storeRating;
    }
}
