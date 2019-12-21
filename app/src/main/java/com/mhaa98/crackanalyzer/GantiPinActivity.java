package com.mhaa98.crackanalyzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GantiPinActivity extends AppCompatActivity {

    EditText pass;
    Button simpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_pin);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pass = findViewById(R.id.pass);
        simpan = findViewById(R.id.pin_simpan_btn);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass.getText().length()==4){
                    popUp();
                }
                else {
                    Toast.makeText(GantiPinActivity.this, "pin harus 4 digit", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void popUp() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(GantiPinActivity.this);
        dialogDelete.setTitle("Simpan PIN?");
        dialogDelete.setMessage("PIN akan digunakan untuk masuk ke aplikasi");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.mSQLiteHelper.insertPin(pass.getText()+"");
                Intent i = new Intent(GantiPinActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
