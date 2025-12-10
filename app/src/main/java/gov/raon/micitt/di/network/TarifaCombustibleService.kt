package gov.raon.micitt.di.network

import gov.raon.micitt.ui.aresep.ResponseCombustibles
import retrofit2.Response
import retrofit2.http.GET

interface TarifaCombustibleService {
    @GET("ws.datosabiertos/Services/IE/TarifaCombustible.svc/ObtenerActuales")
    suspend fun obtenerTarifas(): Response<ResponseCombustibles>
}