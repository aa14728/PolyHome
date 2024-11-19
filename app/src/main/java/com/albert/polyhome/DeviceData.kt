package com.albert.polyhome

import java.util.Locale.Category

data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int? = null,
    val openingMode: Int? = null,
    val power: Int? = null
)

data class DeviceList(
    val devices: ArrayList<DeviceData>?
)
enum class DeviceCategories(val category: String){
    SHUTTER("Shutter"),
    GARAGE("Garage"),
    LIGHT("Light")
}
