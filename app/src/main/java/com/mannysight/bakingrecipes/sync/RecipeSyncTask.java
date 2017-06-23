package com.mannysight.bakingrecipes.sync;

import android.content.ContentResolver;
import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.mannysight.bakingrecipes.model.Recipe;
import com.mannysight.bakingrecipes.model.RecipeContentValues;
import com.mannysight.bakingrecipes.provider.RecipeProvider;
import com.mannysight.bakingrecipes.utilities.JsonUtils;
import com.mannysight.bakingrecipes.utilities.NetworkUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by wamuo on 6/16/2017.
 */

public class RecipeSyncTask {

    synchronized public static void syncRecipe(Context context) {

        try {


            String jsonRecipeResponse = NetworkUtils
                    .getResponseFromHttpUrl();

            Type listType = new TypeToken<List<Recipe>>() {
            }.getType();

            List<Recipe> recipes = JsonUtils.getRecipeListFromJson(jsonRecipeResponse, listType);

            RecipeContentValues values = JsonUtils
                    .getRecipeContentValues(recipes);

            if (values.getRecipes() != null && values.getRecipes().length != 0) {
                ContentResolver recipeContentResolver = context.getContentResolver();

                recipeContentResolver.delete(
                        RecipeProvider.Recipes.CONTENT_URI,
                        null,
                        null);

                recipeContentResolver.delete(
                        RecipeProvider.RecipeIngredients.CONTENT_URI,
                        null,
                        null);
                recipeContentResolver.delete(
                        RecipeProvider.RecipeSteps.CONTENT_URI,
                        null,
                        null);

                recipeContentResolver.bulkInsert(
                        RecipeProvider.Recipes.CONTENT_URI,
                        values.getRecipes());
                recipeContentResolver.bulkInsert(
                        RecipeProvider.RecipeIngredients.CONTENT_URI,
                        values.getIngredients());
                recipeContentResolver.bulkInsert(
                        RecipeProvider.RecipeSteps.CONTENT_URI,
                        values.getSteps());

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
