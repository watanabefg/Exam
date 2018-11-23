package jp.pules.exam.itemdetail.domain.usecase

import jp.pules.exam.UseCase
import jp.pules.exam.data.source.ItemsDataSource
import jp.pules.exam.data.source.ItemsRepository
import jp.pules.exam.items.domain.model.Item


/**
 * Retrieves a [Item] from the [ItemsRepository].
 */
class GetItem(val itemsRepository: ItemsRepository) : UseCase<GetItem.RequestValues, GetItem.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            itemsRepository.getItem(requestValues.itemId, object : ItemsDataSource.GetItemCallback {
                override fun onItemLoaded(item: Item) {
                    val responseValue = ResponseValue(item)
                    useCaseCallback!!.onSuccess(responseValue)
                }

                override fun onDataNotAvailable() {
                    useCaseCallback!!.onError()
                }
            })
        }
    }

    class RequestValues(var itemId: String) : UseCase.RequestValues

    class ResponseValue(var item: Item) : UseCase.ResponseValue
}