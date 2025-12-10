package gov.raon.micitt.models

import com.google.gson.Gson

class CheckAuthModel : BaseModel {
    var requestId: String = ""

    constructor(requestId: String) {
        this.requestId = requestId
    }

    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, CheckAuthModel::class.java)

        requestId = data.requestId
    }
}