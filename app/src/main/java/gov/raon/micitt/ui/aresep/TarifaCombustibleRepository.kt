package gov.raon.micitt.ui.aresep

import gov.raon.micitt.di.network.AresepClient
import gov.raon.micitt.di.network.TarifaCombustibleService

class TarifaCombustibleRepository {
    private val service = AresepClient.instance.create(TarifaCombustibleService::class.java)

    suspend fun getTarifas(): List<TarifaCombustible> {
        val response = service.obtenerTarifas()
        if (response.isSuccessful) {
            return response.body()?.tarifas ?: emptyList()
        } else {
            throw Exception("Error: ${response.code()}")
        }
    }
}