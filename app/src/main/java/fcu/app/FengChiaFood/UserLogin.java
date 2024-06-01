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

public class UserLogin extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private Button login;
    private String mail = "tom@fcu.edu.tw";
    private String pass = "fcu123";
    public static final String USERNAME ="User Name";
    public static final String PASSWORD ="Password";

    private Button btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        login = findViewById(R.id.btn_login);

        btnGoToRegister = findViewById(R.id.btn_gotoregis);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Email.getText().toString();
                String password = Password.getText().toString();

                if(username.equals(mail) && password.equals(pass)){
                    Bundle bundle  = new Bundle();
                    bundle.putString(USERNAME,username);
                    bundle.putString(PASSWORD,password);

                    Intent intent = new Intent();
                    intent.setClass(UserLogin.this,MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(UserLogin.this,"賬號密碼錯誤，請重新輸入",Toast.LENGTH_LONG).show();
                }
            }
        };
        View.OnClickListener gotolistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserLogin.this, UserRegister.class);
                startActivity(intent);
            }
        };
        login.setOnClickListener(listener);
        btnGoToRegister.setOnClickListener(gotolistener);
    }
}