package fcu.app.FengChiaFood;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShopDescription extends AppCompatActivity {

    private ImageView storeImageView;
    private TextView storeNameTextView;
    private TextView storeRatingTextView;
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

        ShopDetails shopDetails = getIntent().getParcelableExtra("shopDetails");

        if(shopDetails != null){
            storeImageView.setImageResource(shopDetails.getStoreImageId());
            storeNameTextView.setText(shopDetails.getStoreName());
            storeRatingTextView.setText(shopDetails.getStoreRating());
        }
    }
}