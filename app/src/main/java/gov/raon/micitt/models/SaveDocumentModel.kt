package gov.raon.micitt.models

import com.google.gson.Gson
import gov.raon.micitt.models.realm.RealmDocumentModel

open class SaveDocumentModel : BaseModel {
    var hashedNid: String? = null
    var agencyCode: String? = null
    var dataFormat: String? = null
    var dataType: String? = null
    var agencyName: String? = null
    var eDoc: String? = null
    var strIdentificacion: String? = null
    var date : String? = null

    constructor(realmDocumentModel: RealmDocumentModel) {
        this.hashedNid = realmDocumentModel.hashedNid
        this.agencyCode = realmDocumentModel.agencyCode
        this.agencyName = realmDocumentModel.agencyName
        this.dataFormat = realmDocumentModel.dataFormat
        this.dataType = realmDocumentModel.dataType
        this.eDoc = realmDocumentModel.eDoc
        this.strIdentificacion = realmDocumentModel.strIdentificacion
        this.date = realmDocumentModel.date

    }


    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, SaveDocumentModel::class.java)

        hashedNid = data.hashedNid
        agencyCode = data.agencyCode
        dataFormat = data.dataFormat
        dataType = data.dataType
        agencyName = data.agencyName
        eDoc = data.eDoc
        strIdentificacion = data.strIdentificacion
        date = data.date
    }
}

