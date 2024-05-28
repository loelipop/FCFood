package fcu.app.FengChiaFood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton GoMain;
    private Button Shopregister;
    private Button GoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        GoMain = findViewById(R.id.main_profile);
        Shopregister = findViewById(R.id.shopregister);
        GoLogin = findViewById(R.id.login);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.shopregister){
                    Intent intent = new Intent();
                    intent.setClass(ProfileActivity.this, ShopRegister.class);
                    startActivity(intent);
                }else if(view.getId() == R.id.login){
                    Intent intent = new Intent();
                    intent.setClass(ProfileActivity.this, UserLogin.class);
                    startActivity(intent);
                }else if(view.getId() == R.id.main_profile){
                    Intent intent = new Intent();
                    intent.setClass(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        GoMain.setOnClickListener(listener);
        Shopregister.setOnClickListener(listener);
        GoLogin.setOnClickListener(listener);
    }
}