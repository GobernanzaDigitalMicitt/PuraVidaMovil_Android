package gov.raon.micitt.ui.bccr_backend

import gov.raon.micitt.di.network.RetrofitClient
import gov.raon.micitt.di.network.TipoCambioResponse


class TipoCambioRepository {
    private val service = RetrofitClient.instance

    suspend fun getTipoCambio(): List<TipoCambioResponse> {
        val response = service.getExchangeRates()
        return response
//        if (response.isSuccessful) {
//            return response.body()?.tarifas ?: emptyList()
//        } else {
//            throw Exception("Error: ${response.code()}")
//        }
    }
}
