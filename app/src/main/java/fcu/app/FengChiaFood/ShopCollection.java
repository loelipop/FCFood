package fcu.app.FengChiaFood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShopCollection extends AppCompatActivity {
    private RecyclerView store_list;
    private List<ShopDetails> shopDetailsList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageButton GoProfile;
    private ImageButton GoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_collection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GoProfile = findViewById(R.id.user_profile);
        GoMain = findViewById(R.id.main_profile);
        store_list = findViewById(R.id.StoreList);
        store_list.setLayoutManager(new LinearLayoutManager(this));

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        shopDetailsList = new ArrayList<>();
        loadUserDetails();

        StoreListAdapter adapter = new StoreListAdapter(this, shopDetailsList);
        store_list.setAdapter(adapter);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.main_profile){
                    Intent intent = new Intent();
                    intent.setClass(ShopCollection.this, MainActivity.class);
                    startActivity(intent);
                }else if(view.getId() == R.id.user_profile){
                    Intent intent = new Intent();
                    intent.setClass(ShopCollection.this, ProfileActivity.class);
                    startActivity(intent);
                }
            }
        };
        GoProfile.setOnClickListener(listener);
        GoMain.setOnClickListener(listener);
    }

    private void loadUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // 直接获取用户ID
            loadFavouriteStoreIds(userId); // 使用获取到的用户ID加载用户的收藏店铺ID
        } else {
            Toast.makeText(ShopCollection.this, "User is not signed in", Toast.LENGTH_LONG).show();
        }
    }

    private void loadFavouriteStoreIds(String userId) {
        db.collection("favourites")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> favouriteStoreIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            favouriteStoreIds.add(document.getString("store_id"));
                        }
                        fetchStoresByIds(favouriteStoreIds);
                    } else {
                        Toast.makeText(ShopCollection.this, "Error getting favourite store IDs: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchStoresByIds(List<String> storeIds) {
        if (storeIds.isEmpty()) {
            return;
        }

        db.collection("stores")
                .whereIn(FieldPath.documentId(), storeIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            double rating = document.getDouble("rating");
                            String photoUrl = document.getString("photo_url");
                            String address = document.getString("address");
                            String description = document.getString("description");
                            String googleMapUrl = document.getString("google_map_url");

                            shopDetailsList.add(new ShopDetails(id, photoUrl, name, String.valueOf(rating), address, description, googleMapUrl));
                        }
                        store_list.getAdapter().notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShopCollection.this, "Error getting store details: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}