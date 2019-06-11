package net.kibotu.schlachtensee.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Text

data class Temperature(
    @field:Attribute(name = "dat")
    var date: String? = null,
    @field:Text
    var wert: String? = null
)