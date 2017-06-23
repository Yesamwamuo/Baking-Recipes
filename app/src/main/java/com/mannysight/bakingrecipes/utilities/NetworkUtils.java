package com.mannysight.bakingrecipes.utilities;

import com.mannysight.bakingrecipes.api.RecipeApi;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by wamuo on 6/10/2017.
 */

public class NetworkUtils {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";

    public static String getResponseFromHttpUrl() throws IOException {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        RecipeApi api = retrofit.create(RecipeApi.class);

        Call<String> call = api.getAllRecipes();
        return  call.execute().body();
    }
}

