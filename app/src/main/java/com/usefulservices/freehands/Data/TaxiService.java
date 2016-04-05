package com.usefulservices.freehands.Data;


import com.usefulservices.freehands.Utils.Utility;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TaxiService {

    @Headers({"Content-Type: application/json"})
    @POST(Utility.REGISTER_URL)
    Call<ResponseBody> registerDevice(@Body HashMap<String, String> body, @Header("Authorization") String authorization);

    @Headers({"Content-Type: application/json"})
    @POST(Utility.UNREGISTER_URL)
    Call<ResponseBody> unregisterDevice(@Body HashMap<String, String> body, @Header("Authorization") String authorization);
}