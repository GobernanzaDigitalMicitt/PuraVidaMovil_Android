package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class ErrorRes (
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String
)

