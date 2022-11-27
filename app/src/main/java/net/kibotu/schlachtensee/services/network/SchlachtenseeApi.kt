package net.kibotu.schlachtensee.services.network

import net.kibotu.schlachtensee.models.yearly.TemperatureHistory
import retrofit2.http.GET

/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 *
 * https://documenter.getpostman.com/view/213696/S1TbSZyy?version=latest
 */

interface SchlachtenseeApi {

    @GET("/jmn_ajax.php?anfrage=7&art=temperaturtag&offset=0")
    suspend fun dailyTemperature(): TemperatureHistory

    @GET("/jmn_ajax.php?anfrage=7&art=temperaturwoche&offset=0")
    suspend fun weeklyTemperature(): TemperatureHistory

    @GET("/jmn_ajax.php?anfrage=7&art=temperaturjahr&offset=0")
    suspend fun yearlyTemperature(): TemperatureHistory

    @GET("/jmn_ajax.php?anfrage=6")
    suspend fun lastTemperature(): String
}