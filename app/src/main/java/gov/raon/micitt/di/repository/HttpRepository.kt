package gov.raon.micitt.di.repository

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gov.raon.micitt.di.DataState
import gov.raon.micitt.di.network.ApiService
import gov.raon.micitt.di.repository.http.HttpListener
import gov.raon.micitt.models.AgencyModel
import gov.raon.micitt.models.CertificateModel
import gov.raon.micitt.models.CheckAuthModel
import gov.raon.micitt.models.CheckDocumentModel
import gov.raon.micitt.models.DocumentModel
import gov.raon.micitt.models.SignDocumentModel
import gov.raon.micitt.models.SignModel
import gov.raon.micitt.models.NotificationModel
import gov.raon.micitt.models.SignOutModel
import gov.raon.micitt.utils.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class HttpRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getElement(jsonStr: String): JsonElement {
        return JsonParser().parse(jsonStr)
    }

    fun getElement(hashMap: HashMap<String, Any>): JsonElement {
        val map = hashMap as Map<*, *>
        val body = JSONObject(map).toString()
        return JsonParser().parse(body)
    }

    suspend fun signUp(signModel: SignModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val result = apiService.signUp(signModel.toJson())

            emit(DataState.Success(result))

        } catch (e: Exception) {
            Log.d("Error : " + e.message)
            emit(DataState.Error(e))
        }
    }

    suspend fun signIn(signModel: SignModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val result = apiService.signIn(signModel.toJson())
            emit(DataState.Success(result))

        } catch (e: Exception) {
            emit(DataState.Error(e))
            Log.d("signIn Error : " + e.message)
        }
    }

    suspend fun checkAuthSignUp(checkAuthModel: CheckAuthModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            Log.d("checkAuthentication rep")
            val result = apiService.checkSignUpStatus(checkAuthModel.toJson())
            emit(DataState.Success(result))
        } catch (e: Exception) {
            Log.d( "error1 : " + e.message)
            emit(DataState.Error(e))
        }
    }

    suspend fun checkAuthSignIn(checkAuthModel: CheckAuthModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            Log.d("checkAuthentication rep")
            val result = apiService.checkSignInStatus(checkAuthModel.toJson())
            emit(DataState.Success(result))
        } catch (e: Exception) {
            Log.d( "error1 : " + e.message)
            emit(DataState.Error(e))
        }
    }

    suspend fun getAgency(agencyModel: AgencyModel?): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val result = apiService.getAgency(agencyModel?.toJson())

            emit(DataState.Success(result))
        } catch (e: Exception) {
            Log.d( "Error : " + e.message)
            emit(DataState.Error(e))
        }
    }

    suspend fun getDocument(documentModel: DocumentModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val result = apiService.getDocument(documentModel.toJson())

            emit(DataState.Success(result))
        } catch (e: Exception) {
            Log.d("Error : " + e.message)
            emit(DataState.Error(e))
        }
    }

    suspend fun signDocument(signDocumentModel: SignDocumentModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val result = apiService.signDocument(signDocumentModel.toJson())

            emit(DataState.Success(result))
        } catch (e: Exception) {
            Log.d("Error : " + e.message)
            emit(DataState.Error(e))
        }
    }

    suspend fun checkSignDocumentStatus(checkDocumentModel: CheckDocumentModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            Log.d( "checkDocumentModel : " + checkDocumentModel.toJson())
            val result = apiService.checkSignDocumentStatus(checkDocumentModel.toJson())

            emit(DataState.Success(result))
        } catch (e: Exception) {
            Log.d("Error : " + e.message)
            emit(DataState.Error(e))
        }
    }

    suspend fun certificateDocument(certificateModel: CertificateModel): Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val result = apiService.certificateDocument(certificateModel.toJson())

            emit(DataState.Success(result))
        } catch (e: Exception) {
            Log.d("Error : " + e.message)
            emit(DataState.Error(e))
        }
    }


    fun <T> filterResponse(response: Response<T>, listener: HttpListener<JSONObject>) {
        try {
            val body = response.body()?.toString()?.let { JSONObject(it) }

            when {
                response.isSuccessful && body != null -> {
                    if (body.getString("resultCode") == "000") {
                        listener.success(body)
                    } else {
                        listener.fail(body)
                    }
                }
                else -> listener.fail("Not Data")
            }
        } catch (e: Exception) {
            listener.fail("filterResponse Error")
        }

    }

    suspend fun getNotice(notification: NotificationModel): Flow<DataState<Response<JsonObject>>> = flow {
            emit(DataState.Loading)
            try {
                val result = apiService.getNotice(notification.toJson())

                emit(DataState.Success(result))

            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }


    suspend fun withdraw(hashedToken : String) : Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val toModel = SignOutModel(hashedToken)
            val result = apiService.withDraw(toModel.toJson())
            emit(DataState.Success(result))

        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    suspend fun logOut(hashedToken : String) : Flow<DataState<Response<JsonObject>>> = flow {
        emit(DataState.Loading)
        try {
            val toModel = SignOutModel(hashedToken)
            val result = apiService.logOut(toModel.toJson())
            emit(DataState.Success(result))

        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }
}