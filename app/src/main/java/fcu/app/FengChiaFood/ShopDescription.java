package fcu.app.FengChiaFood;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopDescription extends AppCompatActivity {

    private ImageView storeImageView;
    private TextView storeNameTextView;
    private TextView storeRatingTextView;
    private TextView storeAddressTextView;
    private TextView storeDescriptionTextView;
    private String googleMapUrl;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private Button addFavourites;
    private String userId = "";
    private boolean userHasStoreId = false;
    private Button subrating;
    private RatingBar rating;
    private String ratingDocumentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_description);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        storeImageView = findViewById(R.id.des_photo);
        storeNameTextView = findViewById(R.id.des_shopname);
        storeRatingTextView = findViewById(R.id.des_rating);
        storeAddressTextView = findViewById(R.id.des_address);
        storeDescriptionTextView = findViewById(R.id.des_description);
        addFavourites = findViewById(R.id.button_favourites);
        rating = findViewById(R.id.ratingBar);
        subrating = findViewById(R.id.submit_rating);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        addFavourites.setBackgroundColor(getResources().getColor(R.color.blue));
        addFavourites.setTextColor(getResources().getColor(android.R.color.white));

        String storeId = getIntent().getStringExtra("storeId");
        if(currentUser != null){
            userId = currentUser.getUid();
        }

        if (storeId != null) {
            loadShopDetailsFromFirestore(storeId);
        }
       if (currentUser != null && storeId != null){
           loadFavouritesDetails(userId, storeId);
           loadUserRating(userId, storeId);
       }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleMapUrl != null && !googleMapUrl.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleMapUrl));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(ShopDescription.this, "No application can handle this request. Please install a web browser or Google Maps.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopDescription.this, "No Google Maps URL available", Toast.LENGTH_SHORT).show();
                }
            }
        };

        View.OnClickListener flistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser == null){
                    Toast.makeText(ShopDescription.this,"請登入才可使用收藏功能",Toast.LENGTH_SHORT).show();
                }else{
                    if(doesUserHaveStoreId() == false){
                        Map<String, Object> favourite = new HashMap<>();
                        favourite.put("user_id", userId);
                        favourite.put("store_id", storeId);
                        db.collection("favourites")
                                .document() //set custom document ID
                                .set(favourite) //the object to be added
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        addFavourites.setText("已收藏");
                                        addFavourites.setBackgroundColor(getResources().getColor(R.color.gold));
                                        userHasStoreId = true;
                                        Log.d("AddDB", "Add database success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("AddDB", "Error adding document", e);
                                    }
                                });
                    }else{
                        db.collection("favourites")
                                .whereEqualTo("user_id", userId)
                                .whereEqualTo("store_id", storeId)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            db.collection("favourites").document(document.getId()).delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("RemoveDB", "Store ID removed from favourites.");
                                                        addFavourites.setText("收藏");
                                                        addFavourites.setBackgroundColor(getResources().getColor(R.color.blue));
                                                        userHasStoreId = false;
                                                    })
                                                    .addOnFailureListener(e -> Log.w("RemoveDB", "Error removing document", e));
                                        }
                                    }
                                });
                    }
                }
            }
        };

        View.OnClickListener rlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    Toast.makeText(ShopDescription.this,"請登入才可使用評分功能",Toast.LENGTH_SHORT).show();
                }else{
                    Map<String, Object> ratingData = new HashMap<>();
                    ratingData.put("user_id", userId);
                    ratingData.put("store_id", storeId);
                    ratingData.put("rating", rating.getRating());

                   if (ratingDocumentId != null){
                        db.collection("ratings").document(ratingDocumentId)
                                .set(ratingData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        updatOverallrating(storeId);
                                        Toast.makeText(ShopDescription.this, "評分更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("SubmitRating", "Error updating rating", e);
                                        Toast.makeText(ShopDescription.this, "評分更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        db.collection("ratings")
                                .document()
                                .set(ratingData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        updatOverallrating(storeId);
                                        Toast.makeText(ShopDescription.this, "評分更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("SubmitRating", "Error updating rating", e);
                                        Toast.makeText(ShopDescription.this, "評分更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        };

        storeAddressTextView.setOnClickListener(listener);
        addFavourites.setOnClickListener(flistener);
        subrating.setOnClickListener(rlistener);
    }

    private void updatOverallrating(String storeId){
        db.collection("ratings")
                .whereEqualTo("store_id", storeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        double totalRating = 0;
                        int ratingCount = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double rating = document.getDouble("rating");
                            if (rating != null) {
                                totalRating += rating;
                                ratingCount++;
                            }
                        }
                        double overallRating = (ratingCount > 0) ? (totalRating / ratingCount) : 0;
                        db.collection("stores").document(storeId)
                                .update("rating", overallRating)
                                .addOnSuccessListener(aVoid -> {
                                    storeRatingTextView.setText(String.valueOf(overallRating));
                                    Log.d("SubmitRating", "Overall rating updated successfully");
                                })
                                .addOnFailureListener(e -> Log.w("SubmitRating", "Error updating overall rating", e));
                    }
                })
                .addOnFailureListener(e -> Log.w("SubmitRating", "Error calculating overall rating", e));
    }

    private void loadFavouritesDetails(String userId, String storeId){
        db.collection("favourites")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("store_id", storeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        userHasStoreId = !querySnapshot.isEmpty();
                        if (userHasStoreId) {
                            addFavourites.setText("已收藏");
                            addFavourites.setBackgroundColor(getResources().getColor(R.color.gold));
                            addFavourites.setTextColor(getResources().getColor(android.R.color.white)); // Set text color to white for contrast
                        } else {
                            addFavourites.setText("收藏");
                            addFavourites.setBackgroundColor(getResources().getColor(R.color.blue));
                            addFavourites.setTextColor(getResources().getColor(android.R.color.white)); // Set text color to white for contrast
                        }

                    }
                });
    }
    public boolean doesUserHaveStoreId() {
        return userHasStoreId;
    }

    private void loadUserRating(String userId, String storeId) {
        db.collection("ratings")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("store_id", storeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Double ratings = document.getDouble("rating");
                                if (rating != null) {
                                    rating.setRating(ratings.floatValue());
                                }
                                ratingDocumentId = document.getId();
                            }
                        }
                    } else {
                        ratingDocumentId = null;
                        Log.w("LoadRating", "Error getting documents.", task.getException());
                    }
                });
    }

    private void loadShopDetailsFromFirestore(String storeId){
        db.collection("stores").document(storeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        double rating = documentSnapshot.getDouble("rating");
                        String photoUrl = documentSnapshot.getString("photo_url");
                        String address = documentSnapshot.getString("address");
                        String description = documentSnapshot.getString("description");
                        googleMapUrl = documentSnapshot.getString("google_map_url");
                        StorageReference photoref = storage.getReferenceFromUrl(photoUrl);
                        try{
                            final File file =File.createTempFile("image", "jpg");
                            photoref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    storeImageView.setImageBitmap(bitmap);
                                }
                            });
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        storeNameTextView.setText(name);
                        storeRatingTextView.setText(String.valueOf(rating));
                        storeAddressTextView.setText(address);
                        storeDescriptionTextView.setText(description);


                    } else {
                        Toast.makeText(ShopDescription.this, "Store not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ShopDescription.this, "Error loading store details", Toast.LENGTH_SHORT).show();
                });
    }
}