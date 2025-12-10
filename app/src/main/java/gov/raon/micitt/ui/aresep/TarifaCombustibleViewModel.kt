package gov.raon.micitt.ui.aresep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TarifaCombustibleViewModel(
    private val repository: TarifaCombustibleRepository = TarifaCombustibleRepository()
) : ViewModel() {
    private val _tarifas = MutableLiveData<TarifaCombustibleState>()
    val tarifas: LiveData<TarifaCombustibleState> = _tarifas

    fun loadTarifas() {
        viewModelScope.launch {
            _tarifas.value = TarifaCombustibleState.Loading
            try {
                val response = repository.getTarifas()
                _tarifas.value = TarifaCombustibleState.Success(response)
            } catch (e: Exception) {
                _tarifas.value = TarifaCombustibleState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}