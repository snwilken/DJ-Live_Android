package com.djlive.djlive;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import static android.support.test.espresso.intent.Intents.intended;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class UserSetupActivityTest {

    @Rule
    public ActivityTestRule<UserSetupActivity> mActivityTestRule =
            new ActivityTestRule<>(UserSetupActivity.class);

    @Test
    public void userSetupActivityTest() {


    }

    @Test
    public void validateHostButtonExists() {

        //validates that buttons exist
        onView(withId(R.id.btnHost)).check(matches(notNullValue() ));
        onView(withId(R.id.btnHost)).check(matches(withText("Host A Playlist")));
        onView(withId(R.id.btnHost)).perform(click());
    }

    @Test
    public void validateJoinButtonExists() {

        onView(withId(R.id.btnJoin)).check(matches(notNullValue() ));
        onView(withId(R.id.btnJoin)).check(matches(withText("Join A Playlist")));
        onView(withId(R.id.btnJoin)).perform(click());
    }



}

