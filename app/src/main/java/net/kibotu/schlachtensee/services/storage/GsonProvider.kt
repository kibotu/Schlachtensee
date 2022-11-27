package net.kibotu.schlachtensee.services.storage

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */


val gson: Gson by lazy {
    GsonBuilder().create()
}

inline fun <reified T> T.toJson(): String = gson.toJson(this)

inline fun <reified T> String.fromJson(): T = gson.fromJson(this)

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)