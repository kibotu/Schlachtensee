package net.kibotu.schlachtensee.models.yearly

import org.simpleframework.xml.ElementList

data class Temperatures(
    @field:ElementList(
        entry = "wert",
        type = Temperature::class,
        inline = true
    ) var value: List<Temperature>? = null
)