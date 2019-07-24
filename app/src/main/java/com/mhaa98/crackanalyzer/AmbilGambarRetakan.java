package com.mhaa98.crackanalyzer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class AmbilGambarRetakan extends AppCompatActivity {

    int kode,struktur,pos;
    Button gallery,camera;
    ImageView baris1,baris2,baris3,baris4,baris0;
    String pathToFile;
    double tmpx, tmpy;
    int panjang, lebar, tengahx, tengahy;
    TextView keterangan;

    double d1, d2, d3, d4, dd1, dd2, dd3, dd4;
    String data_txt;
    double[] fitur;

    private static final int WRITE_EXTERNAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambil_gambar_retakan);
        fitur = new double[8];

        gallery = findViewById(R.id.galery_btn);
        camera = findViewById(R.id.camera_btn);
        baris1 = findViewById(R.id.baris_1);
        baris2 = findViewById(R.id.baris_2);
        baris3 = findViewById(R.id.baris_3);
        baris4 = findViewById(R.id.baris_4);
        baris0 = findViewById(R.id.baris_0);

        keterangan = findViewById(R.id.keterangan_struktur);

        Bundle b = getIntent().getExtras();
        kode = b.getInt("id");
        struktur = b.getInt("stuk");
        pos = b.getInt("pos");


        if(struktur==1){
            getSupportActionBar().setTitle("Ambil Gambar Kolom");
            keterangan.setText("Kolom ke-"+(pos+1));
        }
        else if(struktur==2){
            getSupportActionBar().setTitle("Ambil Gambar Balok");
            keterangan.setText("Balok ke-"+(pos+1));
        }
        else if(struktur==3){
            getSupportActionBar().setTitle("Ambil Gambar Dinding");
            keterangan.setText("Dinding ke-"+(pos+1));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    File photoFile = null;
                    photoFile = createPhotoFile();
                    if(photoFile != null){
                        pathToFile = photoFile.getAbsolutePath();
                        Uri photoUri = FileProvider.getUriForFile(AmbilGambarRetakan.this, "com.thecodecity.cameraandroid.fileprovider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, 1);
                    }
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                //startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                startActivityForResult(intent, 0);
            }
        });
    }
    private  File createPhotoFile(){
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try{
            image = File.createTempFile(name,".jpg", storageDir);
        }
        catch (IOException e){
            Log.d("mylog","Excep : "+e.toString());
        }
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            CropImage.activity(Uri.fromFile(new File(pathToFile)))
                    .setAspectRatio(1,1)
                    .start(this);
        }
        else if(requestCode == 0 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imageUri = result.getUri(); //Mengubah data image kedalam Uri
                Bitmap mBitmap = null;
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(mBitmap, 227, 227, false);
                    baris0.setImageBitmap(resizedBitmap);
                    potongGambar(resizedBitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                //Menangani Jika terjadi kesalahan
                String error = result.getError().toString();
                Log.d("Exception", error);
                Toast.makeText(getApplicationContext(), "Crop Image Error", Toast.LENGTH_SHORT).show();
            }
        }

//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri imageUri = result.getUri(); //Mengubah data image kedalam Uri
//                Bitmap mBitmap = null;
//                try {
//                    mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                    potongGambar(mBitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
////                moveToUpdate(kode, 1);
////                onBackPressed();
//
//                //Menampilkan Gambar pada ImageView
////                Picasso.get().load(imageUri).into(poto);
////                isi_gambar=true;
//
//            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                //Menangani Jika terjadi kesalahan
//                String error = result.getError().toString();
//                Log.d("Exception", error);
//                Toast.makeText(getApplicationContext(), "Crop Image Error", Toast.LENGTH_SHORT).show();
//            }
//        }

    }

    void moveToUpdate(int id, int level){
        try{
            LoginActivity.mSQLiteHelper.updateDataKerusakan(level,id);
            onBackPressed();
        }
        catch (Exception e){
            Log.e("error", e.getMessage());
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


    @RequiresApi(api = Build.VERSION_CODES.M)
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
                        baris1.setImageBitmap(cropedBitmap);
                        getImageData(cropedBitmap,1);

                    }
                    else if(i == 1){
                        baris2.setImageBitmap(cropedBitmap);
                        getImageData(cropedBitmap,2);

                    }
                    else if(i == 2){
                        baris3.setImageBitmap(cropedBitmap);
                        getImageData(cropedBitmap,3);

                    }
                    else if(i == 3){
                        baris4.setImageBitmap(cropedBitmap);
                        getImageData(cropedBitmap,4);
                    }

                    y += eWidth;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            x += eHeight;
        }

        data_txt = d1+" # "+d2+" # "+d3+" # "+d4+" # "+dd1+" # "+dd2+" # "+dd3+" # "+dd4;
        fitur[0]=d1; fitur[1]=d2; fitur[2]=d3; fitur[3]=d4;
        fitur[4]=dd1; fitur[5]=dd2; fitur[6]=dd3; fitur[7]=dd4;
        System.out.println(data_txt);
//        saveToTxt(data_txt);
        kenaliPola();

    }

    public void kenaliPola(){
//        double[][] wHidden = {
//                {0.529213787,	4.849756005,	2.569092881,	1.792153478,	-1.342788688,	2.104159094,	0.765119791,	-0.125151217},
//                {-0.43304143,	1.335734912,	0.764255585,	-1.172926221,	1.0977554,	1.747051841,	-0.874177971,	1.511447218},
//                {1.578601648,	-2.214189092,	3.49856605,	2.352844003,	1.615145864,	-1.31921468,	-2.587039373,	-1.275279653},
//                {3.284184723,	-3.245761208,	-0.454990356,	1.393330238,	1.749829328,	1.001451486,	0.697510802,	-1.029407836},
//                {-0.446300619,	-0.366221445,	-0.447479724,	-0.224565536,	-0.64241201,	-0.207512776,	-2.193040637,	-2.060632413},
//                {-1.632836292,	2.822247672,	-1.51699049,	-0.633528676,	0.545843943,	-0.23757024,	2.575435213,	1.985857351},
//                {2.051390535,	-2.412196182,	-5.948145171,	0.916230658,	-1.692104029,	2.416439352,	-1.218242447,	-2.230982169},
//                {2.52756969,	0.813469631,	4.531586345,	-0.329692935,	-0.783439301,	-0.119087195,	-2.574139768,	0.112902245},
//                {-0.283836461,	1.74013505, 2.204021373,	-1.275715495,	-1.593821893,	-1.660727346,	-1.620942525,	-2.488897846},
//                {0.572100809,	0.435769163,	1.819393774,	0.581801489,	1.573112655,	1.422019565,	2.375444731,	0.231766568}
//        };
        double[][] wHidden = {
                {-2.272343544,	-0.95699697,	-2.487863217,	-1.138329312,	1.196369557,	-0.205596187,	-1.170270849,	-0.390298825},
                {-3.277600557,	2.763179847,	-0.133365334,	2.070909577,	-1.438537791,	1.088500776,	-2.418510081,	0.202740953},
                {-1.401664617,	1.289429965,	6.884984441,	-4.300780746,	4.728097241,	0.353662822,	-1.263916323,	4.972066425},
                {-1.293370365,	-0.006905242,	3.618280207,	3.752730896,	2.036842761,	1.341534425,	0.248579132,	-5.440506074},
                {2.019053254,	-0.813707781,	3.764102794,	-4.176948615,	-5.80084318,	-3.000180285,	-5.690243057,	0.77719729},
                {3.970867379,	0.338931995,	4.095564521,	-3.842196261,	0.29931955,	-6.873803232,	-0.830492105,	1.210068516},
                {5.639945076,	4.116723451,	-3.482936143,	-3.294326054,	2.869856496,	-2.237313051,	-1.682816154,	-1.72748179},
                {-0.053103207,	-1.984092557,	2.012812064,	-1.74550153,	6.276120519,	-0.120959727,	-3.230491711,	2.148918676},
                {-3.047477544,	-0.233462058,	0.860850024,	2.368895102,	5.512006548,	3.661244956,	-0.181366883,	-0.87154538},
                {0.903423207,	-0.483700045,	-1.408005461,	1.386771662,	-0.04216898,	1.104351112,	-0.531293818,	4.752951675}
        };

//        double[] bHidden = {-3.199068346, 3.112236069, -1.746677894, -1.995896545, -1.837489874, -0.480791686, -1.995856947, 0.28435048, 2.691808757, 3.723629328};
        double[] bHidden = {4.880749963, 1.731815719, 5.71778631, 0.045893807, 1.84160361, 4.857752933, 1.944124309, -2.174930815, 5.49926046, 7.297290886};

//        double[][] wKeluaran = {
//                {-3.969131983,	1.442303443,	-4.426600361,	4.666807193,	-1.137368929,	2.794100264,	-2.158397062,	-2.966237616,	-4.268564953,	1.466049061},
//                {-3.304103987,	0.882907773,	1.506499558,	-2.009022973,	-3.010479626,	1.540911844,	5.995733249,	0.924922203,	-0.773138739,	2.268466802},
//                {2.090590903,	1.126301029,	2.991239744,	-0.624765806,	2.950572961,	0.003567094,	-2.805225133,	2.035104164,	0.598715257,	2.387149942},
//                {-2.084855221,	2.77121541,	-2.972240239,	-2.236969062,	1.717593493,	-1.309743757,	0.310335869,	-1.16062691,	-2.115541824,	-0.742481699},
//                {-2.135338637,	-0.4788151,	0.321894402,	2.189091894,	2.523460359,	-1.135563205,	4.081451866,	0.038476253,	3.513774605,	-3.683724142}
//        };
        double[][] wKeluaran = {
                {0.976791855,	3.113836115,	-3.653343269,	1.884246681,	1.876553517,	-3.82982676,	-1.17669804,	-0.403000201,	0.153197092,	1.094654185},
                {0.18744081,	-0.344047582,	0.30652355,	-0.175997391,	-5.173810499,	-0.362448367,	-2.446544733,	6.851013758,	-3.291132134,	-0.265524894},
                {-0.317380026,	-5.712374952,	1.53063029,	6.379516981,	6.140822843,	2.196880408,	-6.421091054,	1.08974286,	1.363284194,	-0.11993134},
                {1.202384449,	1.243479569,	-1.806838572,	-5.42995839,	-6.615372386,	1.489145811,	1.081116772,	-0.025346762,	1.208990896,	-1.983557735},
                {4.519016281,	0.118935055,	-3.201339095,	4.90425078,	-0.586854632,	-5.451774371,	-0.968876277,	8.270899944,	-3.268854742,	3.411239228}

        };

//        double[] bKeluaran = {3.832392496, 0.073808461, -6.856152969, 5.1108744, -7.322274539};
        double[] bKeluaran = {-5.273786324, -1.373900218, -2.698382704, 4.026174486, 2.696061515};

//        double[] wHasil = {-3.620599803,	3.602832413,	0.886616601,	0.600819642,	-1.469233637};
        double[] wHasil = {-1.370369983, 4.709728629, -2.540898707, -4.318847353, 3.774007286};

        double[] tmp = new double[10];
        double[] tmp2 = new double[5];
        double hasil=0;

//        double bHasil=-1.565110406;
        double bHasil= 1.031727303;

        int level=0;
        for(int i=0;i<10;i++){
            tmp[i]=0;
            for(int j=0;j<8;j++){
                tmp[i]=tmp[i]+(fitur[j]*wHidden[i][j]);
            }
            System.out.println("ini diaaaaa "+tmp[i]);
        }
        for (int i=0;i<10;i++){
            tmp[i]=tmp[i]+bHidden[i];
        }
        for(int i=0;i<5;i++){
            tmp2[i]=0;
            for(int j=0;j<10;j++){
                tmp2[i]=tmp2[i]+(tmp[j]*wKeluaran[i][j]);
            }
        }
        for (int i=0;i<5;i++){
            tmp2[i]=tmp2[i]+bKeluaran[i];
        }
        for (int i=0;i<5;i++){
            hasil=hasil+(tmp2[i]*wHasil[i]);
        }
        hasil=hasil+bHasil;

        hasil=(hasil/255)*0.1;
//        hasil=hasil*-1;
        System.out.println("hasil angka pengenalan "+hasil);
        if(hasil>1.5){
            System.out.println(keterangan.getText()+" rusak berat");
            Toast.makeText(this, keterangan.getText()+" rusak berat", Toast.LENGTH_SHORT).show();
            level=3;
        }
        else if(hasil>1 && hasil<=1.5){
            System.out.println(keterangan.getText()+" rusak sedang");
            Toast.makeText(this, keterangan.getText()+" rusak sedang", Toast.LENGTH_SHORT).show();
            level=2;
        }
        else if(hasil<=1){
            System.out.println(keterangan.getText()+" rusak ringan");
            Toast.makeText(this, keterangan.getText()+" rusak ringan", Toast.LENGTH_SHORT).show();
            level=1;
        }

        moveToUpdate(kode, level);


    }


    public void  saveToTxt(String data){
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


}

