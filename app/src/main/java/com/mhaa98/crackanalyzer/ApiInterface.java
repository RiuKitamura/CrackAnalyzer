package com.mhaa98.crackanalyzer;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("insert_data.php")
    Call<ValueModel> insertData(@Part("bgn_nama") RequestBody nama_b, @Part("bgn_lantai") RequestBody lantai,
                                @Part("bgn_tahun") RequestBody thn, @Part("bgn_alamat") RequestBody alamat_b,
                                @Part("bgn_latitude") RequestBody lati, @Part("bgn_longitude") RequestBody longi,
                                @Part MultipartBody.Part photo, @Part("cp") RequestBody nama, @Part("cp_alamat") RequestBody alamat,
                                @Part("cp_hp") RequestBody nomor, @Part("bgn_hasil") RequestBody hasil,
                                @Part("bgn_kepercayaan") RequestBody kepercayaan);
}