package com.mhaa98.crackanalyzer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HasilDiagnosisActivity extends AppCompatActivity {

    int kode;
    String level;
    double persen;
    TextView nama_bangunan;
    TextView hasil_diagnosis;
    TextView keterangan;
    ImageView gambar_bangunan,close;

    TextView jml_kolom, jml_balok, jml_dinding, k_rusak_r, k_rusak_s, k_rusak_b;
    TextView b_rusak_r, b_rusak_s, b_rusak_b, d_rusak_r, d_rusak_s, d_rusak_b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_diagnosis);

//        getSupportActionBar().setTitle("Hasil Diagnosis");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nama_bangunan = findViewById(R.id.nama_bangunan_txt2);
        hasil_diagnosis = findViewById(R.id.hasil_diagnosis_txt);
        keterangan = findViewById(R.id.keterangan_txt);
        gambar_bangunan = findViewById(R.id.poto_bangunan3);
        close = findViewById(R.id.close_diagnosis);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HasilDiagnosisActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        jml_kolom = findViewById(R.id.jum_kolom);
        jml_balok = findViewById(R.id.jum_balok);
        jml_dinding = findViewById(R.id.jum_dinding);

        k_rusak_r = findViewById(R.id.jum_ringan_k);
        k_rusak_s = findViewById(R.id.jum_sedang_k);
        k_rusak_b = findViewById(R.id.jum_berat_k);

        b_rusak_r = findViewById(R.id.jum_ringan_k_b);
        b_rusak_s = findViewById(R.id.jum_sedang_k_b);
        b_rusak_b = findViewById(R.id.jum_berat_k_b);

        d_rusak_r = findViewById(R.id.jum_ringan_k_d);
        d_rusak_s = findViewById(R.id.jum_sedang_k_d);
        d_rusak_b = findViewById(R.id.jum_berat_k_d);


        kode = getIntent().getExtras().getInt("id");
//        level = getIntent().getExtras().getString("level");
//        persen = getIntent().getExtras().getDouble("persen");

        String nama_b;
        byte[] image;
        String level="";
        double persen=0;
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT nama_bangunan, poto, hasil_diagnosis, tingkat_kepercayaan FROM data_bangunan WHERE id="+kode);
        while (cursor.moveToNext()){
            nama_b = cursor.getString(0);
            image = cursor.getBlob(1);
            level = cursor.getString(2);
            persen = cursor.getDouble(3);

            nama_bangunan.setText(nama_b);
            gambar_bangunan.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));

        }
        int jk=0;
        int jb=0;
        int jd=0;
        int jk_r=0;
        int jk_s=0;
        int jk_b=0;
        int jb_r=0;
        int jb_s=0;
        int jb_b=0;
        int jd_r=0;
        int jd_s=0;
        int jd_b=0;
        Cursor c = MainActivity.mSQLiteHelper.getData("SELECT struktur, level_kerusakan FROM data_kerusakan WHERE id_bangunan="+kode);
        while (c.moveToNext()){
            if(c.getInt(0)==1){
                jk++;
                if(c.getInt(1)==1){
                    jk_r++;
                }
                else if(c.getInt(1)==2){
                    jk_s++;
                }
                else if(c.getInt(1)==3){
                    jk_b++;
                }
            }
            else if(c.getInt(0)==2){
                jb++;
                if(c.getInt(1)==1){
                    jb_r++;
                }
                else if(c.getInt(1)==2){
                    jb_s++;
                }
                else if(c.getInt(1)==3){
                    jb_b++;
                }
            }
            else if(c.getInt(0)==3){
                jd++;
                if(c.getInt(1)==1){
                    jd_r++;
                }
                else if(c.getInt(1)==2){
                    jd_s++;
                }
                else if(c.getInt(1)==3){
                    jd_b++;
                }
            }
        }

        jml_kolom.setText(jk+"");
        jml_balok.setText(jb+"");
        jml_dinding.setText(jd+"");

        k_rusak_r.setText(jk_r+"");
        k_rusak_s.setText(jk_s+"");
        k_rusak_b.setText(jk_b+"");

        b_rusak_r.setText(jb_r+"");
        b_rusak_s.setText(jb_s+"");
        b_rusak_b.setText(jb_b+"");

        d_rusak_r.setText(jd_r+"");
        d_rusak_s.setText(jb_s+"");
        d_rusak_b.setText(jb_b+"");


        if(level.equals("1")){
            hasil_diagnosis.setText("Rusak Ringan");
            keterangan.setText("Kerusakan adalah rusak ringan dengan probabilitas "+persen);
        }
        else if(level.equals("2")){
            hasil_diagnosis.setText("Rusak sedang");
            keterangan.setText("Kerusakan adalah rusak sedang dengan probabilitas "+persen);
        }
        else if(level.equals("3")){
            hasil_diagnosis.setText("Rusak Berat");
            keterangan.setText("Kerusakan adalah rusak berat dengan probabilitas "+persen);
        }
        else if(level.equals("12")){
            hasil_diagnosis.setText("Rusak Ringan/Sedang");
            keterangan.setText("Kerusakan adalah rusak ringan dan sedang dengan probabilitas "+persen);
        }
        else if(level.equals("23")) {
            hasil_diagnosis.setText("Rusak Sedang/Berat");
            keterangan.setText("Kerusakan adalah rusak sedang dan berat dengan probabilitas "+persen);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(HasilDiagnosisActivity.this,MainActivity.class);
        startActivity(i);
    }
}

