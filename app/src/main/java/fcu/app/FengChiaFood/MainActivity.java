package fcu.app.FengChiaFood;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private RecyclerView store_list;
    private List<ShopDetails> shopDetailsList;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private TextView textView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //GoProfile = findViewById(R.id.user_profile);
        textView = findViewById(R.id.text_food_map);
        store_list = findViewById(R.id.StoreList);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        store_list.setLayoutManager(new LinearLayoutManager(this));

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        shopDetailsList = new ArrayList<>();
        loadStoresFromFirestore();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            store_list.setPadding(systemBars.left, store_list.getPaddingTop(), systemBars.right, store_list.getPaddingBottom());
            bottomNavigationView.setPadding(0, 0, 0, 0);
            return insets;
        });

        StoreListAdapter adapter = new StoreListAdapter(this, shopDetailsList);
        store_list.setAdapter(adapter);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.favourites_button){
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ShopCollection.class);
                    startActivity(intent);
                    return true;
                }else if(menuItem.getItemId() == R.id.profile_button){
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


    }

    private void loadStoresFromFirestore() {
        db.collection("stores").get()
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
                        Toast.makeText(MainActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}