package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class SignDocumentStatusRes(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    @SerializedName("resultData")
    val resultData: SignDocumentStatusResultData
)

data class SignDocumentStatusResultData(
    @SerializedName("signedEDoc")
    val signedEDoc: String,
)