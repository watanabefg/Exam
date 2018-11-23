package jp.pules.exam.items

import android.support.v4.app.Fragment
import jp.pules.exam.items.domain.model.Item
import android.support.design.widget.Snackbar
import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.content.ContextCompat
import android.os.Bundle
import android.view.*
import android.widget.*
import android.view.MenuInflater
import jp.pules.exam.R
import jp.pules.exam.itemdetail.ItemDetailActivity


class ItemsFragment : Fragment(), ItemsContract.View {
    private lateinit var presenter: ItemsContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    private lateinit var noItemsView: View
    private lateinit var noItemIcon : ImageView
    private lateinit var noItemMainView : TextView
    private lateinit var itemsView : LinearLayout

    internal var itemListener: ItemListener = object : ItemListener {
        override fun onItemClick(clickedItem: Item) {
            presenter.openItemDetails(clickedItem)
        }
    }

    private val listAdapter: ItemsAdapter = ItemsAdapter(ArrayList(0), itemListener)

    override fun setPresenter(presenter: ItemsContract.Presenter) {
        this.presenter = presenter
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.items_fragment, container, false)

        with(root){
            val listView = findViewById<ListView>(R.id.items_list).apply {adapter = listAdapter}

            val swipeRefreshLayout =
                    findViewById<ScrollChildSwipeRefreshLayout>(R.id.refresh_layout)
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(activity!!, R.color.colorPrimary),
                    ContextCompat.getColor(activity!!, R.color.colorAccent),
                    ContextCompat.getColor(activity!!, R.color.colorPrimaryDark)
            )

            swipeRefreshLayout.scrollUpChild = listView
            swipeRefreshLayout.setOnRefreshListener {
                presenter.loadItems(false)
            }

            itemsView = findViewById(R.id.itemsLL)

            noItemsView = findViewById(R.id.noItems)
            noItemIcon = findViewById(R.id.noItemsIcon)
            noItemMainView = findViewById(R.id.noItemsMain)
        }

        setHasOptionsMenu(true)

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> presenter.loadItems(true)
            else -> {}
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.items_fragment_menu, menu)
    }


    override fun setLoadingIndicator(active: Boolean) {
        if (view == null) {
            return
        }
        val srl = view!!.findViewById(R.id.refresh_layout) as SwipeRefreshLayout

        srl.post { srl.isRefreshing = active }
    }

    override fun showItems(items: List<Item>) {
        listAdapter.items = items

        itemsView.visibility = View.VISIBLE
        noItemsView.visibility = View.GONE
    }

    override fun showItemDetailsUi(itemId: String) {
        val intent = Intent(context, ItemDetailActivity::class.java).apply {
            putExtra(ItemDetailActivity.EXTRA_ITEM_ID, itemId)
        }
        startActivity(intent)
    }

    override fun showLoadingItemsError() {
        showMessage(getString(R.string.loading_items_error))
    }

    private fun showMessage(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showNoItems() {
        showNoItemsViews(
                resources.getString(R.string.no_items_all),
                R.drawable.ic_iconfinder_icon_45_note_list_315263
        )
    }

    private fun showNoItemsViews(string: String, iconResource: Int) {
        itemsView.visibility = View.GONE
        noItemsView.visibility = View.VISIBLE

        noItemMainView.text = string
        noItemIcon.setImageResource(iconResource)
    }

    private class ItemsAdapter(items: List<Item>, private val itemListener: ItemListener)
        : BaseAdapter() {

        var items: List<Item> = items
            set(items) {
                field = items
                notifyDataSetChanged()
            }

        override fun getCount(): Int = items.size

        override fun getItem(i: Int) = items[i]

        override fun getItemId(id: Int): Long = id.toLong()

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val item = getItem(i)

            val rowView = view ?: LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_list_content, viewGroup, false)

            with(rowView.findViewById<TextView>(R.id.title)) {
                text = item.titleForList
            }

            rowView.setOnClickListener { itemListener.onItemClick(item) }
            return rowView
        }

    }

    interface ItemListener {
        fun onItemClick(clickedItem : Item)
    }

    companion object {
        fun newInstance() = ItemsFragment()
    }
}