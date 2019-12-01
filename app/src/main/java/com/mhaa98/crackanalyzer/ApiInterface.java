package com.mhaa98.crackanalyzer;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("insert_bangunan.php")
    Call<ValueModel> insertData(@Part("bgn_id") RequestBody id_b,@Part("bgn_nama") RequestBody nama_b,
                                @Part("bgn_lantai") RequestBody lantai, @Part("bgn_tahun") RequestBody thn, @Part("bgn_alamat") RequestBody alamat_b,
                                @Part("bgn_provinsi") RequestBody prov,@Part("bgn_kota") RequestBody kota,@Part("bgn_kecamatan") RequestBody kec,
                                @Part("bgn_kodepos") RequestBody pos, @Part("bgn_latitude") RequestBody lati, @Part("bgn_longitude") RequestBody longi,
                                @Part MultipartBody.Part photo, @Part("cp") RequestBody nama, @Part("cp_alamat") RequestBody alamat,
                                @Part("cp_hp") RequestBody nomor, @Part("bgn_hasil") RequestBody hasil,
                                @Part("bgn_kepercayaan") RequestBody kepercayaan);
    @Multipart
    @POST("insert_struktur.php")
    Call<ValueModel> insertStruktur(@Part("str_id") RequestBody str_id, @Part("str_bgn") RequestBody str_bgn,@Part("str_str") RequestBody str_str,
                                    @Part("str_level") RequestBody str_lvl,@Part MultipartBody.Part foto_str);

    @Multipart
    @POST("insert_struktur2.php")
    Call<ValueModel> insertStruktur2(@Part("str_id") RequestBody str_id, @Part("str_bgn") RequestBody str_bgn,
                                     @Part("str_str") RequestBody str_str, @Part("str_level") RequestBody str_lvl);
}