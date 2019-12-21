package com.mhaa98.crackanalyzer;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    Button login;
    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=findViewById(R.id.login_btn);

        mSQLiteHelper = new SQLiteHelper(this, "kerusakan_bangunan.sqlite", null, 1);

        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS data_bangunan( id VARCHAR PRIMARY KEY," +
                "nama_bangunan VARCHAR, jumlah_lantai VARCHAR, tahun VARCHAR, alamat_bangunan VARCHAR, provinsi VARCHAR, " +
                "kota VARCHAR, kecamatan VARCHAR, kode_pos VARCHAR, latitude VARCHAR, " +
                "longitude VARCHAR, poto BLOB, nama VARCHAR, alamat VARCHAR, nomor_hp VARCHAR, hasil_diagnosis VARCHAR(3), tingkat_kepercayaan double, instance_bangunan VARCHAR)");

        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS data_kerusakan(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_bangunan VARCHAR, struktur INTEGER, level_kerusakan INTEGER, poto_kondisi BLOB)");

        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS data_gambar(data_g VARCHAR)");

        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS data_instance (id INTEGER, instance VARCHAR)");

        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS data_user (id_user INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email VARCHAR, pass VARCHAR, status INTEGER)");

        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS ds_gejala(id_gejala INTEGER, " +
                "nama_gejala VARCHAR)");
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS ds_level(id_level INTEGER, " +
                "level VARCHAR, kondisi VARCHAR, penaganan VARCHAR)");
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS ds_rules(level INTEGER, gejala INTEGER, cf DOUBLE)");

        boolean s = false;
        Cursor cursor = mSQLiteHelper.getData("SELECT status FROM data_user");
        while (cursor.moveToNext()){
            int status = cursor.getInt(0);
            if(status==1){
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
            }
            s = true;
        }
        if (s==false){
            mSQLiteHelper.insertUsers();
        }

        boolean instance = false;
        Cursor cc = mSQLiteHelper.getData("SELECT id FROM data_instance");
        while (cc.moveToNext()){
            instance = true;
        }
        if (instance==false){
            mSQLiteHelper.insertInstance();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,Login.class);
                startActivity(i);
            }
        });

        boolean terisi = false;
        Cursor curs = mSQLiteHelper.getData("SELECT id_gejala FROM ds_gejala");
        while (curs.moveToNext()){
            terisi = true;
            break;
        }

        if(terisi == false){
            mSQLiteHelper.insertGejala();
            mSQLiteHelper.insertLevel();
            mSQLiteHelper.insertRules();
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
