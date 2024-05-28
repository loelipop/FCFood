package fcu.app.FengChiaFood;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView store_list;
    private List<ShopDetails> shopDetailsList;
    private ImageButton GoProfile;
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GoProfile = findViewById(R.id.user_profile);
        store_list = findViewById(R.id.StoreList);
        store_list.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DatabaseHelper(this);
        dbHelper.initializeDatabase();


        shopDetailsList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllStores();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int ratingIndex = cursor.getColumnIndex("rating");
            int photoIndex = cursor.getColumnIndex("photo");
            int addressIndex = cursor.getColumnIndex("address");
            int descriptionIndex = cursor.getColumnIndex("description");
            int googleMapUrlIndex = cursor.getColumnIndex("google_map_url");

            if (nameIndex >= 0 && ratingIndex >= 0 && photoIndex >= 0) {
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(idIndex);
                        String name = cursor.getString(nameIndex);
                        float rating = cursor.getFloat(ratingIndex);
                        byte[] imageBytes = cursor.getBlob(photoIndex);
                        String address = cursor.getString(addressIndex);
                        String description = cursor.getString(descriptionIndex);
                        String googleMapUrl = cursor.getString(googleMapUrlIndex);

                        Bitmap photo = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        shopDetailsList.add(new ShopDetails(id,photo, name, String.valueOf(rating), address, description, googleMapUrl));
                    } while (cursor.moveToNext());
                }
            } else {
                Toast.makeText(this, "Error retrieving store data from database.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        Log.d("MainActivity", "Loaded " + shopDetailsList.size() + " stores from database");
        for (ShopDetails details : shopDetailsList) {
            Log.d("MainActivity", "Store: " + details.getStoreName() + ", Rating: " + details.getStoreRating());
        }

        StoreListAdapter adapter = new StoreListAdapter(this, shopDetailsList);
        store_list.setAdapter(adapter);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        };
        GoProfile.setOnClickListener(listener);
    }
}