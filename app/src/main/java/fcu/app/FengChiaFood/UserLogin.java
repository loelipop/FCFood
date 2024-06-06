package fcu.app.FengChiaFood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

public class UserLogin extends AppCompatActivity {

    private EditText mail;
    private EditText pass;
    private Button Login;
    private Button Signup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mail = findViewById(R.id.Email);
        pass = findViewById(R.id.Password);
        Login = findViewById(R.id.btn_login);
        Signup = findViewById(R.id.btn_gotoregis);

        mAuth = FirebaseAuth.getInstance();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btn_login){
                    String Email = mail.getText().toString();
                    String Password = pass.getText().toString();
                    signIn(Email,Password);
                }else if(view.getId() == R.id.btn_gotoregis){
                    Intent intent = new Intent();
                    intent.setClass(UserLogin.this, UserRegister.class);
                    UserLogin.this.startActivity(intent);
                }
            }
        };
        Login.setOnClickListener(listener);
        Signup.setOnClickListener(listener);
    }
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 註冊成功，更新 UI
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(UserLogin.this, "登入成功",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(UserLogin.this,MainActivity.class);
                            UserLogin.this.startActivity(intent);
                        } else {
                            // 註冊失敗，顯示錯誤訊息
                            Toast.makeText(UserLogin.this, "登入失敗",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

}