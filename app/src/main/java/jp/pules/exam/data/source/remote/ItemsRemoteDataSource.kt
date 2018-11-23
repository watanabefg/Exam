package jp.pules.exam.data.source.remote

import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.data.source.ItemsDataSource
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback


class ItemsRemoteDataSource private constructor() : ItemsDataSource {
    val qiitaBase = "https://qiita.com/api/v2/"

    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {
        // 非同期処理
        createNetworkClient(qiitaBase).create(ItemService::class.java)
                .getItems(page = 1, perPage = 20)
                .enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback.onItemsLoaded(it)
                        }
                    } else {
                        callback.onDataNotAvailable()
                    }
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun createNetworkClient(baseUrl: String) = retrofitClient(baseUrl, httpClient())

    private fun retrofitClient(baseUrl: String, httpClient: OkHttpClient) : Retrofit =
    Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    private fun httpClient() : OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
        val clientBuilder = OkHttpClient.Builder()
        // TODO: 下記2行はDebug用
        //httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        //clientBuilder.addInterceptor(httpLoggingInterceptor)

        return clientBuilder.build()
    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {
        // 非同期処理
        createNetworkClient(qiitaBase)
                .create(ItemService::class.java)
                .getItem(itemId)
                .enqueue(object: Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>?) {
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback.onItemLoaded(it)
                        }
                    } else {
                        callback.onDataNotAvailable()
                    }
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                callback.onDataNotAvailable()
            }

        })
    }

    override fun saveItem(item: Item) {
        // TODO: リモートに保存するときに書く
    }


    override fun refreshItems() {
        // {@link ItemsRepository}
    }

    override fun deleteAllItems() {
        // リモートに対しては操作しない
    }

    companion object {
        private var INSTANCE: ItemsRemoteDataSource? = null

        @JvmStatic
        fun getInstance() : ItemsRemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = ItemsRemoteDataSource()
            }
            return INSTANCE!!
        }

    }

}