package com.mhaa98.crackanalyzer;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        Button login = findViewById(R.id.login_btn2);
        final EditText email = findViewById(R.id.email_ad);
        final EditText pass = findViewById(R.id.pass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if(mail!="" && password!=""){
                    Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT * FROM data_user WHERE email = '"+mail+"' AND pass = '"+password+"'");
                    boolean s = false;
                    while (cursor.moveToNext()){
                        //LoginActivity.mSQLiteHelper.sudahMasuk(cursor.getInt(0));
                        Intent i = new Intent(Login.this,MainActivity.class);
                        startActivity(i);
                        s=true;

                    }
                    if(s==false){
                        Toast.makeText(Login.this, "Login gagal", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
