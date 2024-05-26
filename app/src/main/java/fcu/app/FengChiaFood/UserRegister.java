package fcu.app.FengChiaFood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserRegister extends AppCompatActivity {

    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private Button btnRegister;
    private Button btnGoToUserLogin;

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
                    if(!password.equals(passwordAgain)) {
                        Toast.makeText(UserRegister.this, "兩次密碼輸入不一致", Toast.LENGTH_SHORT).show();
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
}