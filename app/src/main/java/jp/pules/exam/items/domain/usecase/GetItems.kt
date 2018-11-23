package jp.pules.exam.items.domain.usecase

import jp.pules.exam.UseCase
import jp.pules.exam.data.source.ItemsDataSource
import jp.pules.exam.data.source.ItemsRepository
import jp.pules.exam.items.domain.model.Item

class GetItems(val itemsRepository: ItemsRepository) : UseCase<GetItems.RequestValues, GetItems.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {

        if (requestValues != null) {
            if (requestValues.isForceUpdate()) {
                itemsRepository.refreshItems()
            }

        }

        itemsRepository.getItems(object : ItemsDataSource.LoadItemsCallback {
            override fun onItemsLoaded(items: List<Item>) {
                val responseValue = ResponseValue(items)
                useCaseCallback!!.onSuccess(responseValue)
            }

            override fun onDataNotAvailable() {
                useCaseCallback!!.onError()
            }
        })

    }

    class RequestValues(private val forceUpdate: Boolean) : UseCase.RequestValues {
        fun isForceUpdate() : Boolean {
            return forceUpdate
        }
    }

    class ResponseValue(var items: List<Item>) : UseCase.ResponseValue

}