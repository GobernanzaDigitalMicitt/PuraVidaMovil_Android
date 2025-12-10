package gov.raon.micitt.models

import com.google.gson.Gson

class SignModel : BaseModel {
    var hashedNId: String?
    var nId: String?

    constructor(hashedNId: String, nId: String?) {
        this.hashedNId = hashedNId
        this.nId = nId
    }

    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, SignModel::class.java)

        hashedNId = data.hashedNId
        nId = data.nId
    }
}