package fcu.app.FengChiaFood;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShopRegister extends AppCompatActivity {

    private EditText etShopname;
    private EditText etShopLocation;
    private EditText etGoogleMapsLink;
    private EditText etShopInfo;
    private Button btnShopRegister;

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

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopname = etShopname.getText().toString();
                String shopLocation = etShopLocation.getText().toString();
                String googleMapsLink = etGoogleMapsLink.getText().toString();
                String shopInfo = etShopInfo.getText().toString();

                if(googleMapsLink.isEmpty()) {
                    googleMapsLink = "尚無Google Maps 連結";
                }
                if(shopInfo.isEmpty()) {
                    shopInfo = "店家很懶，什麼都沒介紹";
                }
            }
        };

        btnShopRegister.setOnClickListener(btnListener);
    }
}