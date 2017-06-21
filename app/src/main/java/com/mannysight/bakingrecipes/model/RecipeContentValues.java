package com.mannysight.bakingrecipes.model;

import android.content.ContentValues;

/**
 * Created by wamuo on 6/16/2017.
 */

public class RecipeContentValues {

    private ContentValues[] recipes;
    private ContentValues[] steps;
    private ContentValues[] ingredients;

    public ContentValues[] getRecipes() {
        return recipes;
    }

    public void setRecipes(ContentValues[] recipes) {
        this.recipes = recipes;
    }

    public ContentValues[] getSteps() {
        return steps;
    }

    public void setSteps(ContentValues[] steps) {
        this.steps = steps;
    }

    public ContentValues[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(ContentValues[] ingredients) {
        this.ingredients = ingredients;
    }
}
