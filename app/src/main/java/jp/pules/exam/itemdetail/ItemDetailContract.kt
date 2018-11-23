package jp.pules.exam.itemdetail

import jp.pules.exam.BasePresenter
import jp.pules.exam.BaseView


/**
 * This specifies the contract between the view and the presenter.
 */
interface ItemDetailContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMissingItem()

        fun hideTitle()

        fun showTitle(title: String)

        fun hideRenderedBody()

        fun showRenderedBody(renderedBody: String)

    }

    interface Presenter : BasePresenter
}