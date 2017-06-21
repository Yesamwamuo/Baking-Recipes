package com.mannysight.bakingrecipes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.mannysight.bakingrecipes.R;
import com.mannysight.bakingrecipes.StepsActivity;
import com.mannysight.bakingrecipes.model.Recipe;

import static com.mannysight.bakingrecipes.widget.ListRemoteViewsFactory.WIDGET_RECIPE_ID;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                Recipe randRecipe, int appWidgetId) {

        RemoteViews rv = getListRemoteView(context, randRecipe);

        String widgetRecipeTitle;
        if (randRecipe != null) {
            String recipeNameText = randRecipe.getName();
            widgetRecipeTitle = recipeNameText + " " + context.getString(R.string.ingredients_title_text);
        } else {
            widgetRecipeTitle = context.getString(R.string.ingredients_title_text);
        }
        rv.setTextViewText(R.id.widget_recipe_title, widgetRecipeTitle);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }


    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager,
                                           Recipe randRecipe, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, randRecipe, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RecipeWidgetIntentService.startActionGetIngredients(context);
    }

    private static RemoteViews getListRemoteView(Context context, Recipe randRecipe) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients_list);

        Intent intent = new Intent(context, ListWidgetService.class);

        intent.putExtra(WIDGET_RECIPE_ID, randRecipe.getId());
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        views.setRemoteAdapter(R.id.widget_list, intent);

        Intent appIntent = new Intent(context, StepsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list, appPendingIntent);

        views.setEmptyView(R.id.widget_list, R.id.widget_empty);
        return views;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

