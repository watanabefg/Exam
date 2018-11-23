package jp.pules.exam.items

import jp.pules.exam.BasePresenter
import jp.pules.exam.BaseView
import jp.pules.exam.items.domain.model.Item

interface ItemsContract {
    interface View : BaseView<Presenter> {
        var isActive: Boolean
        fun setLoadingIndicator(active: Boolean)
        fun showItems(items: List<Item>)
        fun showItemDetailsUi(itemId : String)
        fun showLoadingItemsError()
        fun showNoItems()
    }

    interface Presenter : BasePresenter {
        fun result(requestCode: Int, resultCode: Int)
        fun loadItems(forceUpdate: Boolean)
        fun openItemDetails(requestedItem: Item)
    }
}