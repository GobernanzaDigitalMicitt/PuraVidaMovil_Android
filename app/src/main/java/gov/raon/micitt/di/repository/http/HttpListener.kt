package gov.raon.micitt.di.repository.http


class HttpListener<T> constructor(val success: (result: T?) -> Unit, val fail: (result: Any?) -> Unit) {
    fun complete(result: T?) = success(result)
    fun error(result: Any?) = fail(result)
}
