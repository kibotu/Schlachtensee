package net.kibotu.schlachtensee.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "temphist")
data class TemperatureHistory(
    @field:Element
    var titel: String? = null,
    @field:Element
    var labellist: Labels? = null,
    @field:Element
    var von: String? = null,
    @field:Element
    var maxtemp: String? = null,
    @field:Element
    var templist: Temperatures? = null,
    @field:Element
    var jahr2016: String? = null,
    @field:Element
    var bis: String? = null,
    @field:Element
    var tempavg: Temperatures? = null,
    @field:Element
    var mintemp: String? = null,
    @field:Element
    var sql: String? = null
)