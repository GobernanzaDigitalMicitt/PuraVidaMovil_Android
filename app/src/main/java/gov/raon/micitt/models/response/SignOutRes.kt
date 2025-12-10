package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class SignOutRes(
    @SerializedName("resultCode")
    val resultCode : String,
    @SerializedName("resultMsg")
    val resultMsg : String,
    @SerializedName("detailMsg")
    val detailMsg : String,
)