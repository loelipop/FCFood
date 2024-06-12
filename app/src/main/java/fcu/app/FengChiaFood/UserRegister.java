package fcu.app.FengChiaFood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRegister extends AppCompatActivity {

    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private Button btnRegister;
    private Button btnGoToUserLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPasswordAgain = findViewById(R.id.et_passwordagain);
        btnRegister = findViewById(R.id.btn_register);
        btnGoToUserLogin = findViewById(R.id.btn_gotouserlogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.btn_register) {
                    String email = etEmail.getText().toString();
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    String passwordAgain = etPasswordAgain.getText().toString();

                    if(password.length() < 8) {
                        Toast.makeText(UserRegister.this, "密碼長度不足", Toast.LENGTH_SHORT).show();
                    }
                    else if(!password.equals(passwordAgain)) {
                        Toast.makeText(UserRegister.this, "兩次密碼輸入不一致", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        register(email, password);

                        user.put("email", email);
                        user.put("user_id", mAuth.getCurrentUser().getUid());
                        user.put("user_name", username);

                        /*
                        if(mAuth.getCurrentUser().getUid().isEmpty()) {
                            return;
                        }
                        */

                        db.collection("users")
                                .document() //set custom document ID
                                .set(user) //the object to be added
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
                    }
                }
                else if(v.getId() == R.id.btn_gotouserlogin){
                    Intent intent = new Intent();
                    intent.setClass(UserRegister.this, UserLogin.class);
                    startActivity(intent);
                }
            }
        };

        btnRegister.setOnClickListener(btnListener);
        btnGoToUserLogin.setOnClickListener(btnListener);
    }

    private void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(UserRegister.this, "註冊成功", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UserRegister.this, "註冊失敗", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}