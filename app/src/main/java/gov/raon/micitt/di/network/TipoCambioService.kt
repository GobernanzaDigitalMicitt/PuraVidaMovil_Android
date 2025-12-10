package gov.raon.micitt.di.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface TipoCambioService {
    @GET("wsindicadoreseconomicos.asmx/ObtenerIndicadoresEconomicosXML")
    suspend fun obtenerIndicadores(
        @Query("Indicador") indicador: String,
        @Query("FechaInicio") fechaInicio: String,
        @Query("FechaFinal") fechaFinal: String,
        @Query("Nombre") nombre: String,
        @Query("SubNiveles") subNiveles: String,
        @Query("CorreoElectronico") correoElectronico: String,
        @Query("Token") token: String
    ): Response<ResponseBody>
}