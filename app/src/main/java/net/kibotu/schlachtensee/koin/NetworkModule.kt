package net.kibotu.schlachtensee.koin

import net.kibotu.resourceextension.resLong
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.services.LoadingInterceptor
import net.kibotu.schlachtensee.services.network.RequestProvider
import net.kibotu.schlachtensee.services.network.SchlachtenseeApi
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit


val remoteDataSourceModule = module {
    single { RequestProvider() }
    single { createOkHttpClient(get()).build() }
    single {
        createWebService<SchlachtenseeApi>(
            get(),
            "http://jmnberlin.de/"
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
    .addInterceptor(loadingInterceptor)

private inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T =
    Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(T::class.java)