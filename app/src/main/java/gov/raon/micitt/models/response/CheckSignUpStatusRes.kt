package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class CheckSignUpStatusRes (
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    @SerializedName("resultData")
    val resultData: CheckSingUpStatusData
)

data class CheckSingUpStatusData (
    @SerializedName("hashedNId")
    val hashedNId: String,
    @SerializedName("name")
    val name: String
)
