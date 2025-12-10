package gov.raon.micitt.di.network

import androidx.annotation.DrawableRes
import com.google.gson.JsonElement
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


data class TipoCambioResponse(
    val descripcion: String,
    val fechaRige: String,
    val precio: String
) {
    fun getPrecioFormateado(): String = "¢${"%.2f".format(precio.toFloat())}"
}

data class CategoriaResponse(
    val ID: String,
    val nombre: String,
    val abreviatura: String,
    val descripcion: String,
    @DrawableRes val icono: Int,
    val activityName: String,
    val activo: String
)

data class ServicioResponse(
    val CATEGORIA_ID: String,
    val DESCRIPCION: String,
    val FECHA_CREACION: String,
    val FORMATO_DATO: String,
    val ID: String,
    val INSTITUCION_ID: String,
    val NOMBRE: String,
)

data class VacunaResponse(
    val nId: String,
    val url: String,
    val file_name: String,
    val mime_type: String,
    val archivo: String,
    val hashId: String
)

interface eWalletService {
    @GET("bccr/tipocambio")
    suspend fun getExchangeRates(): List<TipoCambioResponse>

    @GET("aresep/combustible")
    suspend fun getFuelRates(): List<TipoCambioResponse>

    @GET("/db/categoria_servicio")
    suspend fun getCategories(): List<CategoriaResponse>

    @GET("db/servicio/categoria/{id}")
    suspend fun getServicesByCategory(@Path("id") categoryId: Int): List<ServicioResponse>

    @GET("/pdf/get")
    suspend fun getVacuna(): VacunaResponse

    @POST("/pdf")
    suspend fun getPDF(@Body body: JsonElement?): List<VacunaResponse>

    @POST("/servicios/consulta")
    suspend fun getService(@Body body: JsonElement?): JsonElement
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8445/"

    val instance: eWalletService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(eWalletService::class.java)
    }
}