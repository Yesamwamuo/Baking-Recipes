package com.mannysight.bakingrecipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.mannysight.bakingrecipes.R;
import com.mannysight.bakingrecipes.fragment.DetailFragment;
import com.mannysight.bakingrecipes.model.Recipe;

public class StepsActivity extends AppCompatActivity {

    public boolean mTwoPane;
    private Recipe recipe;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);

        actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                recipe = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
                actionBar.setTitle(recipe.getName());
            }
        }

        mTwoPane = getResources().getBoolean(R.bool.isTablet);

        if (mTwoPane) {
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setStepIndex(0);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.detail_fragment_container, detailFragment)
                    .commit();
        }
    }
}
