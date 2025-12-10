package gov.raon.micitt.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import gov.raon.micitt.di.DataState
import gov.raon.micitt.di.local.LocalRepoImpl
import gov.raon.micitt.di.local.LocalRepository
import gov.raon.micitt.di.repository.HttpRepository
import gov.raon.micitt.di.repository.http.HttpListener
import gov.raon.micitt.models.realm.RealmDocumentModel
import gov.raon.micitt.models.response.ErrorRes
import gov.raon.micitt.models.response.SignOutRes
import gov.raon.micitt.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val httpRepository: HttpRepository
) : ViewModel() {

    val logoutLiveList = MutableLiveData<SignOutRes>()
    val liveErrorData = MutableLiveData<ErrorRes>()

    fun <T> withdraw(context: Context, hashedToken : String){
        CoroutineScope(Dispatchers.IO).launch {
            httpRepository.withdraw(hashedToken).collect{
                when(it){
                    DataState.Loading ->{
                    }
                    is DataState.Success ->{
                        httpRepository.filterResponse(
                            it.data as Response<*>,
                            HttpListener({ success -> try{
                                val data = Gson().fromJson(success.toString(), SignOutRes::class.java)
                                if(data != null){
                                    logoutLiveList.postValue(data)
                                }
                            } catch (e: Exception){
                                e.printStackTrace()
                            }

                            },{fail ->
                                try{
                                    val errorData = Gson().fromJson(fail.toString(), ErrorRes::class.java)
                                    liveErrorData.postValue(errorData)
                                } catch (e: Exception){
                                    e.printStackTrace()
                                }
                            })
                        )
                    }
                    is DataState.Error -> {
                        Log.d("Request Network Error")
                    }
                }
            }
        }
    }

    fun <T> logOut(context: Context, hashedToken : String){
        CoroutineScope(Dispatchers.IO).launch {
            httpRepository.logOut(hashedToken).collect{
                when(it){
                    DataState.Loading ->{

                    }
                    is DataState.Success ->{
                        httpRepository.filterResponse(
                            it.data as Response<*>,
                            HttpListener({ success -> try{
                                val data = Gson().fromJson(success.toString(), SignOutRes::class.java)
                                if(data != null){
                                    logoutLiveList.postValue(data)
                                }
                            } catch (e: Exception){
                                e.printStackTrace()
                            }

                            },{fail ->
                                try{
                                    val errorData = Gson().fromJson(fail.toString(), ErrorRes::class.java)
                                    liveErrorData.postValue(errorData)
                                } catch (e: Exception){
                                    e.printStackTrace()
                                }
                            })
                        )
                    }
                    is DataState.Error -> {
                        Log.d("Request Network Error")
                    }
                }
            }
        }
    }
    fun deleteRealm(hashedNid: String){
        CoroutineScope(Dispatchers.IO).launch {
            val where = LocalRepoImpl.Where()
            where.key = "hashedNid"
            where.value = hashedNid

            localRepository.delete(RealmDocumentModel::class.java, where)
        }

    }
}