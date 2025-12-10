package gov.raon.micitt.models

import com.google.gson.Gson

class CheckDocumentModel : BaseModel {
    var hashedToken: String
    var requestId: String

    constructor(
        hashedToken: String,
        requestId: String,
    ) {
        this.hashedToken = hashedToken
        this.requestId = requestId
    }

    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, CheckDocumentModel::class.java)

        hashedToken = data.hashedToken
        requestId = data.requestId
    }
}
