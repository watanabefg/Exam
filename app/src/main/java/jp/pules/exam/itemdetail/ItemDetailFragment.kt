package jp.pules.exam.itemdetail

import android.os.Bundle
import android.support.v4.app.Fragment
import jp.pules.exam.R
import android.view.*
import android.webkit.WebView
import android.widget.TextView

class ItemDetailFragment : Fragment(), ItemDetailContract.View {

    private var mPresenter:ItemDetailContract.Presenter? = null

    private var mDetailTitle:TextView? = null

    private var mDetailRenderedBody: WebView? = null

    companion object {

        private const val ARGUMENT_ITEM_ID = "ITEM_ID"

        fun newInstance(itemId: String): ItemDetailFragment {
            val arguments = Bundle()
            arguments.putString(ARGUMENT_ITEM_ID, itemId)
            val fragment = ItemDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.itemdetail_fragment, container, false)
        setHasOptionsMenu(true)

        mDetailTitle = root.findViewById(R.id.item_detail_title)
        mDetailRenderedBody = root.findViewById(R.id.item_detail_renderedbody)

        return root
    }

    override fun setPresenter(presenter: ItemDetailContract.Presenter) {
        mPresenter = presenter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return false
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            mDetailTitle?.text = ""
            mDetailRenderedBody?.loadData(getString(R.string.loading), "text/html", null)
        }
    }

    override fun hideRenderedBody() {
        mDetailRenderedBody?.visibility = View.GONE
    }

    override fun hideTitle() {
        mDetailTitle?.visibility = View.GONE
    }

    override fun showRenderedBody(renderedBody: String) {
        mDetailRenderedBody?.visibility = View.VISIBLE
        mDetailRenderedBody?.loadData(renderedBody, "text/html", null)
    }

    override fun showTitle(title: String) {
        mDetailTitle?.visibility = View.VISIBLE
        mDetailTitle?.text = title
    }

    override fun showMissingItem() {
        mDetailTitle?.text = ""
        mDetailRenderedBody?.loadData(getString(R.string.no_item), "text/html", null)
    }
    
    override val isActive : Boolean
        get() = isAdded
}