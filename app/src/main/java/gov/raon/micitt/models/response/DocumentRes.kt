package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class DocumentRes(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    @SerializedName("resultData")
    val resultData: DocumentResultData
)

data class DocumentResultData(
    @SerializedName("eDoc")
    val eDoc: String
)