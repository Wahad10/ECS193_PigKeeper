package com.ucdavis.pigkeeper

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.adevinta.android.barista.interaction.BaristaSleepInteractions.sleep
import org.hamcrest.Matcher
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
/**
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.ucdavis.pigkeeper", appContext.packageName)
    }
**/

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.ucdavis.pigkeeper", appContext.packageName)
    }


    @Test
    fun read_title() {
        TestHelper.waitForText("Pig Game")
    }
}


object TestHelper {
    private fun searchFor(matcher: Matcher<View>): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }
            override fun getDescription(): String {
                return "searching for view $matcher in the root view"
            }
            override fun perform(uiController: UiController, view: View) {
                val childViews: Iterable<View> = TreeIterables.breadthFirstViewTraversal(view)
                childViews.forEach {
                    if (matcher.matches(it)) {
                        return
                    }
                }
                throw NoMatchingViewException.Builder()
                    .withRootView(view)
                    .withViewMatcher(matcher)
                    .build()
            }
        }
    }

    @JvmStatic
    fun waitForView(
        viewMatcher: Matcher<View>,
        waitMillis: Int = 5000,
        waitMillisPerTry: Long = 100
    ): ViewInteraction {
        val maxTries = waitMillis / waitMillisPerTry.toInt()
        var ex: Exception = Exception("Error finding a view matching $viewMatcher")
        for (i in 0..maxTries) {
            try {
                onView(isRoot()).perform(searchFor(viewMatcher))
                return onView(viewMatcher)
            } catch (e: Exception) {
                ex = e
                sleep(waitMillisPerTry)
            }
        }
        throw ex
    }

    @JvmStatic
    fun waitForText(text: String): ViewInteraction {
        return waitForView(withText(text))
    }
/**
    @JvmStatic
    fun scrollRecyclerViewTo(text: String) {
        onView(withId(R.id.recyclerview))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(text))
                )
            )
    }**/
}