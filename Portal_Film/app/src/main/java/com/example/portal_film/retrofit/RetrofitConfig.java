package com.example.portal_film.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {
    //Berfungsi untuk get url dari API yg bersifat dinamic, seperti : jenis data,jml data, dll

    public static ApiService getApiService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        ApiService service = retrofit.create(ApiService.class);
        return service;
    };
}
