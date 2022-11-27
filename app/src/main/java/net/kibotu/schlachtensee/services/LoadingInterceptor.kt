package net.kibotu.schlachtensee.services

import okhttp3.Interceptor
import okhttp3.Response

class LoadingInterceptor(private val onLoading: (LoadingInfo) -> Unit) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val loadingInfo = LoadingInfo(name = request.url.encodedPath)
        onLoading(loadingInfo)

        try {
            return chain.proceed(request)
        } catch (e: Exception) {
            throw e
        } finally {
            onLoading(LoadingInfo(loadingInfo.name, false))
        }
    }
}