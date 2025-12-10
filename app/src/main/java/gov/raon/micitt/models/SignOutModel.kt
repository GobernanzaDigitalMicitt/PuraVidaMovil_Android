package gov.raon.micitt.models

import com.google.gson.Gson

class SignOutModel : BaseModel {
    var hashedToken: String = ""

    constructor(hashedToken: String) {
        this.hashedToken = hashedToken
    }

    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, SignOutModel::class.java)

        hashedToken = data.hashedToken
    }
}