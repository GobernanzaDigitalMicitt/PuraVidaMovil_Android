package gov.raon.micitt.ui.bccr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TipoCambioViewModel(
    private val repository: TipoCambioRepository = TipoCambioRepository()
) : ViewModel() {
    private val _indicadorEconomico = MutableLiveData<TipoCambioState>()
    val indicadorEconomico: LiveData<TipoCambioState> = _indicadorEconomico

    fun loadIndicadorEconomico() {
        viewModelScope.launch {
            _indicadorEconomico.value = TipoCambioState.Loading
            try {
                val response = repository.getIndicadoresEconomicosXML()
                if (response != null) {
                    _indicadorEconomico.value = TipoCambioState.Success(response)
                } else {
                    _indicadorEconomico.value = TipoCambioState.Error("No se encontraron datos.")
                }
            } catch (e: Exception) {
                _indicadorEconomico.value = TipoCambioState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}