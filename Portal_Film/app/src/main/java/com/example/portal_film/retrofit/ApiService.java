package com.example.portal_film.retrofit;

import com.example.portal_film.model.ResponseMovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    //Berfungsi untuk get url dari API yg bersifat static, seperti : key,sumber, dll
    @GET("movie/popular")
    Call<ResponseMovie> ambildataMovie (
      @Query("api_key") String apikey
    );
}
