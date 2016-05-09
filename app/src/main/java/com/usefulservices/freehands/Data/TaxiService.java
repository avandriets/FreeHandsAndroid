package com.usefulservices.freehands.Data;

import com.usefulservices.freehands.Utils.Utility;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface TaxiService {

    @Headers({"Content-Type: application/json"})
    @GET(Utility.CityURL)
    Call<List<City>> getCity();

    @Headers({"Content-Type: application/json"})
    @GET(Utility.CountryURL)
    Call<List<Country>> getCountry();

    @Headers({"Content-Type: application/json"})
    @GET(Utility.CarTypesURL)
    Call<List<CarTypes>> getCarTypes();

    @Headers({"Content-Type: application/json"})
    @POST(Utility.REGISTER_URL)
    Call<ResponseBody> registerDevice(@Body HashMap<String, String> body, @Header("Authorization") String authorization);

    @Headers({"Content-Type: application/json"})
    @POST(Utility.UNREGISTER_URL)
    Call<ResponseBody> unregisterDevice(@Body HashMap<String, String> body, @Header("Authorization") String authorization);
}