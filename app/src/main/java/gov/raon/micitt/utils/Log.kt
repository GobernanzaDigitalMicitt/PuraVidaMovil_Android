package gov.raon.micitt.utils

object Log {
    private val TAG: String? = null
    private val isDisable = true

    fun e(message: String, s: String) {
        if (isDisable) android.util.Log.e(getTag(), getMsg(message))
    }

    fun w(message: String) {
        if (isDisable) android.util.Log.w(getTag(), getMsg(message))
    }

    fun i(message: String) {
        if (isDisable) android.util.Log.i("mitccInfo", "[${getTag()}] ${getMsg(message)}")
    }

    fun d(tag:String, message: String) {
        if (isDisable) android.util.Log.d(tag, getMsg(message))
    }

    fun d(message: String) {
        if (isDisable) android.util.Log.d(getTag(), getMsg(message))
    }

    fun v(message: String) {
        if (isDisable) android.util.Log.v(getTag(), getMsg(message))
    }

    private fun getTag(): String? {
        val ste = Thread.currentThread().stackTrace[4] ?: return null

        val sb = StringBuilder()
        sb.append(ste.fileName.replace(".kt", ""))
        return sb.toString()
    }

    private fun getMsg(message: String): String {
        val ste = Thread.currentThread().stackTrace[4] ?: return ""

        val sb = StringBuilder()
        sb.append(ste.methodName)
        sb.append("() [Micitt] - ")
        sb.append(message)
        return sb.toString()
    }
}
