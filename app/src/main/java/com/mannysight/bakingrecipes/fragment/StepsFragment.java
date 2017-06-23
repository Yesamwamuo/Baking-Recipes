package com.mannysight.bakingrecipes.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mannysight.bakingrecipes.R;
import com.mannysight.bakingrecipes.activity.DetailActivity;
import com.mannysight.bakingrecipes.adapter.StepAdapter;
import com.mannysight.bakingrecipes.model.Ingredient;
import com.mannysight.bakingrecipes.model.Recipe;
import com.mannysight.bakingrecipes.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wamuo on 6/11/2017.
 */

public class StepsFragment extends Fragment implements StepAdapter.StepAdapterOnClickHandler {


    public static final String EXTRA_TEXT_STEP_INDEX = "EXTRA_TEXT_STEP_INDEX";
    @BindView(R.id.recyclerview_steps)
    RecyclerView stepsRv;

    @BindView(R.id.recipe_ingredients)
    TextView recipeIngredientsTv;

    private Context context;
    private ArrayList<Step> mSteps;
    private StepAdapter mStepAdapter;
    private Recipe recipe;

    private RecyclerView.LayoutManager layoutManager;
    private boolean mTwoPane;

    public StepsFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mTwoPane = getResources().getBoolean(R.bool.isTablet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        ButterKnife.bind(this, rootView);
        context = getContext();

        layoutManager = new LinearLayoutManager(context);
        stepsRv.setLayoutManager(layoutManager);

        stepsRv.setHasFixedSize(true);
        mStepAdapter = new StepAdapter(this);
        stepsRv.setAdapter(mStepAdapter);

        Intent intentThatStartedThisActivity = getActivity().getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                recipe = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
                mSteps = (ArrayList<Step>) recipe.getSteps();
            }
        }

        if (mSteps != null) {
            bindIngredients(recipe.getIngredients());
            mStepAdapter.setStepList(recipe);
        }
        return rootView;
    }

    private void bindIngredients(List<Ingredient> ingredients) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.ingredients_text));
        sb.append("\n ");
        for (Ingredient ingredient : ingredients) {
            String text = ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getIngredient();
            sb.append(text);
            sb.append("\n ");
        }
        recipeIngredientsTv.setText(sb.toString());
    }


    @Override
    public void onClick(Recipe recipe, int stepIndex) {

        if (mTwoPane) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setStepIndex(stepIndex);
            fragmentTransaction
                    .replace(R.id.detail_fragment_container, detailFragment)
                    .commit();
        } else {
            Class destinationClass = DetailActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, recipe);
            intentToStartDetailActivity.putExtra(EXTRA_TEXT_STEP_INDEX, stepIndex);
            startActivity(intentToStartDetailActivity);
        }
    }
}
