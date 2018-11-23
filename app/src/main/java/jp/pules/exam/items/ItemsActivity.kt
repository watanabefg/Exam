package jp.pules.exam.items

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.items_activity.*
import jp.pules.exam.utils.EspressoIdlingResource
import android.support.test.espresso.IdlingResource
import android.support.annotation.VisibleForTesting
import jp.pules.exam.*


class ItemsActivity : AppCompatActivity() {

    private var mItemsPresenter : ItemsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.items_activity)

        // ツールバーをセット
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.new_items)

        // TODO: タブレット用レイアウトは後回し
        var itemsFragment =
                supportFragmentManager.findFragmentById(R.id.frameLayout) as ItemsFragment?

        if (itemsFragment == null){
            itemsFragment = ItemsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayout, itemsFragment)
                .commit()
        }

        // TODO: Daggerがわかれば書き直す
        mItemsPresenter = ItemsPresenter(
                Injection.provideUseCaseHandler(),
                itemsFragment,
                Injection.provideGetItems(applicationContext)
        )

    }

    @VisibleForTesting
    fun getCountingIdlingResource(): IdlingResource {
        return EspressoIdlingResource.idlingResource
    }
}