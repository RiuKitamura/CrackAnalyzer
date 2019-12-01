package com.mhaa98.crackanalyzer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {
    SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public void queryData (String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertGejala(){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO ds_gejala VALUES" +
                "(1,'Total Kolom Rusak Ringan > 0% dan < 20%'), " +
                "(2,'Total Kolom Rusak Ringan >= 20% dan < 50%'), " +
                "(3,'Total Kolom Rusak Ringan > =50%'), " +
                "(4,'Total Kolom Rusak Sedang > 0% dan < 20%'), " +
                "(5,'Total Kolom Rusak Sedang >= 20% dan < 50%'), " +
                "(6,'Total Kolom Rusak Sedang > =50%'), " +
                "(7,'Total Kolom Rusak Berat > 0% dan < 20%'), " +
                "(8,'Total Kolom Rusak Berat >= 20% dan < 50%'), " +
                "(9,'Total Kolom Rusak Berat > =50%'), " +
                "(10,'Total Balok Rusak Ringan > 0% dan < 20%'), " +
                "(11,'Total Balok Rusak Ringan >= 20% dan < 50%'), " +
                "(12,'Total Balok Rusak Ringan > =50%'), " +
                "(13,'Total Balok Rusak Sedang > 0% dan < 20%'), " +
                "(14,'Total Balok Rusak Sedang >= 20% dan < 50%'), " +
                "(15,'Total Balok Rusak Sedang > =50%'), " +
                "(16,'Total Balok Rusak Berat > 0% dan < 20%'), " +
                "(17,'Total Balok Rusak Berat >= 20% dan < 50%'), " +
                "(18,'Total Balok Rusak Berat > =50%'), " +
                "(19,'Total Dinding Rusak Ringan > 0% dan < 20%'), " +
                "(20,'Total Dinding Rusak Ringan >= 20% dan < 50%'), " +
                "(21,'Total Dinding Rusak Ringan > =50%'), " +
                "(22,'Total Dinding Rusak Sedang > 0% dan < 20%'), " +
                "(23,'Total Dinding Rusak Sedang >= 20% dan < 50%'), " +
                "(24,'Total Dinding Rusak Sedang > =50%'), " +
                "(25,'Total Dinding Rusak Berat > 0% dan < 20%'), " +
                "(26,'Total Dinding Rusak Berat >= 20% dan < 50%'), " +
                "(27,'Total Dinding Rusak Berat > =50%')";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.execute();
        database.close();
    }

    public void insertLevel(){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO ds_level VALUES" +
                "(0,'Tidak Rusak','Bangunan tidak rusak', 'Bangunan tidak mengalami kerusakan'), " +
                "(1,'Rusak Ringan','Dinding retak halus, kerusakan tidak tembus,plesteranterkelupas.Plafon dan listplang rusak, tidak ada kerusakan struktural.', 'Bangunan tidak perlu dikosongkan, hanya perlu perbaikan kosmetik secara arsitektur agar daya tahan bangunan tetap terpelihara'), " +
                "(2,'Rusak Sedang','Dinding partisi retak tembus atau roboh sebagian, Bagian struktur (kolom, balok, kuda-kuda) mengalami kerusakan tetapi masihdapatdiperbaiki.Dindingstruktural (bangunan tanpa kolom dan balok) mengalami kerusakan yang masih dapat diperbaiki', 'Bangunan perlu dikosongkan dan boleh dihuni kembali setelah dilakukan perbaikan dan perkuatan untuk dapat menahan beban gempa.'), " +
                "(3,'Rusak Berat','Dinding partisi retak tembus atau roboh sebagian, Bagian struktur (kolom, balok, kuda-kuda) mengalami kerusakan tetapi masihdapatdiperbaiki.Dindingstruktural (bangunan tanpa kolom dan balok) mengalami kerusakan yang masih dapat diperbaiki.', 'Bangunan harus dikosongkan atau dirobohkan ')";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.execute();
        database.close();
    }

    public void insertRules(){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO ds_rules VALUES" +
                "(1, 1, 0.70), " +
                "(1, 2, 0.80), " +
                "(1, 3, 0.85), " +
                "(1, 4, 0.75), " +
                "(2, 4, 0.75), " +
                "(2, 5, 0.88), " +
                "(2, 6, 0.95), " +
                "(3, 7, 1), " +
                "(3, 8, 1), " +
                "(3, 9, 1), " +
                "(1, 10, 0.70), " +
                "(1, 11, 0.75), " +
                "(2, 12, 0.80), " +
                "(1, 13, 0.75), " +
                "(2, 13, 0.75), " +
                "(2, 14, 0.80), " +
                "(2, 15, 0.85), " +
                "(2, 16, 0.80), " +
                "(3, 16, 0.80), " +
                "(3, 17, 0.85), " +
                "(3, 18, 0.90), " +
                "(1, 19, 0.30), " +
                "(1, 20, 0.50), " +
                "(1, 21, 0.70), " +
                "(1, 22, 0.60), " +
                "(2, 22, 0.60), " +
                "(1, 23, 0.70), " +
                "(2, 23, 0.70), " +
                "(2, 24, 0.75), " +
                "(2, 25, 0.70), " +
                "(2, 26, 0.75), " +
                "(2, 27, 0.80) ";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.execute();
        database.close();
    }

    public void insertData(String namaB, String lantai, String thn, String alamatB, String prov,
                           String kota, String kecamatan, String kodePos, String lati,
                           String longi, byte[] image, String nama, String alamat, String hp, String id_global){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "INSERT INTO data_bangunan VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,id_global);
        statement.bindString(2,namaB);
        statement.bindString(3,lantai);
        statement.bindString(4,thn);
        statement.bindString(5,alamatB);
        statement.bindString(6,prov);
        statement.bindString(7,kota);
        statement.bindString(8,kecamatan);
        statement.bindString(9,kodePos);
        statement.bindString(10,lati);
        statement.bindString(11,longi);
        statement.bindBlob(12,image);
        statement.bindString(13,nama);
        statement.bindString(14,alamat);
        statement.bindString(15,hp);

        statement.executeInsert();

    }

    public void insertUsers(){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO data_user VALUES(NULL,'users@gmail.com','masuk',0)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.executeInsert();
    }

    public void sudahMasuk(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE data_user SET status = 1 WHERE id_user = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindDouble(1,(double) id);
        statement.execute();
        database.close();
    }
    public void sudahKeluar(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE data_user SET status = 0 WHERE id_user = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindDouble(1,(double) id);
        statement.execute();
        database.close();
    }

    public void updateDataLevel(String data, double persen, String id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE data_bangunan SET hasil_diagnosis = ?, tingkat_kepercayaan = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1,data);
        statement.bindDouble(2, persen);
        statement.bindString(3, id);

        statement.execute();
        database.close();

    }



    public void insertDataKerusakan (String id_bangunan, int struktur, int level_kerusakan){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "INSERT INTO data_kerusakan VALUES(NULL,?,?,?,NULL)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,id_bangunan);
        statement.bindDouble(2,(double)struktur);
        statement.bindDouble(3,(double)level_kerusakan);

        statement.executeInsert();

    }
    public void insertDataGambar (String data){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "INSERT INTO data_gambar VALUES(?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,data);

        statement.executeInsert();
    }

    public  void updateGambarRetakan(int id, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String  sql = "UPDATE data_kerusakan SET  poto_kondisi = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindBlob(1,image);
        statement.bindDouble(2, (double)id);

        statement.execute();
        database.close();
    }

    public void updateData(String namaB, String lantai, String thn, String alamatB, String prov, String kota,
                           String kec, String pos, String lati,
                           String longi, byte[] image, String nama, String alamat, String hp, String id){

        SQLiteDatabase database = getWritableDatabase();

        String  sql = "UPDATE data_bangunan SET nama_bangunan = ?, jumlah_lantai = ?, tahun = ?," +
                "alamat_bangunan = ?, provinsi = ?, kota = ?, kecamatan = ?, kode_pos = ?, latitude = ?, " +
                "longitude = ?, poto = ?, nama = ?, alamat = ?, nomor_hp = ? WHERE id = ?";

        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1,namaB);
        statement.bindString(2,lantai);
        statement.bindString(3,thn);
        statement.bindString(4,alamatB);
        statement.bindString(5,prov);
        statement.bindString(6,kota);
        statement.bindString(7,kec);
        statement.bindString(8,pos);
        statement.bindString(9,lati);
        statement.bindString(10,longi);
        statement.bindBlob(11,image);
        statement.bindString(12,nama);
        statement.bindString(13,alamat);
        statement.bindString(14,hp);
        statement.bindString(15,id);

        statement.execute();
        database.close();

    }

    public void updateDataKerusakan (int level, int id){
        System.out.println("ini dddddiaaa "+level+"  "+id);

        SQLiteDatabase database = getWritableDatabase();

        String  sql = "UPDATE data_kerusakan SET level_kerusakan = ? WHERE id = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindDouble(1,(double)level);
        statement.bindDouble(2,(double)id);
        statement.execute();
        database.close();

    }

    public void deleteData(String id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM data_bangunan WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, id);

        statement.execute();
        database.close();
    }

    public void deleteDataUpload(){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM data_bangunan WHERE tingkat_kepercayaan != 0 ";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.execute();
        database.close();
    }

    public void deleteData2(String id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM data_kerusakan WHERE id_bangunan = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, id);

        statement.execute();
        database.close();
    }

    public void deleteDataKerusakan(int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM data_kerusakan WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return  database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

