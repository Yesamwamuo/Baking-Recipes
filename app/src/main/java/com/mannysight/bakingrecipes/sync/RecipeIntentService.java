package com.mannysight.bakingrecipes.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by wamuo on 6/16/2017.
 */

public class RecipeIntentService extends IntentService {

    public RecipeIntentService() {
        super("RecipeIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RecipeSyncTask.syncRecipe(this);
    }
}