package com.red;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button logOn = null;
    private Button logIn = null;
    private EditText account = null;
    private EditText password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOn = findViewById(R.id.Button_logup);
        logIn = findViewById(R.id.Button_login);
        account = findViewById(R.id.UserLogin);
        password = findViewById(R.id.Password);



        logOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogupMainActivity.class);
                startActivity(intent);
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account.getText().toString().equals("red")
                    && password.getText().toString().equals("1234")) {
                    Intent intent = new Intent(MainActivity.this, AfterLoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,R.string.login_fail,Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}
