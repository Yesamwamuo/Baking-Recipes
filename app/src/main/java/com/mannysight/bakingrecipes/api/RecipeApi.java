package com.mannysight.bakingrecipes.api;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by wamuo on 6/23/2017.
 */

public interface RecipeApi {
        @GET("/topher/2017/May/59121517_baking/baking.json")
        Call<String> getAllRecipes();
}
