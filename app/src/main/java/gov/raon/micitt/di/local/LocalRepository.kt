package gov.raon.micitt.di.local

import android.content.Context
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

interface LocalRepository {

    // Realm Database
    suspend fun create(creater: (realm: Realm) -> Unit)
    suspend fun <E : RealmModel> selectAll(clazz: Class<E>, where1: LocalRepoImpl.Where, callback: (RealmResults<E>?) -> Unit)
    suspend fun <E : RealmModel> update(clazz: Class<E>, where1: LocalRepoImpl.Where, where2: LocalRepoImpl.Where, listener: (LocalRepoImpl.Updater<*>) -> Unit)
    suspend fun <E : RealmModel> delete(clazz: Class<E>, where1: LocalRepoImpl.Where)

    // Shared Pref
    fun removeAllPreferences(context: Context)
    fun setInt(context: Context, key: String, value: Int)
    fun setBoolean(context: Context, key: String, value: Boolean?)
    fun setString(context: Context, key: String, value: String)
    fun setLong(context: Context, key: String, value: Long)
    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int
    fun getBoolean(context: Context, key: String, defaultValue: Boolean?): Boolean
    fun getString(context: Context, key: String, defaultVal: String? = null): String?
    fun getLong(context: Context, key: String, defaultVal: Long): Long

}