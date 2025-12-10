package gov.raon.micitt.models

import com.google.gson.Gson

class AgencyModel : BaseModel {
    var agencyCode: String?

    constructor(agencyCode: String?) {
        this.agencyCode = agencyCode
    }

    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, AgencyModel::class.java)

        agencyCode = data.agencyCode
    }
}
