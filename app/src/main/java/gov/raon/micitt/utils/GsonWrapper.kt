package gov.raon.micitt.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

class GsonWrapper private constructor(private val gson: Gson) {

    companion object {
        fun getGson(): GsonWrapper {
            return GsonWrapper(GsonBuilder().disableHtmlEscaping().create())
        }

        fun getGsonPrettyPrinting(): GsonWrapper {
            val builder = GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
            return GsonWrapper(builder.create())
        }
    }

    constructor() : this(GsonBuilder().disableHtmlEscaping().create())

    constructor(prettyPrinting: Boolean) : this(
        GsonBuilder().disableHtmlEscaping().apply {
            if (prettyPrinting) setPrettyPrinting()
        }.create()
    )

    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }

    fun toJson(src: Any): String {
        return gson.toJson(src)
    }
}
