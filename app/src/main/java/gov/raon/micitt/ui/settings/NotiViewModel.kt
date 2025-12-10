package gov.raon.micitt.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import gov.raon.micitt.di.DataState
import gov.raon.micitt.di.local.LocalRepoImpl
import gov.raon.micitt.di.local.LocalRepository
import gov.raon.micitt.models.response.NotificationRes
import gov.raon.micitt.di.repository.HttpRepository
import gov.raon.micitt.di.repository.http.HttpListener
import gov.raon.micitt.models.NotificationModel
import gov.raon.micitt.models.SaveDocumentModel
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
class NotiViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val httpRepository: HttpRepository
) : ViewModel() {
    val notiLiveList = MutableLiveData<NotificationRes>()
    val logoutLiveList = MutableLiveData<SignOutRes>()
    val liveErrorData = MutableLiveData<ErrorRes>()

    fun <T> getNotice(context: Context, notification: NotificationModel) {
        CoroutineScope(Dispatchers.IO).launch {
            httpRepository.getNotice(notification).collect {
                when (it) {
                    DataState.Loading -> {

                    }

                    is DataState.Success -> {
                        httpRepository.filterResponse(
                            it.data as Response<*>,
                            HttpListener({ success ->
                                try {
                                    val data = Gson().fromJson(success.toString(), NotificationRes::class.java)
                                    if (data != null) {
                                        notiLiveList.postValue(data)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, { fail ->
                                try {
                                    val errorData = Gson().fromJson(fail.toString(), ErrorRes::class.java)
                                    liveErrorData.postValue(errorData)
                                } catch (e: Exception) {
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


}