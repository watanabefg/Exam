package jp.pules.exam.itemdetail

import jp.pules.exam.UseCase
import jp.pules.exam.UseCaseHandler
import jp.pules.exam.itemdetail.domain.usecase.GetItem
import jp.pules.exam.items.domain.model.Item

/**
 * Listens to user actions from the UI ([ItemDetailFragment]), retrieves the data and updates
 * the UI as required.
 */
class ItemDetailPresenter(useCaseHandler: UseCaseHandler,
                          private val mItemId: String,
                          itemDetailView: ItemDetailContract.View,
                          getItem: GetItem) : ItemDetailContract.Presenter {

    private var mItemDetailView: ItemDetailContract.View
    private var mUseCaseHandler: UseCaseHandler
    private var mGetItem: GetItem

    init {
        mUseCaseHandler = useCaseHandler
        mItemDetailView = itemDetailView
        mGetItem = getItem
        mItemDetailView.setPresenter(this)
    }

    override fun start() {
        openItem()
    }

    private fun openItem() {
        if (mItemId.isEmpty()) {
            mItemDetailView.showMissingItem()
            return
        }

        mItemDetailView.setLoadingIndicator(true)

        mUseCaseHandler.execute(mGetItem, GetItem.RequestValues(mItemId),
                object : UseCase.UseCaseCallback<GetItem.ResponseValue> {
                    override fun onSuccess(response: GetItem.ResponseValue) {
                        val item = response.item

                        // The view may not be able to handle UI updates anymore
                        if (!mItemDetailView.isActive) {
                            return
                        }
                        mItemDetailView.setLoadingIndicator(false)
                        showItem(item)
                    }

                    override fun onError() {
                        // The view may not be able to handle UI updates anymore
                        if (!mItemDetailView.isActive) {
                            return
                        }
                        mItemDetailView.showMissingItem()
                    }
                })
    }

    private fun showItem(item: Item) {
        with(mItemDetailView) {
            if (mItemId.isEmpty()){
                hideTitle()
                hideRenderedBody()
            } else {
                showTitle(item.title)
                showRenderedBody(item.rendered_body)
            }
        }
    }
}