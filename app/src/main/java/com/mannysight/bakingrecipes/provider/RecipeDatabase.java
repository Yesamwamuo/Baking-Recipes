package com.mannysight.bakingrecipes.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by wamuo on 6/12/2017.
 */
@Database(version = RecipeDatabase.VERSION)
public class RecipeDatabase {

    public static final int VERSION = 1;

    @Table(RecipeContract.IngredientEntry.class)
    public static final String RECIPE_INGREDIENTS = "ingredients";

    @Table(RecipeContract.StepEntry.class)
    public static final String RECIPE_STEPS = "steps";

    @Table(RecipeContract.RecipeEntry.class)
    public static final String RECIPES = "recipes";
}