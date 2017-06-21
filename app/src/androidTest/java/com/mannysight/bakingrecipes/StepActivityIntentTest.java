package com.mannysight.bakingrecipes;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static com.mannysight.bakingrecipes.StepsFragment.EXTRA_TEXT_STEP_INDEX;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by wamuo on 6/20/2017.
 */

public class StepActivityIntentTest {


    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule
            = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void clickStepSendsExtras() {

        onView(ViewMatchers.withId(R.id.recyclerview_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(ViewMatchers.withId(R.id.recyclerview_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        intended(allOf(
                hasExtra(EXTRA_TEXT_STEP_INDEX, 1)
        ));
    }
}
