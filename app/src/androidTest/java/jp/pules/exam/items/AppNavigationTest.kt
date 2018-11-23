package jp.pules.exam.items

import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.NoActivityResumedException
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    @Rule
    @JvmField var activityTestRule = ActivityTestRule(ItemsActivity::class.java)

    @Test fun backFromItemsScreen_ExitsApp() {
        assertPressingBackExitsApp()
    }

    private fun assertPressingBackExitsApp() {
        try {
            pressBack()
            fail("Should kill the app and throw an exception")
        } catch (e: NoActivityResumedException) {
            // Test OK
        }

    }

}