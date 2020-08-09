package net.kibotu.schlachtensee.models.yearly

import org.simpleframework.xml.ElementList

data class Labels(
    @field:ElementList(
        entry = "bez",
        type = String::class,
        inline = true
    ) var bez: List<String>? = null
)