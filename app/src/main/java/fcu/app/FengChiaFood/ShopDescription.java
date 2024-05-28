package fcu.app.FengChiaFood;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShopDescription extends AppCompatActivity {

    private ImageView storeImageView;
    private TextView storeNameTextView;
    private TextView storeRatingTextView;
    private TextView storeAddressTextView;
    private TextView storeDescriptionTextView;
    private DatabaseHelper dbHelper;
    private String googleMapUrl;

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
        dbHelper = new DatabaseHelper(this);
        int storeId = getIntent().getIntExtra("storeId", -1);
        if (storeId != -1) {
            loadShopDetailsFromDatabase(storeId);
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

        storeAddressTextView.setOnClickListener(listener);
    }
    private void loadShopDetailsFromDatabase(int storeId) {
        Cursor cursor = dbHelper.getAllStores();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int ratingIndex = cursor.getColumnIndex("rating");
            int photoIndex = cursor.getColumnIndex("photo");
            int addressIndex = cursor.getColumnIndex("address");
            int descriptionIndex = cursor.getColumnIndex("description");
            int googleMapUrlIndex = cursor.getColumnIndex("google_map_url");

            if (idIndex >= 0 && nameIndex >= 0 && ratingIndex >= 0 && photoIndex >= 0 && addressIndex >= 0 && descriptionIndex >= 0 && googleMapUrlIndex >= 0) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    if (id == storeId) {
                        String name = cursor.getString(nameIndex);
                        float rating = cursor.getFloat(ratingIndex);
                        byte[] imageBytes = cursor.getBlob(photoIndex);
                        String address = cursor.getString(addressIndex);
                        String description = cursor.getString(descriptionIndex);
                        googleMapUrl = cursor.getString(googleMapUrlIndex);

                        Bitmap photo = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        storeImageView.setImageBitmap(photo);
                        storeNameTextView.setText(name);
                        storeRatingTextView.setText(String.valueOf(rating));
                        storeAddressTextView.setText(address);
                        storeDescriptionTextView.setText(description);
                        break;
                    }
                }
            } else {
                Toast.makeText(this, "Error retrieving store data from database.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
    }
}