package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class SignRes (
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    @SerializedName("resultData")
    val resultData: SignUpData,
)

data class SignUpData (
    @SerializedName("requestId")
    val requestId: String,
    @SerializedName("verificationCode")
    val verificationCode: String,
    @SerializedName("documentSummary")
    val documentSummary: String,
    @SerializedName("maximumSignatureTimeInSeconds")
    val maximumSignatureTimeInSeconds: Int,
    @SerializedName("createdDt")
    val createdDt: String
)
