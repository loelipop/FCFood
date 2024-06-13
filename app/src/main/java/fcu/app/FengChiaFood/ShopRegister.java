package fcu.app.FengChiaFood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ShopRegister extends AppCompatActivity {

    private EditText etShopname;
    private EditText etShopLocation;
    private EditText etGoogleMapsLink;
    private EditText etShopInfo;
    private Button btnShopRegister;
    private Button btnSelPic;
    private ImageView ivUploadPic;
    //private TextView tvPicName;

    private StorageReference ref;
    private FirebaseFirestore db;

    private Map<String, Object> shop = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etShopname = findViewById(R.id.et_shopname);
        etShopLocation = findViewById(R.id.et_shoplocation);
        etGoogleMapsLink = findViewById(R.id.et_googleMapsLink);
        etShopInfo = findViewById(R.id.et_shopinfo);
        btnShopRegister = findViewById(R.id.btn_shopregister);
        btnSelPic = findViewById(R.id.btn_selpic);
        ivUploadPic = findViewById(R.id.iv_uploadpic);
        //tvPicName = findViewById(R.id.tv_picname);

        ref = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.btn_shopregister) {
                    String shopname = etShopname.getText().toString();
                    String shopLocation = etShopLocation.getText().toString();
                    String googleMapsLink = etGoogleMapsLink.getText().toString();
                    String shopInfo = etShopInfo.getText().toString();
                    String photoURL = "gs://fengchiafood.appspot.com/" + shopname + ".jpg";

                    StorageReference shopPicRef = ref.child(shopname + ".jpg");

                    if (shopname.isEmpty() || shopLocation.isEmpty() || googleMapsLink.isEmpty() || shopInfo.isEmpty()) {
                        Toast.makeText(ShopRegister.this, "有資料缺漏，請再檢查一次", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        shop.put("address", shopLocation);
                        shop.put("description", shopInfo);
                        shop.put("google_map_url", googleMapsLink);
                        shop.put("name", shopname);
                        shop.put("photo_url", photoURL);
                        shop.put("rating", 4.2); //random num for temp

                        db.collection("stores")
                                .document()
                                .set(shop)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("AddDB", "Add database success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("AddDB", "Error adding document", e);
                                    }
                                });

                        ivUploadPic.setDrawingCacheEnabled(true);
                        ivUploadPic.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) ivUploadPic.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = shopPicRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(ShopRegister.this, "圖片上傳失敗", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(ShopRegister.this, "圖片上傳成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intent = new Intent();
                        intent.setClass(ShopRegister.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                else if(v.getId() == R.id.btn_selpic) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(intent, 1);
                }
            }
        };

        btnShopRegister.setOnClickListener(btnListener);
        btnSelPic.setOnClickListener(btnListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Get the url of the image from data
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // update the preview image in the layout
                ivUploadPic.setImageURI(selectedImageUri);
                //tvPicName.setText(selectedImageUri.toString());
            }
        }
    }
}