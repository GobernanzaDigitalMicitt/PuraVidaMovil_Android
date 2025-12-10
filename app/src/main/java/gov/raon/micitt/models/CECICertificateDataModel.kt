package gov.raon.micitt.models

import com.google.gson.Gson

class CECICertificateDataModel: BaseModel {
    var apellido: String? = null
    var nid: String? = null
    var data: String? = null
    var hashId: String? = null
    var dataType: String? = null

    constructor(
        apellido: String,
        nid: String,
        data: String,
        hashId: String,
        dataType: String
    ) {
        this.apellido = apellido
        this.nid = nid
        this.data = data
        this.hashId = hashId
        this.dataType = dataType
    }

    override fun fromJson(value: String) {
        val json = Gson().fromJson(value, CECICertificateDataModel::class.java)

        apellido = json.apellido
        nid = json.nid
        data = json.data
        hashId = json.hashId
        dataType = json.dataType
    }
}
