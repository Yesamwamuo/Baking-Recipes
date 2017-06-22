package com.mannysight.bakingrecipes;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.mannysight.bakingrecipes.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by wamuo on 6/20/2017.
 */

public class IdlingResourceMainActivityTest  {
    @Rule
public ActivityTestRule<MainActivity> mActivityTestRule =
        new ActivityTestRule<>(MainActivity.class);

        private IdlingResource mIdlingResource;


        // Registers any resource that needs to be synchronized with Espresso before the test is run.
        @Before
        public void registerIdlingResource() {
            mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
            // To prove that the test fails, omit this call:
            Espresso.registerIdlingResources(mIdlingResource);
        }

        @Test
        public void idlingResourceTest() {
        onView(withId(R.id.recyclerview_recipes))
        .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

            onView(withId(R.id.recyclerview_steps))
                    .check(matches(isDisplayed()));
        }

        // Remember to unregister resources when not needed to avoid malfunction.
        @After
        public void unregisterIdlingResource() {
            if (mIdlingResource != null) {
                Espresso.unregisterIdlingResources(mIdlingResource);
            }
        }
}