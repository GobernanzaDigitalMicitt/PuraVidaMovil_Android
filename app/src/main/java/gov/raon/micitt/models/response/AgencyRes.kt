package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class AgencyRes(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    @SerializedName("resultData")
    val resultData: ResultData
)

data class ResultData(
    @SerializedName("agencyInfoList")
    val agencyInfoList: MutableList<AgencyInfo>
)

data class AgencyInfo(
    @SerializedName("agencyCode")
    val agencyCode: String,
    @SerializedName("agencyName")
    val agencyName: String,
    @SerializedName("dataFormatList")
    val dataFormatList: MutableList<String>?,
    @SerializedName("dataTypeList")
    val dataTypeList: MutableList<String>?,
    @SerializedName("description")
    val description: String

)