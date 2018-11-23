package jp.pules.exam.items

import jp.pules.exam.UseCase
import jp.pules.exam.UseCaseHandler
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.items.domain.usecase.GetItems

class ItemsPresenter(useCaseHandler: UseCaseHandler,
                     itemsView: ItemsContract.View, getItems: GetItems) : ItemsContract.Presenter {

    private var mItemsView: ItemsContract.View = itemsView
    private var mGetItems: GetItems = getItems

    var mFirstLoad : Boolean = true

    private var mUseCaseHandler: UseCaseHandler = useCaseHandler

    init {
        mItemsView.setPresenter(this)
    }

    override fun start() {
        loadItems(false)
    }

    override fun loadItems(forceUpdate: Boolean) {
        loadItems(forceUpdate || mFirstLoad, true)
        mFirstLoad = false
    }

    private fun loadItems(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            // ローディングインディケーターを表示する
            mItemsView.setLoadingIndicator(true)
        }

        val requestValue = GetItems.RequestValues(forceUpdate)

        mUseCaseHandler.execute(
                mGetItems,
                requestValue,
                object : UseCase.UseCaseCallback<GetItems.ResponseValue> {
                    override fun onSuccess(response: GetItems.ResponseValue) {
                        val items = response.items
                        if (!mItemsView.isActive) {
                            return
                        }
                        if (showLoadingUI) {
                            mItemsView.setLoadingIndicator(false)
                        }

                        processItems(items)
                    }

                    override fun onError() {
                        if (!mItemsView.isActive) {
                            return
                        }
                        mItemsView.showLoadingItemsError()
                    }
                }
        )
    }

    private fun processItems(items: List<Item>) {
        if (items.isEmpty()) {
            processEmptyItems()
        } else {
            mItemsView.showItems(items)
        }
    }

    private fun processEmptyItems() {
        mItemsView.showNoItems()
    }

    override fun openItemDetails(requestedItem: Item) {
        mItemsView.showItemDetailsUi(requestedItem.id)
    }

    override fun result(requestCode: Int, resultCode: Int) {
    }

}