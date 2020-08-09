package net.kibotu.schlachtensee.koin

import com.exozet.android.core.services.network.interceptors.ContentTypeInterceptor
import com.exozet.android.core.services.network.interceptors.LoadingInterceptor
import com.exozet.android.core.services.network.interceptors.createHttpLoggingInterceptor
import com.exozet.android.core.services.network.interceptors.createOKLogInterceptor
import net.kibotu.resourceextension.resBoolean
import net.kibotu.resourceextension.resLong
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.services.network.RequestProvider
import net.kibotu.schlachtensee.services.network.SchlachtenseeApi
import net.kibotu.schlachtensee.services.storage.AppConfiguration
import net.kibotu.schlachtensee.viewmodels.AppViewModel
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit


val remoteDataSourceModule = module {
    single { RequestProvider() }
    single { createOkHttpClient(get()).build() }
    single { LoadingInterceptor { get<AppViewModel>().onLoading(it) } }
    single {
        createWebService<SchlachtenseeApi>(
            get(),
            get<AppConfiguration>().schlachtensee_base_url
        )
    }
}

private fun createOkHttpClient(
    loadingInterceptor: LoadingInterceptor
): OkHttpClient.Builder = OkHttpClient.Builder()
    .retryOnConnectionFailure(true)
    .readTimeout(R.integer.timeoutDurationInSeconds.resLong, TimeUnit.SECONDS)
    .writeTimeout(R.integer.timeoutDurationInSeconds.resLong, TimeUnit.SECONDS)
    .connectTimeout(R.integer.timeoutDurationInSeconds.resLong, TimeUnit.SECONDS)
    .addInterceptor(ContentTypeInterceptor())
    .addInterceptor(createHttpLoggingInterceptor { R.bool.enable_logging.resBoolean })
    .addInterceptor(createOKLogInterceptor())
    .addInterceptor(loadingInterceptor)

private inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T =
    Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(T::class.java)