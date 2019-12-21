package com.mhaa98.crackanalyzer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button up;
    ImageButton gantiPin;

    ListView mListView;
    ArrayList<Model> mList;
    HistoryListAdapter mAdapter = null;

    final int CAMERA_REQUEST_CODE1 = 1;
    final int CAMERA_REQUEST_CODE2 = 0;

    ImageView imageViewIcon;
    TextView addInstance;



    double tmpx, tmpy;
    int panjang, lebar, tengahx, tengahy;

    double d1, d2, d3, d4, dd1, dd2, dd3, dd4;
    String data_txt;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        up = findViewById(R.id.upload_btn);
        up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (haveNetwork()){
                    Cursor cc = LoginActivity.mSQLiteHelper.getData("SELECT COUNT(id) FROM data_bangunan WHERE tingkat_kepercayaan !=0");
                    int jumData = 0;
                    while (cc.moveToNext()){
                        jumData = cc.getInt(0);
                    }
                    if (jumData == 0)
                        Toast.makeText(MainActivity.this, "Tidak ada yang bisa diupload", Toast.LENGTH_SHORT).show();
                    else if (jumData > 0){

                        String instanceLokal="";
                        Cursor c = LoginActivity.mSQLiteHelper.getData("SELECT instance FROM data_instance");
                        while (c.moveToNext()){
                            instanceLokal = c.getString(0);
                        }
                        final String instanceLokal2=instanceLokal;

                        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<List<InstanceModel>> call = apiInterface.getInstance();
                        call.enqueue(new Callback<List<InstanceModel>>() {
                            @Override
                            public void onResponse(Call<List<InstanceModel>> call, Response<List<InstanceModel>> response) {
                                List<InstanceModel> instanceModel = response.body();
                                boolean ada = false;
                                for(InstanceModel im:instanceModel) {
                                    if (instanceLokal2.equals(im.getInstance())) {
                                        ada = true;
                                        pupupUpload();
                                        break;
                                    }
                                }
                                if (ada == false){
                                    Toast.makeText(MainActivity.this, "Instance tidak ditemukan", Toast.LENGTH_SHORT).show();
                                }

                            }
                            @Override
                            public void onFailure(Call<List<InstanceModel>> call, Throwable t) {
                            }
                        });
                    }
                }
                else if(!haveNetwork()){
                    Toast.makeText(MainActivity.this, "Tidak ada koneksi", Toast.LENGTH_SHORT).show();;
                }
            }
        });
        gantiPin = findViewById(R.id.convert);
        gantiPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GantiPin2Activity.class);
                startActivity(i);
            }
        });


        mListView = findViewById(R.id.list1);
        mList = new ArrayList<>();
        mAdapter = new HistoryListAdapter(this, R.layout.history_layout, mList);
        mListView.setAdapter(mAdapter);

        //mSQLiteHelper.getReadableDatabase();
        //GET ALL DATA FROM SQLITE
        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT id, nama_bangunan, jumlah_lantai, tahun, alamat_bangunan,latitude, " +
                "longitude, nama, alamat, nomor_hp, tingkat_kepercayaan FROM data_bangunan ORDER BY id DESC");
        mList.clear();
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            String nama_b = cursor.getString(1);
            String lantai = cursor.getString(2);
            String thn = cursor.getString(3);
            String alamat_b = cursor.getString(4);
            String lati = cursor.getString(5);
            String longi = cursor.getString(6);
            //byte[] poto = cursor.getBlob(7);
            String nama = cursor.getString(7);
            String alamat = cursor.getString(8);
            String nomor = cursor.getString(9);
            double kepercayaan = cursor.getDouble(10);

            mList.add(new Model(id, nama_b, lantai, thn, alamat_b,lati, longi, nama, alamat, nomor,kepercayaan));

        }
        mAdapter.notifyDataSetChanged();

        TextView pesan = findViewById(R.id.pesan);
        ImageView imgPesan = findViewById(R.id.pesan_img);

        pesan.setVisibility(View.GONE);
        imgPesan.setVisibility(View.GONE);
        if(mList.size()==0){
            pesan.setVisibility(View.VISIBLE);
            imgPesan.setVisibility(View.VISIBLE);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = LoginActivity.mSQLiteHelper.getData("SELECT id, tingkat_kepercayaan FROM data_bangunan ORDER BY id DESC");
                ArrayList<String> arrID = new ArrayList<String>();
                ArrayList<Double> arrKepercayaan = new ArrayList<Double>();
                while (c.moveToNext()){
                    arrID.add(c.getString(0));
                    arrKepercayaan.add(c.getDouble(1));
                }

                if(arrKepercayaan.get(position)==0) {
                    moveToDiagonis(arrID.get(position));
                }
                else{
                    moveToHasilDiagnosis(arrID.get(position));
                }

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("Pilih sebuah aksi");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            Cursor c = LoginActivity.mSQLiteHelper.getData("SELECT id FROM data_bangunan ORDER BY id DESC");
                            ArrayList<String> arrID = new ArrayList<String>();
                            while (c.moveToNext()){
                                arrID.add(c.getString(0));
                            }
                            moveToUpdate(arrID.get(position));

                            //showDialogUpdate(MainActivity.this, arrID.get(position));
                        }
                        if (which == 1){
                            Cursor c = LoginActivity.mSQLiteHelper.getData("SELECT id FROM data_bangunan ORDER BY id DESC");
                            ArrayList<String> arrID = new ArrayList<String>();
                            while (c.moveToNext()){
                                arrID.add(c.getString(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

        ImageButton btn_add = (ImageButton) findViewById(R.id.add_btn);
        btn_add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this,FormActivity.class);
                startActivity(i);
            }
        });


        addInstance = findViewById(R.id.instance_btn);
        addInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cariInstance();
            }
        });

        Cursor in_c = LoginActivity.mSQLiteHelper.getData("SELECT instance FROM data_instance");
        while (in_c.moveToNext()){
            if (in_c.getString(0).equals("kosong")==false){
                String txt = in_c.getString(0);
                addInstance.setText(txt);
            }
            else{
                addInstance.setText("Cari Instance");
            }

        }
    }


    private void cariInstance() {
        Intent i = new Intent(this,InstanceActivity.class);
        startActivity(i);
    }

    private void pupupUpload() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);
        dialogDelete.setTitle("Yakin diupload?");
        dialogDelete.setMessage("Data bangunan yang akan diupload adalah data yang sudah didiagnosis");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                upload();
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

    private void upload(){
        Cursor jumdata = LoginActivity.mSQLiteHelper.getData("SELECT COUNT(id) FROM data_bangunan WHERE tingkat_kepercayaan !=0");
        int jum=0;
        while (jumdata.moveToNext()){
            jum=jumdata.getInt(0);
        }

        final Handler handle = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.incrementProgressBy(1); // Incremented By Value 2
            }
        };

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMax(jum); // Progress Dialog Max Value
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Sedang mengupload"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        final Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT * FROM data_bangunan WHERE tingkat_kepercayaan!=0");
        while (cursor.moveToNext()){

            final String bgn = cursor.getString(0);
            final RequestBody id_b = RequestBody.create(MediaType.parse("text/plain"), bgn);
            RequestBody nama_b = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(1));
            RequestBody lantai = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(2));
            RequestBody thn = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(3));
            RequestBody alamat_b = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(4));
            RequestBody prov = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(5));
            RequestBody kota = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(6));
            RequestBody kec = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(7));
            RequestBody pos = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(8));
            String lat = cursor.getString(9);
            String longg = cursor.getString(10);
            if(lat.equals("")||longg.equals(""))
            {   lat = "0";
                longg = "0";
            }
            RequestBody lati = RequestBody.create(MediaType.parse("text/plain"), lat);
            RequestBody longi = RequestBody.create(MediaType.parse("text/plain"), longg);
            byte[] poto = cursor.getBlob(11);
            RequestBody nama = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(12));
            RequestBody alamat = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(13));
            RequestBody nomor = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(14));
            RequestBody hasil = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(15));
            RequestBody kepercayaan = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cursor.getDouble(16)));
            RequestBody instance = RequestBody.create(MediaType.parse("text/plain"), cursor.getString(17));

            RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), poto);
            MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo",
                    cursor.getString(1), photoBody);

            //System.out.println(id+" "+nama_b+" "+lantai+" "+thn+" "+alamat_b+" "+lati+" "+longi+" "+nama+" "+alamat+" "+nomor+" "+hasil+" "+kepercayaan);

            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<ValueModel> call = apiInterface.insertData(id_b,nama_b,lantai,thn,alamat_b,prov,kota,kec,pos,lati,longi,photoPart,nama,alamat,nomor,hasil,kepercayaan,instance);
            call.enqueue(new Callback<ValueModel>() {
                @Override
                public void onResponse(Call<ValueModel> call, Response<ValueModel> response) {
                    String msg = response.body().getMessage();
                    System.out.println("Status bangunan: "+ msg);
                    System.out.println(bgn+" ininininininininininininini");
                    if(msg.equals("berhasil"))
                    {   Cursor cursorsub = LoginActivity.mSQLiteHelper.getData("SELECT * FROM data_kerusakan WHERE id_bangunan = '"+bgn+"'");
                        int count_s = 0;
                        while (cursorsub.moveToNext()){
                            count_s++;
                            String id_s = cursorsub.getString(1)+"-"+count_s;
                            RequestBody str_id = RequestBody.create(MediaType.parse("text/plain"), id_s);
                            RequestBody str_bgn = RequestBody.create(MediaType.parse("text/plain"), cursorsub.getString(1));
                            RequestBody str_str = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cursorsub.getInt(2)));
                            RequestBody str_level = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cursorsub.getInt(3)));
                            byte[] str_foto = cursorsub.getBlob(4);
                            if(str_foto==null)
                            {   ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                                Call<ValueModel> callStr = apiInterface.insertStruktur2(str_id, str_bgn, str_str, str_level);
                                callStr.enqueue(new Callback<ValueModel>() {
                                    @Override
                                    public void onResponse(Call<ValueModel> call, Response<ValueModel> response) {
                                        String msg = response.body().getMessage();
                                        System.out.println("Status: " + msg);
                                    }

                                    @Override
                                    public void onFailure(Call<ValueModel> call, Throwable t) {
                                        System.out.println("Gagal mengirim data struktur!");
                                    }
                                });
                            }
                            else {
                                RequestBody bodyFoto = RequestBody.create(MediaType.parse("image/*"), str_foto);
                                MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo",
                                        cursorsub.getString(1), bodyFoto);

                                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                                Call<ValueModel> callStr = apiInterface.insertStruktur(str_id, str_bgn, str_str, str_level, photoPart);
                                callStr.enqueue(new Callback<ValueModel>() {
                                    @Override
                                    public void onResponse(Call<ValueModel> call, Response<ValueModel> response) {
                                        String msg = response.body().getMessage();
                                        System.out.println("Status: " + msg);
                                    }

                                    @Override
                                    public void onFailure(Call<ValueModel> call, Throwable t) {
                                        System.out.println("Gagal mengirim data struktur!");
                                    }
                                });
                            }
                        }

                    }

                    handle.sendMessage(handle.obtainMessage());
                    System.out.println(cursor.getPosition()+"-----------------------------");
                    if (progressDialog.getProgress()+1 == progressDialog.getMax()) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Upload sukses", Toast.LENGTH_SHORT).show();
                        deleteDataUpload();
                    }
                }

                @Override
                public void onFailure(Call<ValueModel> call, Throwable t) {
                    System.out.println("Gagal mengirim data bangunan!");
                }
            });



        }

        //progressDoalog.dismiss();
        //Toast.makeText(MainActivity.this, "Belum tersedia", Toast.LENGTH_SHORT).show();
        //ambilDataGambar();
    }

    private void deleteDataUpload(){
        LoginActivity.mSQLiteHelper.deleteDataUpload();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
    private boolean haveNetwork(){
        boolean have_WIFI=false;
        boolean have_MobileData=false;

        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos=connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected()){
                    have_WIFI=true;
                }
            }
            if(info.getTypeName().equalsIgnoreCase("MOBILE")){
                if (info.isConnected()){
                    have_MobileData=true;
                }
            }
        }
        return have_MobileData||have_WIFI;
    }

    void moveToDiagonis(final String id){
        Intent i = new Intent(this, DiagnosisActivity.class);
        Bundle bun = new Bundle();
        bun.putString("id", id);
        i.putExtras(bun);
        startActivity(i);
    }

    void  moveToHasilDiagnosis(final  String id){
        Intent i = new Intent(this, HasilDiagnosisActivity.class);
        Bundle bun = new Bundle();
        bun.putString("id", id);
        i.putExtras(bun);
        startActivity(i);
    }

    private void showDialogDelete(final String id) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Apa anda yakin untuk menghapus?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    LoginActivity.mSQLiteHelper.deleteData(id);
                    LoginActivity.mSQLiteHelper.deleteData2(id);
                    Toast.makeText(MainActivity.this, "Penghapusan berhasil", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateList();
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

    public void moveToUpdate(final String position){
        Intent i = new Intent(this, UpdateActivity.class);
        Bundle bun = new Bundle();
        bun.putString("id", position);
        i.putExtras(bun);
        startActivity(i);
    }

//    private void showDialogUpdate(Activity activity, final int position){
//        final Dialog dialog = new Dialog(activity);
//        dialog.setContentView(R.layout.update_dialog);
//        dialog.setTitle("Update");
//
//        imageViewIcon = dialog.findViewById(R.id.up_add_photo_btn);
//        final EditText edtNamaB = dialog.findViewById(R.id.up_nama_bangunan);
//        final EditText edtLantai = dialog.findViewById(R.id.up_jml_lantai);
//        final EditText edtThn = dialog.findViewById(R.id.up_thn_dibuat);
//        final EditText edtAlamatB = dialog.findViewById(R.id.up_alamat_bangunan);
//        final EditText edtLati = dialog.findViewById(R.id.up_latitude);
//        final EditText edtLongi = dialog.findViewById(R.id.up_longitude);
//        final EditText edtNama = dialog.findViewById(R.id.up_nama_person);
//        final EditText edtAlamat = dialog.findViewById(R.id.up_alamat_person);
//        final EditText edtNomor = dialog.findViewById(R.id.up_nomor_person);
//        Button btnUpdate = dialog.findViewById(R.id.update_btn);
//
//
//        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
//        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels*0.7);
//        dialog.getWindow().setLayout(width,height);
//        dialog.show();
//
//        imageViewIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SelectImage();
//            }
//        });
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    mSQLiteHelper.updateData(
//                            edtNamaB.getText().toString().trim(),
//                            edtLantai.getText().toString().trim(),
//                            edtThn.getText().toString().trim(),
//                            edtAlamatB.getText().toString().trim(),
//                            edtLati.getText().toString().trim(),
//                            edtLongi.getText().toString().trim(),
//                            imageViewToByte(imageViewIcon),
//                            edtNama.getText().toString().trim(),
//                            edtAlamat.getText().toString().trim(),
//                            edtNomor.getText().toString().trim(),
//                            position
//
//                    );
//                    dialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Update sukses", Toast.LENGTH_SHORT).show();
//                    finish();
//                    overridePendingTransition(0, 0);
//                    startActivity(getIntent());
//                    overridePendingTransition(0, 0);
//                }
//                catch (Exception error){
//                    Log.e("Update error", error.getMessage());
//                }
//                updateList();
//            }
//        });
//
//    }


    private void updateList() {
        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT id, nama_bangunan, jumlah_lantai, tahun, alamat_bangunan,latitude, " +
                "longitude, nama, alamat, nomor_hp FROM data_bangunan ORDER BY id DESC");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String namaB = cursor.getString(1);
            String lantai = cursor.getString(2);
            String thn = cursor.getString(3);
            String alamatB = cursor.getString(4);
            String lati = cursor.getString(5);
            String longi = cursor.getString(6);
//            byte[] image = cursor.getBlob(7);
            String nama = cursor.getString(7);
            String alamat = cursor.getString(8);
            String nomor = cursor.getString(9);
        }
        mAdapter.notifyDataSetChanged();
    }

//    private void SelectImage(){
//
//        final CharSequence[] items={"Camera","Gallery", "Cancel"};
//
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Add Image");
//
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (items[i].equals("Camera")) {
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, CAMERA_REQUEST_CODE1);
//
//                } else if (items[i].equals("Gallery")) {
//
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent.setType("image/*");
//                    //startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
//                    startActivityForResult(intent, CAMERA_REQUEST_CODE2);
//
//                } else if (items[i].equals("Cancel")) {
//                    dialogInterface.dismiss();
//                }
//            }
//        });
//        builder.show();
//
//    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode==CAMERA_REQUEST_CODE1){

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imageViewIcon.setImageBitmap(bmp);

            }else if(requestCode==CAMERA_REQUEST_CODE2){

                Uri selectedImageUri = data.getData();
                imageViewIcon.setImageURI(selectedImageUri);
            }

        }
    }


    //    private static byte[] imageViewToByte(ImageView image) {
//        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        return byteArray;
//    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void ambilDataGambar(){
        for(int i=1;i<=408;i++){
            String filepath = "/sdcard/Pictures/Rusak Sedang/data2 ("+i+").jpg";
            File imagefile = new File(filepath);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Bitmap bm = BitmapFactory.decodeStream(fis);
            //imageViewIcon.setImageBitmap(bm);
            potongGambar(bm);
        }


    }

    void potongGambar(Bitmap gambar){
        Bitmap image = convertBitmap(gambar);
        int row = 4;
        int col = 1;
        panjang = image.getHeight();
        lebar = image.getWidth();
        tmpx = (double) lebar/2;
        tmpy = (double) panjang/2;
        tengahx = (int) Math.ceil(tmpx);
        tengahy = (int) Math.ceil(tmpy);
        System.out.println("width = "+panjang+", height = "+lebar);
        System.out.println("tengahx = "+tengahx+", tengahy = "+tengahy);

        String array2d[][] = new String[panjang][lebar];

        //width and height of each piece
        int eWidth = lebar / col;
        int eHeight = panjang / row;

        int x = 0;
        int y = 0;

        double g1,g2,g3,g4;

        for (int i = 0; i < row; i++) {
            y = 0;
            for (int j = 0; j < col; j++) {
                try {
                    System.out.println("creating piece: "+i+" "+j);
                    Bitmap cropedBitmap = Bitmap.createBitmap(image, y, x, eWidth, eHeight);
//                    BufferedImage SubImgage = image.getSubimage(y, x, eWidth, eHeight);
//                    File outputfile = new File("C:/temp/TajMahal"+i+j+".jpg");
//                    ImageIO.write(SubImgage, "jpg", outputfile);
                    if(i == 0){
                        getImageData(cropedBitmap,1);

                    }
                    else if(i == 1){
                        getImageData(cropedBitmap,2);

                    }
                    else if(i == 2){
                        getImageData(cropedBitmap,3);

                    }
                    else if(i == 3){
                        getImageData(cropedBitmap,4);
                    }

                    y += eWidth;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            x += eHeight;
        }

        data_txt = d1+" "+d2+" "+d3+" "+d4+" "+dd1+" "+dd2+" "+dd3+" "+dd4;
        LoginActivity.mSQLiteHelper.insertDataGambar(data_txt);

    }
    public void  saveToTxt(){
        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT * FROM data_gambar");
        mList.clear();
        String data="";
        while (cursor.moveToNext()){
            data = data + cursor.getString(0)+"\n";

        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        try{
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/My Files/");
            dir.mkdirs();
            String fileName = "MyFile_" + timeStamp + ".txt";
            File file = new File(dir,fileName);

            FileWriter fw =new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();

            Toast.makeText(this, ""+dir, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Bitmap convertBitmap(Bitmap input) {
        int width = input.getWidth();
        int height = input.getHeight();
        Bitmap firstPass =  Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Bitmap secondPass =  Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas firstCanvas = new Canvas(firstPass);
        Paint colorFilterMatrixPaint = new Paint();
        colorFilterMatrixPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                1, 1, 1, -1, 0
        }));

        firstCanvas.drawBitmap(input, 0, 0, colorFilterMatrixPaint);

        Canvas secondCanvas = new Canvas(secondPass);
        Paint colorFilterMatrixPaint2 = new Paint();
        colorFilterMatrixPaint2.setColorFilter(new ColorMatrixColorFilter(new float[]{
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 255, -255
        }));

        secondCanvas.drawBitmap(firstPass, 0, 0, colorFilterMatrixPaint2);

        int pixels[] = new int[width * height];
        byte pixelsMap[] = new byte[width * height];
        secondPass.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelsMap[(x * y) + y] = (byte) ((pixels[(x * y) + y] >> 24) * -1);
            }
        }
        return secondPass;
    }

    protected void getImageData(Bitmap img, int kode){
        int w = img.getWidth();
        int h = img.getHeight();
        int[] data = new int[w * h];
        img.getPixels(data, 0, w, 0, 0, w, h);

        int[][] pixel = new int[img.getHeight()][img.getWidth()];
        System.out.println("tinggi "+img.getHeight()+" lebar "+img.getWidth()+"panjangnya adalah "+data.length);
        int c=0;
        int tengahX = img.getWidth()/2;
        int tengahY = img.getHeight()/2;

        System.out.println("tengah ->: "+tengahY+" "+tengahX);
        for( int a = 0; a < img.getHeight(); a++ ) {
            for (int b=0;b<img.getWidth();b++){
                pixel[a][b]=data[c];
                c++;
                System.out.print(pixel[a][b]+" ");
            }
            System.out.println();
        }
        double d=0;
        double dd=0;
        int jum_o=0;
        for(int a=0;a<img.getHeight();a++){
            for(int b=0;b<img.getWidth();b++){
                if(pixel[a][b]==0){
                    d=d + Math.sqrt(Math.pow(a-tengahY,2)+Math.pow(b-tengahX,2));
                    dd=dd + Math.sqrt(Math.pow(a-tengahy,2)+Math.pow(b-tengahx,2));
                    jum_o++;
                }
            }
        }
        System.out.println("jum d -> "+d+" d2 -> "+dd+" jum 0 -> "+jum_o);
        if(kode==1){
            if(jum_o!=0){
                d1=d/jum_o;
                dd1=dd/jum_o;
            }
            else {
                d1=0;
                dd1=0;
            }

            System.out.println("jum rata -> "+d1+" jum rata2 "+dd1);
        }
        else if(kode==2){
            if(jum_o!=0){
                d2=d/jum_o;
                dd2=dd/jum_o;
            }
            else {
                d2=0;
                dd2=0;
            }
            System.out.println("jum rata -> "+d2+" jum rata2 "+dd2);
        }
        else if(kode==3){
            if(jum_o!=0){
                d3=d/jum_o;
                dd3=dd/jum_o;
            }
            else {
                d3=0;
                dd3=0;
            }
            System.out.println("jum rata -> "+d3+" jum rata2 "+dd3);
        }
        else if(kode==4){
            if(jum_o!=0){
                d4=d/jum_o;
                dd4=dd/jum_o;
            }
            else {
                d4=0;
                dd4=0;
            }
            System.out.println("jum rata -> "+d4+" jum rata2 "+dd4);
        }
    }


}
