package gov.raon.micitt.ui.certificate.model

import com.google.gson.annotations.SerializedName

data class toXmlData (
    @SerializedName("strXml")
    var strXml: String
)

data class infoData (
    @SerializedName("strCondicion")
    var strCondicion: String?,
    @SerializedName("strEsMoroso")
    var strEsMoroso: String?,
    @SerializedName("strEsOmiso")
    var strEsOmiso: String?,
    @SerializedName("strFechaActualizacion")
    var strFechaActualizacion : String?,
    @SerializedName("strFechaDesinscripcion")
    var strFechaDesinscripcion: String?,
    @SerializedName("strFechaInscripcion")
    var strFechaInscripcion: String?,
    @SerializedName("strIdentificacion")
    var strIdentificacion: String?,
    @SerializedName("strSistema")
    var strSistema : String?,
    @SerializedName("nroInternoID")
    var nroInternoID : String?,
    @SerializedName("strAdministracion")
    var strAdministracion: String?,
    @SerializedName("strEstadoTributario")
    var strEstadoTributario: String?,
    @SerializedName("strNombreComercial")
    var strNombreComercial : String?,
    @SerializedName("strRazonSocial")
    var strRazonSocial : String?
)