package gov.raon.micitt.models.response

import com.google.gson.annotations.SerializedName

data class NotificationRes(
    @SerializedName("resultCode")
    val resultCode : String,
    @SerializedName("resultMsg")
    val resultMsg : String,
    @SerializedName("resultData")
    val resultData : NotificationResData,
)

data class NotificationResData(
    @SerializedName("notificationList")
    val notificationList: MutableList<NotificationData>,
    @SerializedName("notificationCnt")
    val notificationCnt : Int
)

data class NotificationData(
    @SerializedName("id")
    val id : Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content : String,
    @SerializedName("createdDt")
    val createdDt : String,
    @SerializedName("updatedDt")
    val updatedDt : String
)