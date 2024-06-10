package fcu.app.FengChiaFood;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopCollection extends AppCompatActivity {
    private RecyclerView store_list;
    private List<ShopDetails> shopDetailsList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView noFavorites;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_collection);

        store_list = findViewById(R.id.StoreList);
        noFavorites = findViewById(R.id.nocollection);
        store_list.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            store_list.setPadding(systemBars.left, store_list.getPaddingTop(), systemBars.right, store_list.getPaddingBottom());
            bottomNavigationView.setPadding(0, 0, 0, 0);
            return insets;
        });

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        shopDetailsList = new ArrayList<>();
        loadUserDetails();

        StoreListAdapter adapter = new StoreListAdapter(this, shopDetailsList);
        store_list.setAdapter(adapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.home_button){
                    Intent intent = new Intent();
                    intent.setClass(ShopCollection.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }else if(menuItem.getItemId() == R.id.profile_button){
                    Intent intent = new Intent();
                    intent.setClass(ShopCollection.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });


    }

    private void loadUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // 直接获取用户ID
            loadFavouriteStoreIds(userId); // 使用获取到的用户ID加载用户的收藏店铺ID
            noFavorites.setText("");
        } else {
            noFavorites.setText("用戶沒有登入");
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
                        if (favouriteStoreIds.isEmpty()) {
                            noFavorites.setText("沒有收藏店鋪");
                        } else {
                            fetchStoresByIds(favouriteStoreIds);
                        }
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