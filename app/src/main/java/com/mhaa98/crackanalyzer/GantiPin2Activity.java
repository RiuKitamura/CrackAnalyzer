package com.mhaa98.crackanalyzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GantiPin2Activity extends AppCompatActivity {

    EditText pinLama, pinBaru;
    Button simpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_pin2);

        getSupportActionBar().setTitle("Ganti PIN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pinLama = findViewById(R.id.pin_lama);
        pinBaru = findViewById(R.id.pin_baru);
        simpan = findViewById(R.id.pin_simpan_btn);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinLama.getText().length()==4 && pinBaru.getText().length()==4){
                    Cursor c = LoginActivity.mSQLiteHelper.getData("SELECT pass FROM data_pin");
                    while (c.moveToNext()){
                        String a = pinLama.getText()+"";
                        if (a.equals(c.getString(0))){
                            popUp();
                        }
                        else {
                            Toast.makeText(GantiPin2Activity.this, "PIN lama salah", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                    Toast.makeText(GantiPin2Activity.this, "PIN terdiri dari 4 digit", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void popUp() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(GantiPin2Activity.this);
        dialogDelete.setTitle("Simpan PIN baru?");
        dialogDelete.setMessage("PIN akan digunakan untuk masuk ke aplikasi");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.mSQLiteHelper.updatePin(pinBaru.getText()+"");
                Intent i = new Intent(GantiPin2Activity.this, MainActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
