package gov.raon.micitt.ui.aresep

sealed class TarifaCombustibleState {
    object Loading : TarifaCombustibleState()
    data class Success(val data: List<TarifaCombustible>) : TarifaCombustibleState()
    data class Error(val message: String) : TarifaCombustibleState()
}