package jp.pules.exam.itemdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.pules.exam.Injection
import jp.pules.exam.R
import jp.pules.exam.items.ItemsActivity
import kotlinx.android.synthetic.main.itemdetail_activity.*

/**
 * An activity representing a single Event detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [ItemsActivity].
 */
class ItemDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ITEM_ID = "ITEM_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.itemdetail_activity)

        // ツールバー
        setSupportActionBar(detail_toolbar)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val itemId : String = intent.getStringExtra(EXTRA_ITEM_ID)

        var itemDetailFragment = supportFragmentManager.findFragmentById(R.id.item_detail_container) as ItemDetailFragment?

        if (itemDetailFragment == null) {
            itemDetailFragment = ItemDetailFragment.newInstance(itemId)

            supportFragmentManager.beginTransaction()
                    .add(R.id.item_detail_container, itemDetailFragment)
                    .commit()
        }

        ItemDetailPresenter(
                Injection.provideUseCaseHandler(),
                itemId,
                itemDetailFragment,
                Injection.provideGetItem(applicationContext)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
