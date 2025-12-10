package gov.raon.micitt.di.local

import android.content.Context
import gov.raon.micitt.utils.Log
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.kotlin.deleteFromRealm

class LocalRepoImpl(val realm: Realm?) : LocalRepository {
    override suspend fun create(creater: (realm: Realm) -> Unit) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { realmTransaction ->
                creater(realmTransaction)
            }
        }
    }

    override suspend fun <E : RealmModel> selectAll(
        clazz: Class<E>,
        where1: Where,
        callback: (RealmResults<E>?) -> Unit
    ) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { realmTransaction ->
                val valid = realmTransaction.where(clazz).isValid
                if(valid){
                    val data = realmTransaction.where(clazz)
                    data.equalTo(where1.key!!, where1.value).findAll()
                    callback(data.findAll())
                }
            }
        }
    }

    override suspend fun <E : RealmModel> update(
        clazz: Class<E>,
        where1: Where,
        where2: Where,
        listener: (Updater<*>) -> Unit
    ) {
        Realm.getDefaultInstance().executeTransaction { realmTransaction ->
            val selected =
                realmTransaction.where(clazz).equalTo(where1.key!!, where1.value).equalTo(where2.key!!, where2.value).findFirst()

            val updater = Updater<RealmObject>()
            updater.realm = realmTransaction
            updater.data = selected as RealmObject?

            listener(updater)
        }
    }

    override suspend fun <E : RealmModel> delete(
        clazz: Class<E>,
        where1: Where,
    ) {
        Realm.getDefaultInstance().executeTransaction { realmTransaction ->
            val selected = realmTransaction.where(clazz).equalTo(where1.key!!, where1.value).findAll()
            selected?.deleteAllFromRealm()
        }
    }

    class Where {
        var key: String? = null
        var value: String? = null
    }

    class Updater<E : RealmObject> {
        var realm: Realm? = null
        var data: E? = null
    }

    class Deleter<E : RealmObject>{
        var realm: Realm? = null
        var data: E? = null
    }

    /// Shared Pref
    private val DEFAULT_PREF_NAME = "micitt"

    override fun removeAllPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    override fun setInt(context: Context, key: String, value: Int) {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    override fun setBoolean(context: Context, key: String, value: Boolean?) {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value!!)
        editor.apply()
    }

    override fun setString(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun setLong(context: Context, key: String, value: Long) {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    override fun getInt(context: Context, key: String, defaultValue: Int): Int {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun getBoolean(context: Context, key: String, defaultValue: Boolean?): Boolean {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, defaultValue!!)
    }

    override fun getString(context: Context, key: String, defaultVal: String?): String? {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultVal)
    }

    override fun getLong(context: Context, key: String, defaultVal: Long): Long {
        val sharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(key, defaultVal)
    }

}