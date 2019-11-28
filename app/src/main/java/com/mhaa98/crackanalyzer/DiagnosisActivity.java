package com.mhaa98.crackanalyzer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DiagnosisActivity extends AppCompatActivity {

    Button diagnosis;
    ImageView imageViewIcon;
    RelativeLayout kolom, balok, dinding;

    ListView mListView;
    KondisiStrukturListAdapter mAdapter = null;
    int kod;
    String kode;
    byte[] image;

    //variabel diagnosis
    int jum_gejala;
    int jum_kolom, jml_kolom_rusak_ringan, jml_kolom_rusak_sedang, jml_kolom_rusak_berat;
    int jum_balok, jml_balok_rusak_ringan, jml_balok_rusak_sedang, jml_balok_rusak_berat;
    int jum_dinding, jml_dinding_rusak_ringan, jml_dinding_rusak_sedang, jml_dinding_rusak_berat;

    double persen_kolom_rusak_ringan, persen_kolom_rusak_sedang, persen_kolom_rusak_berat;
    double persen_balok_rusak_ringan, persen_balok_rusak_sedang, persen_balok_rusak_berat;
    double persen_dinding_rusak_ringan, persen_dinding_rusak_sedang, persen_dinding_rusak_berat;
    //////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        kolom = findViewById(R.id.kolom_btn);
        balok = findViewById(R.id.balok_btn);
        dinding = findViewById(R.id.dinding_btn);

        diagnosis = findViewById(R.id.diagnosis_btn);


        getSupportActionBar().setTitle("Kondisi Bangunan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        kode = getIntent().getExtras().getString("id");
        refreshText(kode);
//        int a=0;
//        kod=kode;
//        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT id FROM data_kerusakan WHERE id_bangunan="+kode+" ORDER BY id DESC");
//        while (cursor.moveToNext()){
//            int id = cursor.getInt(0);
//            a++;
//        }
//        TextView jml_kolom = findViewById(R.id.jml_kolom);
//        jml_kolom.setText(a+"");
        TextView namaBg = findViewById(R.id.nama_bangunan_txt);
        imageViewIcon = findViewById(R.id.poto_bangunan2);

        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT nama_bangunan, poto FROM data_bangunan WHERE id="+kode);
        while (cursor.moveToNext()){
            String nama_b = cursor.getString(0);
            image = cursor.getBlob(1);

            namaBg.setText(nama_b);
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));

        }

        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(DiagnosisActivity.this);
            }
        });

        kolom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToKondisiKolom(kode,1);
            }
        });
        balok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToKondisiKolom(kode,2);
            }
        });
        dinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToKondisiKolom(kode, 3);
            }
        });

        diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagnosis(kode);
            }
        });
    }

    public class ViewDialog {
        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.show_image);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            ImageView gambar = dialog.findViewById(R.id.show_image2);
            gambar.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
            ImageButton close = dialog.findViewById(R.id.close_image);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }

    void refreshText(String kod){
        int a=0;

        jum_gejala=0;

        jum_kolom=0; jml_kolom_rusak_ringan=0; jml_kolom_rusak_sedang=0; jml_kolom_rusak_berat=0;
        jum_balok=0; jml_balok_rusak_ringan=0; jml_balok_rusak_sedang=0; jml_balok_rusak_berat=0;
        jum_dinding=0; jml_dinding_rusak_ringan=0; jml_dinding_rusak_sedang=0; jml_dinding_rusak_berat=0;

        persen_kolom_rusak_ringan=0; persen_kolom_rusak_sedang=0; persen_kolom_rusak_berat=0;
        persen_balok_rusak_ringan=0; persen_balok_rusak_sedang=0; persen_balok_rusak_berat=0;
        persen_dinding_rusak_ringan=0; persen_dinding_rusak_sedang=0; persen_dinding_rusak_berat=0;

        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT id, level_kerusakan FROM data_kerusakan WHERE id_bangunan='"+kod+"' AND struktur= 1 ORDER BY id DESC");
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            a++;
            jum_kolom++;
            if(cursor.getInt(1)==1){
                jml_kolom_rusak_ringan++;
            }else if(cursor.getInt(1)==2){
                jml_kolom_rusak_sedang++;
            }else if(cursor.getInt(1)==3){
                jml_kolom_rusak_berat++;
            }
        }
        TextView jml_kolom = findViewById(R.id.jml_kolom);
        jml_kolom.setText(a+"");
        a=0;

        cursor = LoginActivity.mSQLiteHelper.getData("SELECT id, level_kerusakan FROM data_kerusakan WHERE id_bangunan="+kod+" AND struktur= 2 ORDER BY id DESC");
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            a++;
            jum_balok++;
            if(cursor.getInt(1)==1){
                jml_balok_rusak_ringan++;
            }else if(cursor.getInt(1)==2){
                jml_balok_rusak_sedang++;
            }else if(cursor.getInt(1)==3){
                jml_balok_rusak_berat++;
            }
        }
        TextView jml_balok = findViewById(R.id.jml_balok);
        jml_balok.setText(a+"");
        a=0;

        cursor = LoginActivity.mSQLiteHelper.getData("SELECT id, level_kerusakan FROM data_kerusakan WHERE id_bangunan="+kod+" AND struktur= 3 ORDER BY id DESC");
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            a++;
            jum_dinding++;
            if(cursor.getInt(1)==1){
                jml_dinding_rusak_ringan++;
            }else if(cursor.getInt(1)==2){
                jml_dinding_rusak_sedang++;
            }else if(cursor.getInt(1)==3){
                jml_dinding_rusak_berat++;
            }
        }
        TextView jml_dinding = findViewById(R.id.jml_dinding);
        jml_dinding.setText(a+"");

        if(jum_kolom!=0){
            persen_kolom_rusak_ringan = (double) jml_kolom_rusak_ringan/jum_kolom*100;
            persen_kolom_rusak_sedang = (double) jml_kolom_rusak_sedang/jum_kolom*100;
            persen_kolom_rusak_berat = (double) jml_kolom_rusak_berat/jum_kolom*100;
            if(persen_kolom_rusak_ringan!=0)
                jum_gejala++;
            if(persen_kolom_rusak_sedang!=0)
                jum_gejala++;
            if(persen_kolom_rusak_berat!=0)
                jum_gejala++;
        }
        if(jum_balok!=0){
            persen_balok_rusak_ringan = (double) jml_balok_rusak_ringan/jum_balok*100;
            persen_balok_rusak_sedang = (double) jml_balok_rusak_sedang/jum_balok*100;
            persen_balok_rusak_berat = (double) jml_balok_rusak_berat/jum_balok*100;
            if(persen_balok_rusak_ringan!=0)
                jum_gejala++;
            if(persen_balok_rusak_sedang!=0)
                jum_gejala++;
            if(persen_balok_rusak_berat!=0)
                jum_gejala++;
        }
        if(jum_dinding!=0){
            persen_dinding_rusak_ringan = (double) jml_dinding_rusak_ringan/jum_dinding*100;
            persen_dinding_rusak_sedang = (double) jml_dinding_rusak_sedang/jum_dinding*100;
            persen_dinding_rusak_berat = (double) jml_dinding_rusak_berat/jum_dinding*100;
            if(persen_dinding_rusak_ringan!=0)
                jum_gejala++;
            if(persen_dinding_rusak_sedang!=0)
                jum_gejala++;
            if(persen_dinding_rusak_berat!=0)
                jum_gejala++;
        }


        System.out.println("Kolom: "+jum_kolom+", ringan: "+jml_kolom_rusak_ringan+
                "("+persen_kolom_rusak_ringan+"%), sedang: "+jml_kolom_rusak_sedang+"("+persen_kolom_rusak_sedang+"%), berat: "+jml_kolom_rusak_berat+"("+persen_kolom_rusak_berat+"%)");
        System.out.println("Balok: "+jum_balok+", ringan: "+jml_balok_rusak_ringan+
                "("+persen_balok_rusak_ringan+"%), sedang: "+jml_balok_rusak_sedang+"("+persen_balok_rusak_sedang+"%), berat: "+jml_balok_rusak_berat+"("+persen_balok_rusak_berat+"%)");
        System.out.println("Dinding: "+jum_dinding+", ringan: "+jml_dinding_rusak_ringan+
                "("+persen_dinding_rusak_ringan+"%), sedang: "+jml_dinding_rusak_sedang+"("+persen_dinding_rusak_sedang+"%), berat: "+jml_dinding_rusak_berat+"("+persen_dinding_rusak_berat+"%)");


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
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    void moveToKondisiKolom(final String id, final int a){
        Intent i = new Intent(this, KondisiStruktur.class);
        Bundle bun = new Bundle();
        System.out.println("ini sebelum "+id+" "+a);
        bun.putString("id", id);
        bun.putInt("stuk",a);
        i.putExtras(bun);
        startActivity(i);
    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        refreshText(kode);
        System.out.println("resume");
    }

    public String ambilDataLevel(int idGejala) {
        String tmpLevel="";
        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT level FROM ds_rules WHERE gejala="+idGejala);
        while (cursor.moveToNext()){
            tmpLevel+=cursor.getInt(0);
        }
        return tmpLevel;
    }

    public double ambilDataKepercayaan (int idGejala) {
        double tmpBelief = 0;
        int tmpJum = 0;
        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT cf FROM ds_rules WHERE gejala="+idGejala);
        while (cursor.moveToNext()){
            tmpBelief+=cursor.getDouble(0);
            tmpJum++;
        }

        return tmpBelief/tmpJum;
    }
    public void diagnosis(String kode){
        int i = 0;
        int jum_min=0;
        //int[] gejala_terpilih = new int[jum_gejala];
        String[] level_rusak = new String[jum_gejala];
        double[] belief = new double[jum_gejala];
        int[] gejala = new int[jum_gejala];
        if(jum_kolom!=0){
            if(persen_kolom_rusak_ringan > 0 && persen_kolom_rusak_ringan<20){
                level_rusak[i]=ambilDataLevel(1);
                belief[i]=ambilDataKepercayaan(1);
                gejala[i]=1;
                jum_min++;
                i++;
            }
            else if(persen_kolom_rusak_ringan >= 20 && persen_kolom_rusak_ringan<50){
                level_rusak[i]=ambilDataLevel(2);
                belief[i]=ambilDataKepercayaan(2);
                gejala[i]=2;
                jum_min++;
                i++;
            }
            else if(persen_kolom_rusak_ringan >= 50){
                level_rusak[i]=ambilDataLevel(3);
                belief[i]=ambilDataKepercayaan(3);
                gejala[i]=3;
                jum_min++;
                i++;
            }

            if(persen_kolom_rusak_sedang > 0 && persen_kolom_rusak_sedang<20){
                level_rusak[i]=ambilDataLevel(4);
                belief[i]=ambilDataKepercayaan(4);
                gejala[i]=4;
                jum_min++;
                i++;
            }
            else if(persen_kolom_rusak_sedang >= 20 && persen_kolom_rusak_sedang<50){
                level_rusak[i]=ambilDataLevel(5);
                belief[i]=ambilDataKepercayaan(5);
                gejala[i]=5;
                jum_min++;
                i++;
            }
            else if(persen_kolom_rusak_sedang >= 50){
                level_rusak[i]=ambilDataLevel(6);
                belief[i]=ambilDataKepercayaan(6);
                gejala[i]=6;
                jum_min++;
                i++;
            }

            if(persen_kolom_rusak_berat > 0 && persen_kolom_rusak_berat<20){
                level_rusak[i]=ambilDataLevel(7);
                belief[i]=ambilDataKepercayaan(7);
                gejala[i]=7;
                jum_min++;
                i++;
            }
            else if(persen_kolom_rusak_berat >= 20 && persen_kolom_rusak_berat<50){
                level_rusak[i]=ambilDataLevel(8);
                belief[i]=ambilDataKepercayaan(8);
                gejala[i]=8;
                jum_min++;
                i++;
            }
            else if(persen_kolom_rusak_berat >= 50){
                level_rusak[i]=ambilDataLevel(9);
                belief[i]=ambilDataKepercayaan(9);
                gejala[i]=9;
                jum_min++;
                i++;
            }
        }
        if(jum_balok!=0){
            if(persen_balok_rusak_ringan>0 && persen_balok_rusak_ringan<20){
                level_rusak[i]=ambilDataLevel(10);
                belief[i]=ambilDataKepercayaan(10);
                gejala[i]=10;
                jum_min++;
                i++;
            }
            else if(persen_balok_rusak_ringan>=20 && persen_balok_rusak_ringan<50){
                level_rusak[i]=ambilDataLevel(11);
                belief[i]=ambilDataKepercayaan(11);
                gejala[i]=11;
                jum_min++;
                i++;
            }
            else if(persen_balok_rusak_ringan>= 50){
                level_rusak[i]=ambilDataLevel(12);
                belief[i]=ambilDataKepercayaan(12);
                gejala[i]=12;
                jum_min++;
                i++;
            }

            if(persen_balok_rusak_sedang>0 && persen_balok_rusak_sedang<20){
                level_rusak[i]=ambilDataLevel(13);
                belief[i]=ambilDataKepercayaan(13);
                gejala[i]=13;
                jum_min++;
                i++;
            }
            else if(persen_balok_rusak_sedang>=20 && persen_balok_rusak_sedang<50){
                level_rusak[i]=ambilDataLevel(14);
                belief[i]=ambilDataKepercayaan(14);
                gejala[i]=14;
                jum_min++;
                i++;
            }
            else if(persen_balok_rusak_sedang>= 50){
                level_rusak[i]=ambilDataLevel(15);
                belief[i]=ambilDataKepercayaan(15);
                gejala[i]=15;
                jum_min++;
                i++;
            }

            if(persen_balok_rusak_berat>0 && persen_balok_rusak_berat<20){
                level_rusak[i]=ambilDataLevel(16);
                belief[i]=ambilDataKepercayaan(16);
                gejala[i]=16;
                jum_min++;
                i++;
            }
            else if(persen_balok_rusak_berat>=20 && persen_balok_rusak_berat<50){
                level_rusak[i]=ambilDataLevel(17);
                belief[i]=ambilDataKepercayaan(17);
                gejala[i]=17;
                jum_min++;
                i++;
            }
            else if(persen_balok_rusak_berat>= 50){
                level_rusak[i]=ambilDataLevel(18);
                belief[i]=ambilDataKepercayaan(18);
                gejala[i]=18;
                jum_min++;
                i++;
            }
        }
        if(jum_dinding!=0){
            if(persen_dinding_rusak_ringan>0 && persen_dinding_rusak_ringan<20){
                level_rusak[i]=ambilDataLevel(19);
                belief[i]=ambilDataKepercayaan(19);
                gejala[i]=19;
                jum_min++;
                i++;
            }
            else if(persen_dinding_rusak_ringan>=20 && persen_dinding_rusak_ringan<50){
                level_rusak[i]=ambilDataLevel(20);
                belief[i]=ambilDataKepercayaan(20);
                gejala[i]=20;
                jum_min++;
                i++;
            }
            else if(persen_dinding_rusak_ringan>= 50){
                level_rusak[i]=ambilDataLevel(21);
                belief[i]=ambilDataKepercayaan(21);
                gejala[i]=21;
                jum_min++;
                i++;
            }

            if(persen_dinding_rusak_sedang>0 && persen_dinding_rusak_sedang<20){
                level_rusak[i]=ambilDataLevel(22);
                belief[i]=ambilDataKepercayaan(22);
                gejala[i]=22;
                jum_min++;
                i++;
            }
            else if(persen_dinding_rusak_sedang>=20 && persen_dinding_rusak_sedang<50){
                level_rusak[i]=ambilDataLevel(23);
                belief[i]=ambilDataKepercayaan(23);
                gejala[i]=23;
                jum_min++;
                i++;
            }
            else if(persen_dinding_rusak_sedang>= 50){
                level_rusak[i]=ambilDataLevel(24);
                belief[i]=ambilDataKepercayaan(24);
                gejala[i]=24;
                jum_min++;
                i++;
            }

            if(persen_dinding_rusak_berat>0 && persen_dinding_rusak_berat<20){
                level_rusak[i]=ambilDataLevel(25);
                belief[i]=ambilDataKepercayaan(25);
                gejala[i]=25;
                jum_min++;
                i++;
            }
            else if(persen_dinding_rusak_berat>=20 && persen_dinding_rusak_berat<50){
                level_rusak[i]=ambilDataLevel(26);
                belief[i]=ambilDataKepercayaan(26);
                gejala[i]=26;
                jum_min++;
                i++;
            }
            else if(persen_dinding_rusak_berat>= 50){
                level_rusak[i]=ambilDataLevel(27);
                belief[i]=ambilDataKepercayaan(27);
                gejala[i]=27;
                jum_min++;
                i++;
            }
        }
        System.out.println("daftar gejalaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        for (int a=0;a<level_rusak.length;a++){
            System.out.println(gejala[a]+": "+level_rusak[a]+" -> "+belief[a]);
        }

        if(jum_min>=2){
            double[] m1,m2;
            String[] mm1,mm2;
            m1 = new double[2];
            mm1 = new String[2];
            m2 = new double[2];
            mm2 = new String[2];

            m1[0] = belief[0];
            m1[1] = 1-belief[0];
            m2[0] = belief[1];
            m2[1] = 1-belief[1];

            mm1[0] = level_rusak[0];
            mm1[1] = "-";
            mm2[0] = level_rusak[1];
            mm2[1] = "-";
            boolean pertama=true;
            for(int a = 1; a<jum_gejala;a++){
                boolean status_k=false;
                double[][] tmp = new double[m1.length][2];
                String[][] tmpS = new String[m1.length][2];
                System.out.println("Densitas baru "+a);
                for(int b =0; b<m1.length; b++){
                    tmpS[b][0]="";
                    tmpS[b][1]="";
                    tmp[b][0]=m1[b]*m2[0];
                    tmp[b][1]=m1[b]*m2[1];
                    if(mm1[b].equals(mm2[0])){
                        tmpS[b][0]=mm1[b];
                    }
                    else if(mm1[b].equals("-") && mm2[0].equals("-")==false){
                        tmpS[b][0] = mm2[0];
                    }
//                else if(mm1[b]=="-" && mm2[0]=="-"){
//                    tmpS[b][0] = "-";
//                }
                    else if(mm1[b].equals(mm2[0])==false){
                        int satu=0;
                        int dua=0;
                        int tiga=0;
                        String status = mm1[b]+mm2[0];
                        char[] arrHuruf = status.toCharArray();
                        for(int x=0; x<arrHuruf.length; x++){
                            if(arrHuruf[x]=='1'){
                                satu++;
                            }
                            else if(arrHuruf[x]=='2'){
                                dua++;
                            }
                            else if(arrHuruf[x]=='3'){
                                tiga++;
                            }
                        }
                        if(satu>1){
                            tmpS[b][0]+="1";
                        }
                        if(dua>1){
                            tmpS[b][0]+="2";
                        }
                        if(tiga>1){
                            tmpS[b][0]+="3";
                        }
                        if(satu<=1 && dua<=1 && tiga<=1){
                            tmpS[b][0]="-";
                            status_k=true;
                        }
                    }

                    if(mm1[b].equals(mm2[1])){
                        tmpS[b][1]="-";
                    }
                    else{
                        tmpS[b][1]=mm1[b];
                    }

                    System.out.println(tmpS[b][0]+": "+tmp[b][0]+" "+tmpS[b][1]+": "+tmp[b][1]);
                }
                String[] persamaanS = new String[1];
                double[] persamaan = new double[1];
                int hitung=0;
                for(int x=0;x<m1.length; x++){
                    for(int y=0;y<2;y++){
                        persamaan[hitung]=tmp[x][y];
                        persamaanS[hitung]=tmpS[x][y];
                        hitung++;
                        double[] temp = new double[persamaan.length]; // var. array sementara
                        String[] tempS = new String[persamaanS.length]; // var. array sementara
                        System.arraycopy(persamaan,0,temp,0,persamaan.length); // copy array
                        System.arraycopy(persamaanS,0,tempS,0,persamaanS.length); // copy array
                        persamaan = new double[hitung+1]; // deklarasi ulang array
                        persamaanS = new String[hitung+1]; // deklarasi ulang array
                        System.arraycopy(temp,0,persamaan,0,temp.length); // copy array
                        System.arraycopy(tempS,0,persamaanS,0,tempS.length); // copy array
                    }
                }
                System.out.println("merubah matrik jadi vektor");
                for(int x=0;x<persamaanS.length-1;x++){
                    System.out.print(persamaanS[x]+"("+persamaan[x]+"), ");
                }
                System.out.println();

                double[] tampung = new double[1];
                String[] tampungS = new String[1];
                int count=0;
                tampungS[0]=persamaanS[0];
                for(int x=0;x<persamaanS.length-2;x++){
                    for(int y=x+1;y<persamaanS.length-2;y++){
                        if(persamaanS[x].equals(persamaanS[y])){
                            persamaanS[y]="null";
                            persamaan[x]+=persamaan[y];
                            persamaan[y]=0;
                        }
                    }
                }
                System.out.println("mengumpukan variabel yg sama");
                for(int x=0;x<persamaanS.length-1;x++){
                    System.out.print(persamaanS[x]+"("+persamaan[x]+"), ");
                }
                System.out.println();

                if(status_k==true){
                    double kk=0;
                    for(int x=0;x<persamaanS.length-2;x++){
                        if(persamaanS[x].equals("-")){
                            persamaanS[x]="null";
                            kk+=persamaan[x];
                            persamaan[x]=0;
                        }
                    }
                    System.out.println("kkkkkkkkkk "+kk);
                    for(int x=0;x<persamaanS.length-2;x++){
                        if(persamaanS[x].equals("null")==false){
                            try{
                                persamaan[x]=persamaan[x]/(1-kk);
                            }
                            catch (Exception e){
                                persamaan[x]=0;
                            }

                        }
                    }

                }
                System.out.println("hasil akhir");

                for(int x=0;x<persamaanS.length-1;x++){
                    System.out.print(persamaanS[x]+"("+persamaan[x]+"), ");
                }
                System.out.println();

                double[] nextDensitas = new double[1];
                String[] nextDensitasS= new String[1];
                for(int x=0;x<persamaanS.length-2;x++){
                    if(persamaanS[x].equals("null")==false){
                        nextDensitas[0]=persamaan[x];
                        nextDensitasS[0]=persamaanS[x];
                        break;
                    }
                }
                int hit=1;
                for(int x=0;x<persamaanS.length-2;x++){
                    if(nextDensitasS[hit-1]!=persamaanS[x] && persamaanS[x].equals("null")==false){
                        double[] temp = new double[nextDensitas.length];
                        String[] tempS = new String[nextDensitasS.length]; // var. array sementara
                        System.arraycopy(nextDensitas,0,temp,0,nextDensitas.length); // copy array
                        System.arraycopy(nextDensitasS,0,tempS,0,nextDensitasS.length); // copy array
                        nextDensitas= new double[hit+1]; // deklarasi ulang array
                        nextDensitasS = new String[hit+1]; // deklarasi ulang array
                        System.arraycopy(temp,0,nextDensitas,0,temp.length); // copy array
                        System.arraycopy(tempS,0,nextDensitasS,0,tempS.length); // copy array
                        nextDensitas[hit]=persamaan[x];
                        nextDensitasS[hit]=persamaanS[x];
                        hit++;
                    }
                }
                hit=nextDensitasS.length;

                double[] temp = new double[nextDensitas.length]; // var. array sementara
                String[] tempS = new String[nextDensitasS.length]; // var. array sementara
                System.arraycopy(nextDensitas,0,temp,0,nextDensitas.length); // copy array
                System.arraycopy(nextDensitasS,0,tempS,0,nextDensitasS.length); // copy array
                nextDensitas = new double[hit+1]; // deklarasi ulang array
                nextDensitasS = new String[hit+1]; // deklarasi ulang array
                System.arraycopy(temp,0,nextDensitas,0,temp.length); // copy array
                System.arraycopy(tempS,0,nextDensitasS,0,tempS.length); // copy array
                nextDensitasS[hit]="-";
                double count2=0;
                for(int x=0;x<nextDensitas.length-1;x++){
                    count2+=nextDensitas[x];
                }
                nextDensitas[hit]=1-count2;

                pertama=false;
                for(int x=0;x<nextDensitasS.length;x++){
                    System.out.print(nextDensitasS[x]+"("+nextDensitas[x]+"), ");
                }
                System.out.println("hhhhhhhhh");

                m1 = new double[hit+1];
                mm1 = new String[hit+1];
                System.arraycopy(nextDensitas,0,m1,0,nextDensitas.length); // copy array
                System.arraycopy(nextDensitasS,0,mm1,0,nextDensitasS.length); // copy array

                if(a+1!=jum_gejala){
                    m2[0] = belief[a+1];
                    m2[1] = 1-belief[a+1];
                    mm2[0] = level_rusak[a+1];
                    mm2[1] = "-";
                }

            }
            double max=0;
            String maxS="";
            for(int x=0;x<m1.length-1;x++){
                if(max<m1[x]){
                    max=m1[x];
                    maxS=mm1[x];
                }
            }
            LoginActivity.mSQLiteHelper.updateDataLevel(maxS,max, kode);

            if(maxS.equals("1")){
                System.out.println("Kerusakan adalah rusak ringan dengan probabilitas "+max);
            }
            else if(maxS.equals("2")){
                System.out.println("Kerusakan adalah rusak sedang dengan probabilitas "+max);
            }
            else if(maxS.equals("3")){
                System.out.println("Kerusakan adalah rusak berat dengan probabilitas "+max);
            }
            else if(maxS.equals("12")){
                System.out.println("Kerusakan adalah rusak ringan dan sedang dengan probabilitas "+max);
            }
            else if(maxS.equals("23")) {
                System.out.println("Kerusakan adalah rusak sedang dan berat dengan probabilitas " + max);
            }

            Intent n = new Intent(this, HasilDiagnosisActivity.class);
            Bundle bun = new Bundle();
            bun.putString("id", kode);
//        bun.putString("level", maxS);
//        bun.putDouble("persen", max);
            n.putExtras(bun);
            startActivity(n);
        }
        else if (jum_min==1){
            LoginActivity.mSQLiteHelper.updateDataLevel(level_rusak[0],belief[0], kode);
            Intent n = new Intent(this, HasilDiagnosisActivity.class);
            Bundle bun = new Bundle();
            bun.putString("id", kode);
            n.putExtras(bun);
            startActivity(n);
        }
        else{
            Toast.makeText(this, "Data struktur tidak lengkap", Toast.LENGTH_SHORT).show();
        }


    }

//    public void diagnosis(int kode){
//        int i = 0;
//        int jum_min=0;
//        //int[] gejala_terpilih = new int[jum_gejala];
//        String[] level_rusak = new String[jum_gejala];
//        double[] belief = new double[jum_gejala];
//        if(jum_kolom!=0){
//            if(persen_kolom_rusak_ringan > 0 && persen_kolom_rusak_ringan<20){
//                level_rusak[i]="1";
//                belief[i]=0.30;
//                jum_min++;
//                i++;
//            }
//            else if(persen_kolom_rusak_ringan >= 20 && persen_kolom_rusak_ringan<50){
//                level_rusak[i]="12";
//                belief[i]=0.60;
//                jum_min++;
//                i++;
//            }
//            else if(persen_kolom_rusak_ringan >= 50){
//                //gejala_terpilih[i]=1;
//                level_rusak[i]="2";
//                belief[i]=0.80;
//                jum_min++;
//                i++;
//            }
//
//            if(persen_kolom_rusak_sedang > 0 && persen_kolom_rusak_sedang<20){
//                level_rusak[i]="12";
//                belief[i]=0.35;
//                jum_min++;
//                i++;
//            }
//            else if(persen_kolom_rusak_sedang >= 20 && persen_kolom_rusak_sedang<50){
//                level_rusak[i]="2";
//                belief[i]=0.65;
//                jum_min++;
//                i++;
//            }
//            else if(persen_kolom_rusak_sedang >= 50){
//                //gejala_terpilih[i]=1;
//                level_rusak[i]="2";
//                belief[i]=0.90;
//                jum_min++;
//                i++;
//            }
//
//            if(persen_kolom_rusak_berat > 0 && persen_kolom_rusak_berat<20){
//                level_rusak[i]="23";
//                belief[i]=0.40;
//                jum_min++;
//                i++;
//            }
//            else if(persen_kolom_rusak_berat >= 20 && persen_kolom_rusak_berat<50){
//                level_rusak[i]="3";
//                belief[i]=0.70;
//                jum_min++;
//                i++;
//            }
//            else if(persen_kolom_rusak_berat >= 50){
//                //gejala_terpilih[i]=1;
//                level_rusak[i]="3";
//                belief[i]=0.95;
//                jum_min++;
//                i++;
//            }
//        }
//        if(jum_balok!=0){
//            if(persen_balok_rusak_ringan>0 && persen_balok_rusak_ringan<20){
//                level_rusak[i]="1";
//                belief[i]=0.30;
//                jum_min++;
//                i++;
//            }
//            else if(persen_balok_rusak_ringan>=20 && persen_balok_rusak_ringan<50){
//                level_rusak[i]="12";
//                belief[i]=0.45;
//                jum_min++;
//                i++;
//            }
//            else if(persen_balok_rusak_ringan>= 50){
//                level_rusak[i]="2";
//                belief[i]=0.65;
//                jum_min++;
//                i++;
//            }
//
//            if(persen_balok_rusak_sedang>0 && persen_balok_rusak_sedang<20){
//                level_rusak[i]="12";
//                belief[i]=0.35;
//                jum_min++;
//                i++;
//            }
//            else if(persen_balok_rusak_sedang>=20 && persen_balok_rusak_sedang<50){
//                level_rusak[i]="2";
//                belief[i]=0.60;
//                jum_min++;
//                i++;
//            }
//            else if(persen_balok_rusak_sedang>= 50){
//                level_rusak[i]="2";
//                belief[i]=0.80;
//                jum_min++;
//                i++;
//            }
//
//            if(persen_balok_rusak_berat>0 && persen_balok_rusak_berat<20){
//                level_rusak[i]="23";
//                belief[i]=0.35;
//                jum_min++;
//                i++;
//            }
//            else if(persen_balok_rusak_berat>=20 && persen_balok_rusak_berat<50){
//                level_rusak[i]="3";
//                belief[i]=0.65;
//                jum_min++;
//                i++;
//            }
//            else if(persen_balok_rusak_berat>= 50){
//                level_rusak[i]="3";
//                belief[i]=0.85;
//                jum_min++;
//                i++;
//            }
//        }
//        if(jum_dinding!=0){
//            if(persen_dinding_rusak_ringan>0 && persen_dinding_rusak_ringan<20){
//                level_rusak[i]="1";
//                belief[i]=0.30;
//                jum_min++;
//                i++;
//            }
//            else if(persen_dinding_rusak_ringan>=20 && persen_dinding_rusak_ringan<50){
//                level_rusak[i]="1";
//                belief[i]=0.45;
//                jum_min++;
//                i++;
//            }
//            else if(persen_dinding_rusak_ringan>= 50){
//                level_rusak[i]="12";
//                belief[i]=0.65;
//                jum_min++;
//                i++;
//            }
//
//            if(persen_dinding_rusak_sedang>0 && persen_dinding_rusak_sedang<20){
//                level_rusak[i]="1";
//                belief[i]=0.30;
//                jum_min++;
//                i++;
//            }
//            else if(persen_dinding_rusak_sedang>=20 && persen_dinding_rusak_sedang<50){
//                level_rusak[i]="12";
//                belief[i]=0.55;
//                jum_min++;
//                i++;
//            }
//            else if(persen_dinding_rusak_sedang>= 50){
//                level_rusak[i]="2";
//                belief[i]=0.65;
//                jum_min++;
//                i++;
//            }
//
//            if(persen_dinding_rusak_berat>0 && persen_dinding_rusak_berat<20){
//                level_rusak[i]="12";
//                belief[i]=0.35;
//                jum_min++;
//                i++;
//            }
//            else if(persen_dinding_rusak_berat>=20 && persen_dinding_rusak_berat<50){
//                level_rusak[i]="12";
//                belief[i]=0.55;
//                jum_min++;
//                i++;
//            }
//            else if(persen_dinding_rusak_berat>= 50){
//                level_rusak[i]="23";
//                belief[i]=0.68;
//                jum_min++;
//                i++;
//            }
//        }
//        if(jum_min>=2){
//            double[] m1,m2;
//            String[] mm1,mm2;
//            m1 = new double[2];
//            mm1 = new String[2];
//            m2 = new double[2];
//            mm2 = new String[2];
//
//            m1[0] = belief[0];
//            m1[1] = 1-belief[0];
//            m2[0] = belief[1];
//            m2[1] = 1-belief[1];
//
//            mm1[0] = level_rusak[0];
//            mm1[1] = "-";
//            mm2[0] = level_rusak[1];
//            mm2[1] = "-";
//            boolean pertama=true;
//            for(int a = 1; a<jum_gejala;a++){
//                boolean status_k=false;
//                double[][] tmp = new double[m1.length][2];
//                String[][] tmpS = new String[m1.length][2];
//                System.out.println("Densitas baru "+a);
//                for(int b =0; b<m1.length; b++){
//                    tmpS[b][0]="";
//                    tmpS[b][1]="";
//                    tmp[b][0]=m1[b]*m2[0];
//                    tmp[b][1]=m1[b]*m2[1];
//                    if(mm1[b].equals(mm2[0])){
//                        tmpS[b][0]=mm1[b];
//                    }
//                    else if(mm1[b].equals("-") && mm2[0].equals("-")==false){
//                        tmpS[b][0] = mm2[0];
//                    }
////                else if(mm1[b]=="-" && mm2[0]=="-"){
////                    tmpS[b][0] = "-";
////                }
//                    else if(mm1[b].equals(mm2[0])==false){
//                        int satu=0;
//                        int dua=0;
//                        int tiga=0;
//                        String status = mm1[b]+mm2[0];
//                        char[] arrHuruf = status.toCharArray();
//                        for(int x=0; x<arrHuruf.length; x++){
//                            if(arrHuruf[x]=='1'){
//                                satu++;
//                            }
//                            else if(arrHuruf[x]=='2'){
//                                dua++;
//                            }
//                            else if(arrHuruf[x]=='3'){
//                                tiga++;
//                            }
//                        }
//                        if(satu>1){
//                            tmpS[b][0]+="1";
//                        }
//                        if(dua>1){
//                            tmpS[b][0]+="2";
//                        }
//                        if(tiga>1){
//                            tmpS[b][0]+="3";
//                        }
//                        if(satu<=1 && dua<=1 && tiga<=1){
//                            tmpS[b][0]="-";
//                            status_k=true;
//                        }
//                    }
//
//                    if(mm1[b].equals(mm2[1])){
//                        tmpS[b][1]="-";
//                    }
//                    else{
//                        tmpS[b][1]=mm1[b];
//                    }
//
//                    System.out.println(tmpS[b][0]+": "+tmp[b][0]+" "+tmpS[b][1]+": "+tmp[b][1]);
//                }
//                String[] persamaanS = new String[1];
//                double[] persamaan = new double[1];
//                int hitung=0;
//                for(int x=0;x<m1.length; x++){
//                    for(int y=0;y<2;y++){
//                        persamaan[hitung]=tmp[x][y];
//                        persamaanS[hitung]=tmpS[x][y];
//                        hitung++;
//                        double[] temp = new double[persamaan.length]; // var. array sementara
//                        String[] tempS = new String[persamaanS.length]; // var. array sementara
//                        System.arraycopy(persamaan,0,temp,0,persamaan.length); // copy array
//                        System.arraycopy(persamaanS,0,tempS,0,persamaanS.length); // copy array
//                        persamaan = new double[hitung+1]; // deklarasi ulang array
//                        persamaanS = new String[hitung+1]; // deklarasi ulang array
//                        System.arraycopy(temp,0,persamaan,0,temp.length); // copy array
//                        System.arraycopy(tempS,0,persamaanS,0,tempS.length); // copy array
//                    }
//                }
//                System.out.println("merubah matrik jadi vektor");
//                for(int x=0;x<persamaanS.length-1;x++){
//                    System.out.print(persamaanS[x]+"("+persamaan[x]+"), ");
//                }
//                System.out.println();
//
//                double[] tampung = new double[1];
//                String[] tampungS = new String[1];
//                int count=0;
//                tampungS[0]=persamaanS[0];
//                for(int x=0;x<persamaanS.length-2;x++){
//                    for(int y=x+1;y<persamaanS.length-2;y++){
//                        if(persamaanS[x].equals(persamaanS[y])){
//                            persamaanS[y]="null";
//                            persamaan[x]+=persamaan[y];
//                            persamaan[y]=0;
//                        }
//                    }
//                }
//                System.out.println("mengumpukan variabel yg sama");
//                for(int x=0;x<persamaanS.length-1;x++){
//                    System.out.print(persamaanS[x]+"("+persamaan[x]+"), ");
//                }
//                System.out.println();
//
//                if(status_k==true){
//                    double kk=0;
//                    for(int x=0;x<persamaanS.length-2;x++){
//                        if(persamaanS[x].equals("-")){
//                            persamaanS[x]="null";
//                            kk+=persamaan[x];
//                            persamaan[x]=0;
//                        }
//                    }
//                    System.out.println("kkkkkkkkkk "+kk);
//                    for(int x=0;x<persamaanS.length-2;x++){
//                        if(persamaanS[x].equals("null")==false){
//                            try{
//                                persamaan[x]=persamaan[x]/(1-kk);
//                            }
//                            catch (Exception e){
//                                persamaan[x]=0;
//                            }
//
//                        }
//                    }
//
//                }
//                System.out.println("hasil akhir");
//
//                for(int x=0;x<persamaanS.length-1;x++){
//                    System.out.print(persamaanS[x]+"("+persamaan[x]+"), ");
//                }
//                System.out.println();
//
//                double[] nextDensitas = new double[1];
//                String[] nextDensitasS= new String[1];
//                for(int x=0;x<persamaanS.length-2;x++){
//                    if(persamaanS[x].equals("null")==false){
//                        nextDensitas[0]=persamaan[x];
//                        nextDensitasS[0]=persamaanS[x];
//                        break;
//                    }
//                }
//                int hit=1;
//                for(int x=0;x<persamaanS.length-2;x++){
//                    if(nextDensitasS[hit-1]!=persamaanS[x] && persamaanS[x].equals("null")==false){
//                        double[] temp = new double[nextDensitas.length];
//                        String[] tempS = new String[nextDensitasS.length]; // var. array sementara
//                        System.arraycopy(nextDensitas,0,temp,0,nextDensitas.length); // copy array
//                        System.arraycopy(nextDensitasS,0,tempS,0,nextDensitasS.length); // copy array
//                        nextDensitas= new double[hit+1]; // deklarasi ulang array
//                        nextDensitasS = new String[hit+1]; // deklarasi ulang array
//                        System.arraycopy(temp,0,nextDensitas,0,temp.length); // copy array
//                        System.arraycopy(tempS,0,nextDensitasS,0,tempS.length); // copy array
//                        nextDensitas[hit]=persamaan[x];
//                        nextDensitasS[hit]=persamaanS[x];
//                        hit++;
//                    }
//                }
//                hit=nextDensitasS.length;
//
//                double[] temp = new double[nextDensitas.length]; // var. array sementara
//                String[] tempS = new String[nextDensitasS.length]; // var. array sementara
//                System.arraycopy(nextDensitas,0,temp,0,nextDensitas.length); // copy array
//                System.arraycopy(nextDensitasS,0,tempS,0,nextDensitasS.length); // copy array
//                nextDensitas = new double[hit+1]; // deklarasi ulang array
//                nextDensitasS = new String[hit+1]; // deklarasi ulang array
//                System.arraycopy(temp,0,nextDensitas,0,temp.length); // copy array
//                System.arraycopy(tempS,0,nextDensitasS,0,tempS.length); // copy array
//                nextDensitasS[hit]="-";
//                double count2=0;
//                for(int x=0;x<nextDensitas.length-1;x++){
//                    count2+=nextDensitas[x];
//                }
//                nextDensitas[hit]=1-count2;
//
//                pertama=false;
//                for(int x=0;x<nextDensitasS.length;x++){
//                    System.out.print(nextDensitasS[x]+"("+nextDensitas[x]+"), ");
//                }
//                System.out.println("hhhhhhhhh");
//
//                m1 = new double[hit+1];
//                mm1 = new String[hit+1];
//                System.arraycopy(nextDensitas,0,m1,0,nextDensitas.length); // copy array
//                System.arraycopy(nextDensitasS,0,mm1,0,nextDensitasS.length); // copy array
//
//                if(a+1!=jum_gejala){
//                    m2[0] = belief[a+1];
//                    m2[1] = 1-belief[a+1];
//                    mm2[0] = level_rusak[a+1];
//                    mm2[1] = "-";
//                }
//
//            }
//            double max=0;
//            String maxS="";
//            for(int x=0;x<m1.length-1;x++){
//                if(max<m1[x]){
//                    max=m1[x];
//                    maxS=mm1[x];
//                }
//            }
//            LoginActivity.mSQLiteHelper.updateDataLevel(maxS,max, kode);
//
//            if(maxS.equals("1")){
//                System.out.println("Kerusakan adalah rusak ringan dengan probabilitas "+max);
//            }
//            else if(maxS.equals("2")){
//                System.out.println("Kerusakan adalah rusak sedang dengan probabilitas "+max);
//            }
//            else if(maxS.equals("3")){
//                System.out.println("Kerusakan adalah rusak berat dengan probabilitas "+max);
//            }
//            else if(maxS.equals("12")){
//                System.out.println("Kerusakan adalah rusak ringan dan sedang dengan probabilitas "+max);
//            }
//            else if(maxS.equals("23")) {
//                System.out.println("Kerusakan adalah rusak sedang dan berat dengan probabilitas " + max);
//            }
//
//            Intent n = new Intent(this, HasilDiagnosisActivity.class);
//            Bundle bun = new Bundle();
//            bun.putInt("id", kode);
////        bun.putString("level", maxS);
////        bun.putDouble("persen", max);
//            n.putExtras(bun);
//            startActivity(n);
//        }
//        else if (jum_min==1){
//            LoginActivity.mSQLiteHelper.updateDataLevel(level_rusak[0],belief[0], kode);
//            Intent n = new Intent(this, HasilDiagnosisActivity.class);
//            Bundle bun = new Bundle();
//            bun.putInt("id", kode);
//            n.putExtras(bun);
//            startActivity(n);
//        }
//        else{
//            Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
}
