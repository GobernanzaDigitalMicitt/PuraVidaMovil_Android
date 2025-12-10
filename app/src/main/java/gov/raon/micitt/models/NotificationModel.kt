package gov.raon.micitt.models

import com.google.gson.Gson

class NotificationModel  : BaseModel {
    var pageNo : Int = 0
    var pageCnt : Int = 0

    constructor(pagNo : Int, pageCnt: Int){
        this.pageNo = pagNo
        this.pageCnt = pageCnt
    }

    override fun fromJson(value: String) {
        val data = Gson().fromJson(value, NotificationModel::class.java)

        pageNo = data.pageNo
        pageCnt = data.pageCnt
    }
}