package net.kibotu.schlachtensee.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exozet.android.core.extensions.resString
import com.exozet.android.core.models.LoadingInfo
import com.exozet.android.core.services.connectivity.NetworkChangeReceiver
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import net.kibotu.ContextHelper
import net.kibotu.logger.BuildConfig
import net.kibotu.logger.Logger.loge
import net.kibotu.logger.Logger.logv
import net.kibotu.logger.snack
import net.kibotu.schlachtensee.R
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AppViewModel : ViewModel() {

    var networkChangeReceiver: NetworkChangeReceiver

    init {
        networkChangeReceiver = NetworkChangeReceiver.register(ContextHelper.getApplication()!!) { isConnected ->

            if (!isConnected) {
                snack(R.string.no_internet_connection_available.resString)
            }

            onConnectionUpdate.postValue(isConnected)
        }
    }

    var onConnectionUpdate = MutableLiveData<Boolean>()


    // region loading

    var isLoading = MutableLiveData<Boolean>()

    private var loadingInfos = mutableListOf<LoadingInfo>()

    private val subscription: CompositeDisposable = CompositeDisposable()

    init {
        addCleanupObservable()
    }

    private fun addCleanupObservable() {
        Observable.interval(0L, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(10)))
            .subscribe {

                if (loadingInfos.isEmpty())
                    return@subscribe

                loadingInfos.removeAll {
                    val duration = System.currentTimeMillis() - it.time
                    if (duration > 15 * 1000) {
                        loge("canceling loading for $duration ms ${it.name}")
                        true
                    } else
                        false
                }

                if (loadingInfos.isNullOrEmpty())
                    isLoading.postValue(false)
            }.addTo(subscription)
    }

    @Synchronized
    fun onLoading(loadingInfo: LoadingInfo) {

        logv("[OnLoading] inProgress=${loadingInfos.count()} $loadingInfo")

        if (BuildConfig.DEBUG) {
            val match = loadingInfos.filter { it.name == loadingInfo.name && !loadingInfo.isLoading }
                .sortedByDescending { it.time }.firstOrNull()

            if (match != null) {
                logv("Loading duration: ${loadingInfo.time - match.time} ms for ${loadingInfo.name}")
            } else {
                logv("Loading start: ${loadingInfo.name}")
            }
        }

        if (loadingInfo.isLoading) {
            isLoading.postValue(true)
            loadingInfos.add(loadingInfo)
            return
        }

        loadingInfos.removeAll { it.name == loadingInfo.name && !loadingInfo.isLoading }

        if (loadingInfos.isNullOrEmpty())
            isLoading.postValue(false)
    }

    fun cancelLoading() {
        loadingInfos.clear()
        isLoading.postValue(false)
    }

    fun dumpLoadingInfos(): List<LoadingInfo> = loadingInfos.filter { it.isLoading }.map { it.copy() }.toList()

    // endregion

    // region cleanup

    override fun onCleared() {
        super.onCleared()
        networkChangeReceiver.unregister(ContextHelper.getApplication()!!)
        subscription.dispose()
    }

    // endregion
}