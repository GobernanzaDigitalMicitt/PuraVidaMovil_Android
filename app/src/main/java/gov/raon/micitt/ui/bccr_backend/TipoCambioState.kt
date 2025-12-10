package gov.raon.micitt.ui.bccr_backend

import gov.raon.micitt.di.network.TipoCambioResponse

sealed class TipoCambioState {
    object Loading : TipoCambioState()
    data class Success(val data: List<TipoCambioResponse>) : TipoCambioState()
    data class Error(val message: String) : TipoCambioState()
}