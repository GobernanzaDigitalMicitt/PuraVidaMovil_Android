package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName
import java.io.Serial

data class CheckSignInStatusRes (
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    @SerializedName("resultData")
    val resultData: CheckSignInStatusData
)

data class CheckSignInStatusData (
    @SerializedName("sessionToken")
    val sessionToken: String,
    @SerializedName("hashedToken")
    val hashedToken: String,
    @SerializedName("nId")
    val nid: String,
    @SerializedName("userName")
    val userName: String
)
