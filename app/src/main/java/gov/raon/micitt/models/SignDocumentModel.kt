package gov.raon.micitt.models

import com.google.gson.Gson

class SignDocumentModel : BaseModel {
    var hashedToken: String
    var eDoc: String
    var dataType: String

    constructor(
        hashedToken: String,
        eDoc: String,
        dataType: String
    ) {
        this.hashedToken = hashedToken
        this.eDoc = eDoc
        this.dataType = dataType
    }

    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, SignDocumentModel::class.java)

        hashedToken = data.hashedToken
        eDoc = data.eDoc
        dataType = data.dataType
    }
}
