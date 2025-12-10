package gov.raon.micitt.ui.bccr

sealed class TipoCambioState {
    object Loading : TipoCambioState()
    data class Success(val data: ListaTipoCambio?) : TipoCambioState()
    data class Error(val message: String) : TipoCambioState()
}