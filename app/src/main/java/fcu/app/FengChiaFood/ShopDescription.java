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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ShopDescription extends AppCompatActivity {

    private ImageView storeImageView;
    private TextView storeNameTextView;
    private TextView storeRatingTextView;
    private TextView storeAddressTextView;
    private TextView storeDescriptionTextView;
    private String googleMapUrl;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

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

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        String storeId = getIntent().getStringExtra("storeId");
        if (storeId != null) {
            loadShopDetailsFromFirestore(storeId);
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