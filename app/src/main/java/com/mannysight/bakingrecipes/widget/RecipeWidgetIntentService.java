package com.mannysight.bakingrecipes.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.mannysight.bakingrecipes.model.Recipe;
import com.mannysight.bakingrecipes.provider.RecipeContract;
import com.mannysight.bakingrecipes.provider.RecipeProvider;
import com.mannysight.bakingrecipes.utilities.JsonUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by wamuo on 6/16/2017.
 */

public class RecipeWidgetIntentService extends IntentService {

    public static final String ACTION_GET_INGREDIENTS = "com.mannysight.bakingrecipes.action.get.ingredients";

    public static void startActionGetIngredients(Context context) {

        Intent intent = new Intent(context, RecipeWidgetIntentService.class);
        intent.setAction(ACTION_GET_INGREDIENTS);
        context.startService(intent);
    }


    public RecipeWidgetIntentService() {
        super("RecipeWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_INGREDIENTS.equals(action)) {
                handleGetIngredients();
            }
        }
    }

    private void handleGetIngredients() {

        Cursor recipeCursor = getContentResolver().query(
                RecipeProvider.Recipes.CONTENT_URI,
                null,
                null,
                null,
                RecipeContract.RecipeEntry.COLUMN_ID
        );

        ArrayList<Recipe> recipeList = JsonUtils.getRecipeListFromCursor(recipeCursor, getApplicationContext());

        Recipe randRecipe = recipeList.get(new Random().nextInt(recipeList.size()));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, randRecipe, appWidgetIds);
    }
}


