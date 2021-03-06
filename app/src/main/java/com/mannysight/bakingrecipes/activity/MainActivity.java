package com.mannysight.bakingrecipes.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.mannysight.bakingrecipes.R;
import com.mannysight.bakingrecipes.adapter.RecipeAdapter;
import com.mannysight.bakingrecipes.model.Recipe;
import com.mannysight.bakingrecipes.sync.RecipeSyncUtils;
import com.mannysight.bakingrecipes.utilities.JsonUtils;
import com.mannysight.bakingrecipes.utilities.NetworkUtils;
import com.mannysight.bakingrecipes.utilities.RecipeTestDownloader;
import com.mannysight.bakingrecipes.utilities.SimpleIdlingResource;
import com.mannysight.bakingrecipes.widget.RecipeWidgetProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler
        , LoaderManager.LoaderCallbacks<ArrayList<Recipe>>,
        RecipeTestDownloader.DelayerCallback {

    private static final int RECIPE_LIST_LOADER = 4795;
    @BindView(R.id.recyclerview_recipes)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    private RecipeAdapter mRecipeAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        boolean mTwoPane = getResources().getBoolean(R.bool.isTablet);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && !mTwoPane) {
            layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && mTwoPane) {
            layoutManager = new GridLayoutManager(this, 3);
            mRecyclerView.setLayoutManager(layoutManager);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecipeAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mRecipeAdapter);

        getSupportLoaderManager().initLoader(RECIPE_LIST_LOADER, null, this);

        RecipeSyncUtils.initialize(this);
        getIdlingResource();

    }

    @Override
    protected void onStart() {
        super.onStart();
        RecipeTestDownloader.downloadRecipe(this, MainActivity.this, mIdlingResource);
    }

    @Override
    public void onClick(Recipe recipe) {
        updateRecipeWidget(recipe);
        Context context = this;
        Class destinationClass = StepsActivity.class;
        Intent intentToStartStepsActivity = new Intent(context, destinationClass);
        intentToStartStepsActivity.putExtra(Intent.EXTRA_TEXT, recipe);
        startActivity(intentToStartStepsActivity);
    }

    public void updateRecipeWidget(Recipe recipe) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, recipe, appWidgetIds);
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<ArrayList<Recipe>>(this) {

            ArrayList<Recipe> recipes = null;

            @Override
            protected void onStartLoading() {

                if (recipes != null) {
                    deliverResult(recipes);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Recipe> loadInBackground() {
                try {
                    String jsonRecipeResponse = NetworkUtils
                            .getResponseFromHttpUrl();

                    Type listType = new TypeToken<List<Recipe>>() {
                    }.getType();

                    List<Recipe> recipes = JsonUtils.getRecipeListFromJson(jsonRecipeResponse, listType);

                    return (ArrayList<Recipe>) recipes;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<Recipe> data) {
                recipes = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecipeAdapter.setRecipeList(data);

        if (null == data) {
            showErrorMessage();
        } else {
            showMovieListDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {

    }

    private void showMovieListDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDone(ArrayList<Recipe> recipes) {
//        mRecipeAdapter = new RecipeAdapter(this);
//        mRecyclerView.setAdapter(mRecipeAdapter);
//        mRecipeAdapter.setRecipeList(recipes);
    }
}
