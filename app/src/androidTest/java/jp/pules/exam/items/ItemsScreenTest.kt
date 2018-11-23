package jp.pules.exam.items

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.NoActivityResumedException
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import jp.pules.exam.Injection
import jp.pules.exam.TestUtils
import junit.framework.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the items screen, the main screen which contains a list of all items.
 */
@RunWith(AndroidJUnit4::class) @LargeTest
class ItemsScreenTest {

    @Rule
    @JvmField var itemsActivityTestRule = object :
            ActivityTestRule<ItemsActivity>(ItemsActivity::class.java) {

        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            Injection.provideItemsRepository(InstrumentationRegistry.getTargetContext())
                    .deleteAllItems()
        }
    }

    @Test fun orientationChange() {
        // 画面の向きを変えてもクラッシュしないこと
        try {
            TestUtils.rotateOrientation(itemsActivityTestRule.activity)
            // Test OK
        } catch (e: NoActivityResumedException) {
            fail("crash")
        }

    }

}