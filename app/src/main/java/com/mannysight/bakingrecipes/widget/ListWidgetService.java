package com.mannysight.bakingrecipes.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mannysight.bakingrecipes.R;
import com.mannysight.bakingrecipes.model.Ingredient;
import com.mannysight.bakingrecipes.model.Recipe;
import com.mannysight.bakingrecipes.provider.RecipeContract;
import com.mannysight.bakingrecipes.provider.RecipeProvider;
import com.mannysight.bakingrecipes.utilities.JsonUtils;

import java.util.ArrayList;

/**
 * Created by wamuo on 6/17/2017.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}


class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String WIDGET_RECIPE_ID = "WIDGET_RECIPE_ID";
    private static final int RECIPE_INVALID_ID = -1;
    private Context mContext;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private Recipe recipe;
    private int recipe_id;

    public ListRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        recipe_id = intent.getIntExtra(WIDGET_RECIPE_ID, RECIPE_INVALID_ID);
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        Cursor recipeCursor = mContext.getContentResolver().query(
                RecipeProvider.Recipes.withId(recipe_id),
                null,
                null,
                null,
                RecipeContract.RecipeEntry.COLUMN_ID
        );

        ArrayList<Recipe> recipeList = JsonUtils.getRecipeListFromCursor(recipeCursor, mContext);

        recipe = recipeList.get(0);
        ingredients = (ArrayList<Ingredient>) recipe.getIngredients();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredients == null) return 0;
        return ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (ingredients == null || ingredients.size() == 0) return null;

        Ingredient ingredient = ingredients.get(i);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_list_item);

        String ingredientText = ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getIngredient();
        views.setTextViewText(R.id.widget_ingredient, ingredientText);

        Bundle extras = new Bundle();
        extras.putParcelable(Intent.EXTRA_TEXT, recipe);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_ingredient, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}

