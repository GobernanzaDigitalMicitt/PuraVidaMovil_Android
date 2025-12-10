package gov.raon.micitt.ui.bccr_backend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TipoCambioViewModel(
    private val repository: TipoCambioRepository = TipoCambioRepository()
) : ViewModel() {
    private val _tipoCambio = MutableLiveData<TipoCambioState>()
    val tipoCambio: LiveData<TipoCambioState> = _tipoCambio

    fun loadTipoCambio() {
        viewModelScope.launch {
            _tipoCambio.value = TipoCambioState.Loading
            try {
                val response = repository.getTipoCambio()
                _tipoCambio.value = TipoCambioState.Success(response)
            } catch (e: Exception) {
                _tipoCambio.value = TipoCambioState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}