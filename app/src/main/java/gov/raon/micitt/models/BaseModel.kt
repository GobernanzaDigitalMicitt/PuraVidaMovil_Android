package gov.raon.micitt.models

import com.google.gson.Gson
import com.google.gson.JsonElement

abstract class BaseModel {
    fun toJson(): JsonElement? {
        val gson = Gson()
        return gson.toJsonTree(this)
    }

    abstract fun fromJson(value: String)
}