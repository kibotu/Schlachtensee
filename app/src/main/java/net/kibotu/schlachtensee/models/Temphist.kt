package net.kibotu.schlachtensee.models

data class Temphist(
    var titel: String? = null,
    var labellist: Labellist? = null,
    var von: String? = null,
    var maxtemp: String? = null,
    var templist: Templist? = null,
    var jahr2016: String? = null,
    var bis: String? = null,
    var tempavg: Tempavg? = null,
    var mintemp: String? = null,
    var sql: String? = null
)