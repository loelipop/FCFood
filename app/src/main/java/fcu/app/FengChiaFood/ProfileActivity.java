package fcu.app.FengChiaFood;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView username;
    private TextView email;
    private TextView fcount;
    private Button Shopregister;
    private Button GoLogin;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        Shopregister = findViewById(R.id.shopregister);
        GoLogin = findViewById(R.id.login);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        username = findViewById(R.id.tv_username);
        email = findViewById(R.id.tv_email);
        fcount = findViewById(R.id.tv_favorites);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            username.setPadding(systemBars.left, username.getPaddingTop(), systemBars.right, username.getPaddingBottom());
            bottomNavigationView.setPadding(0, 0, 0, 0);
            return insets;
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            GoLogin.setText("登出");
            String userId = currentUser.getUid();
            db.collection("users").whereEqualTo("user_id", userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                username.setText(document.getString("user_name"));
                                email.setText(document.getString("email"));
                            }
                        }
                    });
            db.collection("favourites")
                    .whereEqualTo("user_id", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int favourites_count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                favourites_count++;
                            }
                            fcount.setText(String.valueOf(favourites_count));
                        }
                    });

        }else{
            GoLogin.setText("登入");
            username.setText("未登入");
            email.setText("");
            fcount.setText("0");
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.shopregister){
                    if(currentUser != null){
                        Intent intent = new Intent();
                        intent.setClass(ProfileActivity.this, ShopRegister.class);
                        startActivity(intent);
                        finish();
                    }{
                        Toast.makeText(ProfileActivity.this, "請先登入以使用本功能" , Toast.LENGTH_LONG).show();
                    }
                }else if(view.getId() == R.id.login){
                    if(currentUser != null){
                        mAuth.signOut();
                        Toast.makeText(ProfileActivity.this, "登出成功" , Toast.LENGTH_LONG).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent();
                        intent.setClass(ProfileActivity.this, UserLogin.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.favourites_button){
                    Intent intent = new Intent();
                    intent.setClass(ProfileActivity.this, ShopCollection.class);
                    startActivity(intent);
                    finish();
                    return true;
                }else if(menuItem.getItemId() == R.id.home_button){
                    Intent intent = new Intent();
                    intent.setClass(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        Shopregister.setOnClickListener(listener);
        GoLogin.setOnClickListener(listener);
    }
}