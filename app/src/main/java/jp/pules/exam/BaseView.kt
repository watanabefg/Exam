package jp.pules.exam

interface BaseView<T : BasePresenter> {

    fun setPresenter(presenter: T)

}