package gov.raon.micitt.di.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /**
     * User API
     **/
    @POST("/api/v1/user/signup")
    suspend fun signUp(@Body body: JsonElement?): Response<JsonObject>

    @POST("/api/v1/notification/get")
    suspend fun getNotice(@Body body: JsonElement?):Response<JsonObject>
    @POST("/api/v1/user/signup/status")
    suspend fun checkSignUpStatus(@Body body: JsonElement?): Response<JsonObject>

    @POST("/api/v1/user/login")
    suspend fun signIn(@Body body: JsonElement?): Response<JsonObject>

    @POST("/api/v1/user/login/status")
    suspend fun checkSignInStatus(@Body body: JsonElement?): Response<JsonObject>

    @POST("/api/v1/user/logout")
    suspend fun logOut(@Body body: JsonElement?): Response<JsonObject>
    @POST("/api/v1/user/withdraw")
    suspend fun withDraw(@Body body: JsonElement?): Response<JsonObject>


    /**
     * Server API
     **/
    @POST("/api/v1/agency/get")
    suspend fun getAgency(@Body body: JsonElement?): Response<JsonObject>


    /**
     * Gaudi API
     **/
    @POST("/api/v1/document/get")
    suspend fun getDocument(@Body body: JsonElement?): Response<JsonObject>

    @POST("/api/v1/document/sign")
    suspend fun signDocument(@Body body: JsonElement?): Response<JsonObject>

    @POST("/api/v1/document/sign/status")
    suspend fun checkSignDocumentStatus(@Body body: JsonElement?): Response<JsonObject>

    @POST("/api/v1/document/certificate")
    suspend fun certificateDocument(@Body body: JsonElement?): Response<JsonObject>

}