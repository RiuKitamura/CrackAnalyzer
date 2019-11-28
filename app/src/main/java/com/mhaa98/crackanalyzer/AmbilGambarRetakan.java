package com.mhaa98.crackanalyzer;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class AmbilGambarRetakan extends AppCompatActivity {

    byte[] image;
    int kode,struktur,pos;
    Button gallery,camera;
    ImageView gambar_retakan, baris1,baris2,baris3,baris4,baris0;
    String pathToFile;
    double tmpx, tmpy;
    int panjang, lebar, tengahx, tengahy;
    TextView keterangan;

    double d1, d2, d3, d4, dd1, dd2, dd3, dd4;
    String data_txt;
    double[] fitur;
    double[] fiturGLCM = new double[20];

    private static final int WRITE_EXTERNAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambil_gambar_retakan);
        fitur = new double[8];

        gallery = findViewById(R.id.galery_btn);
        camera = findViewById(R.id.camera_btn);
        gambar_retakan = findViewById(R.id.imv_gambar_retakan);
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

        Cursor cursor = LoginActivity.mSQLiteHelper.getData("SELECT poto_kondisi FROM data_kerusakan WHERE id="+kode);
        while (cursor.moveToNext()){
            image = cursor.getBlob(0);
            if (image != null)
            gambar_retakan.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
        }


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

                    LoginActivity.mSQLiteHelper.updateGambarRetakan(kode,imageViewToByte(resizedBitmap));
                    System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"+mBitmap.getHeight()+" jjjj "+mBitmap.getWidth());
//                    String filepath = "/storage/emulated/0/Data/Rusak Berat/data1 (1).jpg";
//                    //String filepath = "/sdcard/Pictures/Rusak Sedang/data2 ("+i+").jpg";
//                    Bitmap bitmap=null;
//                    try {
//                        File f= new File(filepath);
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//
//                        bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"+bitmap.getHeight()+" jjjj "+bitmap.getWidth());

                    GLCM glcmfe = new GLCM(resizedBitmap,256);
                    fiturGLCM = glcmfe.extract();
                    for(int x=0;x<20;x++) {
                        System.out.print(" " + fiturGLCM[x]);
                        //potongGambar(resizedBitmap);
                    }
                    kenaliPola();


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
//        3 kelas
//        double[][] wHidden = {
//                {-2.272343544,	-0.95699697,	-2.487863217,	-1.138329312,	1.196369557,	-0.205596187,	-1.170270849,	-0.390298825},
//                {-3.277600557,	2.763179847,	-0.133365334,	2.070909577,	-1.438537791,	1.088500776,	-2.418510081,	0.202740953},
//                {-1.401664617,	1.289429965,	6.884984441,	-4.300780746,	4.728097241,	0.353662822,	-1.263916323,	4.972066425},
//                {-1.293370365,	-0.006905242,	3.618280207,	3.752730896,	2.036842761,	1.341534425,	0.248579132,	-5.440506074},
//                {2.019053254,	-0.813707781,	3.764102794,	-4.176948615,	-5.80084318,	-3.000180285,	-5.690243057,	0.77719729},
//                {3.970867379,	0.338931995,	4.095564521,	-3.842196261,	0.29931955,	-6.873803232,	-0.830492105,	1.210068516},
//                {5.639945076,	4.116723451,	-3.482936143,	-3.294326054,	2.869856496,	-2.237313051,	-1.682816154,	-1.72748179},
//                {-0.053103207,	-1.984092557,	2.012812064,	-1.74550153,	6.276120519,	-0.120959727,	-3.230491711,	2.148918676},
//                {-3.047477544,	-0.233462058,	0.860850024,	2.368895102,	5.512006548,	3.661244956,	-0.181366883,	-0.87154538},
//                {0.903423207,	-0.483700045,	-1.408005461,	1.386771662,	-0.04216898,	1.104351112,	-0.531293818,	4.752951675}
//        };
        //Hasil training GLCM
        double[][] wHidden = {
                {5.015807184, -2.71917274, 6.210828464, -0.522369139, 2.224556844, -1.189555331, 0.422913151, -1.75647812, -3.371290093, -0.234869151, 0.03620707, 0.033692842, -1.781615564, 2.274388266, -0.625076039, 1.823178499, -1.622078344, 2.099988159, 0.160205756, 3.141006829},
                {-1.24420947, -1.15181041, -0.835994066, -0.182841386, 0.743846761, -0.609677419, -0.835703317, -0.511886587, 0.691660517, -0.350296235, 0.817235475, -0.367822319, 0.025202351, 1.129600268, 0.530109565, 2.158117935, -0.078019435, -0.285957955, -0.905756613, -0.729441884},
                {1.407826748, -1.100193873, 0.584452012, 1.281209829, 4.561879296, -2.568674043, 2.481612446, -1.485583675, -0.600507988, 1.235685048, 1.375089631, 2.096816359, 2.238842532, 0.476332497, 1.397982741, 0.128683391, -0.432710777, -1.01956716, -3.557924387, 2.449291537},
                {1.078526344, -4.597823575, 0.784300023, 2.478988567, -0.379562634, 3.369373416, 0.175982547, 0.269424395, 1.351919189, -1.250970008, -0.281497244, 1.605476198, 3.018270277, -1.194837085, 2.515739799, 0.796924425, 0.077745729, -1.397652583, 0.371964844, 0.728753414},
                {2.2130999, -1.702834907, 4.012421483, -5.340338837, -0.82429644, -0.99837072, -1.317112821, -4.694425912, 0.405275884, -0.763028063, -0.142065159, 0.622596283, -3.258075898, -6.511458655, -2.485789615, 3.10247625, -1.221462931, -2.087263713, 1.906837274, 1.617944281},
                {0.015382213, -1.054303903, -2.211999911, 1.723764079, -0.505752209, -0.932775626, -0.695357552, -1.559829393, -0.170404666, 0.37510684, 0.921090009, 1.093793681, 6.00658444, 2.183394515, 4.701956505, 1.574230328, -2.559363358, -0.401254837, -0.457888424, -0.132369423},
                {-0.247565586, -2.288685586, -1.689386821, -1.615238465, 2.191375643, 1.14856785, 1.813063016, 0.871100349, -0.410696016, 0.36456578, -2.128609591, -1.410664636, -2.207214966, -2.636867246, -1.357993201, 0.600782419, -0.379801588, 0.329501948, -1.002616517, -0.052916398},
                {-0.750956151, -0.564472496, -2.246048881, -0.015252546, 0.374803811, 0.5619945, 0.216637801, 0.154734236, 0.144857441, -0.840332894, -0.937924347, -0.231600655, 3.000700627, -0.682548967, 0.517549872, 0.899221896, 0.94878163, 0.129398267, 1.70779312, 2.558941071},
                {0.17613846, -0.453676236, -0.240239872, -0.436907122, 0.037163616, -0.589866871, -0.652543868, -0.721653844, 0.048678451, -0.472971938, 0.639282086, -0.021779527, -0.092353664, 1.03965141, -0.892430853, 0.661038614, -1.211331365, -1.684155095, -0.117927008, -1.415730518},
                {0.891494247, 1.072347054, 1.46944181, 2.032989836, -0.340532629, -0.096101701, 0.390542649, -0.135692654, -1.393531298, -1.14027976, -1.820459902, 0.200634551, 1.225644682, -0.599706694, -2.281375411, -0.33557714, 0.693253441, 0.798148611, -0.116991217, 1.251814036}
        };

//        double[] bHidden = {-3.199068346, 3.112236069, -1.746677894, -1.995896545, -1.837489874, -0.480791686, -1.995856947, 0.28435048, 2.691808757, 3.723629328};
//        double[] bHidden = {4.880749963, 1.731815719, 5.71778631, 0.045893807, 1.84160361, 4.857752933, 1.944124309, -2.174930815, 5.49926046, 7.297290886};
        double[] bHidden = { 1.553372224, 2.269028201, 3.476534687, 1.635911353, 3.084143695, 1.8357482, 2.717295654, -3.438407964, 2.266341487, 2.986113484 };

//        double[][] wKeluaran = {
//                {-3.969131983,	1.442303443,	-4.426600361,	4.666807193,	-1.137368929,	2.794100264,	-2.158397062,	-2.966237616,	-4.268564953,	1.466049061},
//                {-3.304103987,	0.882907773,	1.506499558,	-2.009022973,	-3.010479626,	1.540911844,	5.995733249,	0.924922203,	-0.773138739,	2.268466802},
//                {2.090590903,	1.126301029,	2.991239744,	-0.624765806,	2.950572961,	0.003567094,	-2.805225133,	2.035104164,	0.598715257,	2.387149942},
//                {-2.084855221,	2.77121541,	-2.972240239,	-2.236969062,	1.717593493,	-1.309743757,	0.310335869,	-1.16062691,	-2.115541824,	-0.742481699},
//                {-2.135338637,	-0.4788151,	0.321894402,	2.189091894,	2.523460359,	-1.135563205,	4.081451866,	0.038476253,	3.513774605,	-3.683724142}
//        };
//        double[][] wKeluaran = {
//                {0.976791855,	3.113836115,	-3.653343269,	1.884246681,	1.876553517,	-3.82982676,	-1.17669804,	-0.403000201,	0.153197092,	1.094654185},
//                {0.18744081,	-0.344047582,	0.30652355,	-0.175997391,	-5.173810499,	-0.362448367,	-2.446544733,	6.851013758,	-3.291132134,	-0.265524894},
//                {-0.317380026,	-5.712374952,	1.53063029,	6.379516981,	6.140822843,	2.196880408,	-6.421091054,	1.08974286,	1.363284194,	-0.11993134},
//                {1.202384449,	1.243479569,	-1.806838572,	-5.42995839,	-6.615372386,	1.489145811,	1.081116772,	-0.025346762,	1.208990896,	-1.983557735},
//                {4.519016281,	0.118935055,	-3.201339095,	4.90425078,	-0.586854632,	-5.451774371,	-0.968876277,	8.270899944,	-3.268854742,	3.411239228}
//
//        };
        double[][] wKeluaran = {
                { -2.644285898, 0.707508475,  1.809848863, -5.712872851, 0.632040544, 6.461606666, -1.810842913, -1.908224132, 0.201069055, -3.302801041},
                { 3.004470503, 1.149099905, -4.076101639, 2.78701235, 5.20467199, 0.303197814, 1.431145361, -2.498925776, -2.955754163, 2.584860614},
                { 6.669041899, 0.336002037, 7.738741726, -3.553976822, 4.177553547, 0.930657776, 3.345570753, 3.488668224, -4.299319207, 1.099875006},
                { -2.424622905, 1.517715579, -2.92948177, -0.219505452, 3.997468808, 0.934029261, 1.318505218, -2.364529296, -1.467414093, 2.20097422},
                { 2.565056892, 0.760634699, 3.02608532, -0.373817128, 0.099662473, -0.001636475, 3.283476127, 0.479712221, -1.840548669, -1.82386745}
        };

//        double[] bKeluaran = {3.832392496, 0.073808461, -6.856152969, 5.1108744, -7.322274539};
//        double[] bKeluaran = {-5.273786324, -1.373900218, -2.698382704, 4.026174486, 2.696061515};
        double[] bKeluaran = {5.507250205, -8.223198148, -10.14682704, -5.762327436, 2.89486503};

//        double[] wHasil = {-3.620599803,	3.602832413,	0.886616601,	0.600819642,	-1.469233637};
//        double[] wHasil = {-1.370369983, 4.709728629, -2.540898707, -4.318847353, 3.774007286};
        double[] wHasil = { 1.434083926, -2.60028838, 2.798096656, 1.011365802, -0.188108965};

        double[] tmp = new double[10];
        double[] tmp2 = new double[5];
        double hasil=0;

//        double bHasil=-1.565110406;
//        double bHasil= 1.031727303;
        double bHasil = -1.413246998;

        double[] inputs = new double[20];
        double[] xmax = {747.325725885824, 976.478082956161, 767.093503937004, 1167.30206460412, 3.99953013424986, 4.01942952668241, 3.98081726380265, 4.01599370510315, 0.0507683576432066, 0.0352289564197722, 0.0452375538263900, 0.0355177949036547, 0.996141710451335, 0.992868245844429, 0.996553098766182, 0.993830989072377, 0.901656162743131, 0.792152116826534, 0.860074818856364, 0.796548443673628};
        double[] xmin = {0.571767769607843, 1.24079969242598, 0.809451593137254, 1.19117262591311, 1.63871515522983, 1.85018933633483, 1.69884230424352, 1.83812238044080, 0.000143796223395163, 0.000138748043110621, 0.000146893035994283, 0.000140086776431329, 0.157213523916287, 0.110842071751426, 0.140370819242068, 0.0912619350962293, 0.135778758045413, 0.132183300336789, 0.135569946100769, 0.131970973401002};
        double ymax = 1;
        double ymin = -1;

        //normalisasi input
        for(int n=0;n<20;n++) {
            inputs[n] = (ymax - ymin)*(fiturGLCM[n]-xmin[n])/(xmax[n]-xmin[n])+ymin;
            System.out.println("Input ke-"+n+": "+inputs[n]);
        }

        //forward propagation
        int level=0;
        for(int i=0;i<10;i++){
            tmp[i]=0;
            for(int j=0;j<20;j++){
                tmp[i]=tmp[i]+(inputs[j]*wHidden[i][j]);
            }
        }
        for (int i=0;i<10;i++){
            tmp[i]=tmp[i]+bHidden[i];
            tmp[i]=1/(1+Math.exp(-1*tmp[i]));
        }
        for(int i=0;i<5;i++){
            tmp2[i]=0;
            for(int j=0;j<10;j++){
                tmp2[i]=tmp2[i]+(tmp[j]*wKeluaran[i][j]);
            }
        }
        for (int i=0;i<5;i++){
            tmp2[i]=tmp2[i]+bKeluaran[i];
            tmp2[i]=1/(1+Math.exp(-1*tmp2[i]));
        }
        for (int i=0;i<5;i++){
            hasil=hasil+(tmp2[i]*wHasil[i]);
        }
        hasil=hasil+bHasil;

        System.out.println("hasil angka pengenalan "+hasil);
        hasil=Math.round(hasil);

        //hasil=(hasil/255)*0.1;
//        hasil=hasil*-1;
        System.out.println("hasil pembulatan "+hasil);
        if(hasil>=2){
            System.out.println(keterangan.getText()+" rusak berat");
            Toast.makeText(this, keterangan.getText()+" rusak berat", Toast.LENGTH_SHORT).show();
            level=3;
        }
        else if(hasil==1){
            System.out.println(keterangan.getText()+" rusak sedang");
            Toast.makeText(this, keterangan.getText()+" rusak sedang", Toast.LENGTH_SHORT).show();
            level=2;
        }
        else if(hasil<=0){
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

    private static byte[] imageViewToByte(Bitmap image) {
        //Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, 227, 227, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


}
