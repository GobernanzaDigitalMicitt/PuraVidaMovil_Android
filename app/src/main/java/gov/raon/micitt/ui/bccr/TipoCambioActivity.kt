package gov.raon.micitt.ui.bccr

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.databinding.ActivityExchangeDetailBinding
import gov.raon.micitt.di.common.BaseActivity

@AndroidEntryPoint
class TipoCambioActivity : BaseActivity() {
    private lateinit var binding: ActivityExchangeDetailBinding
    private val viewModel: TipoCambioViewModel by viewModels()
    private lateinit var adapter: TipoCambioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExchangeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
        viewModel.loadIndicadorEconomico()
    }

    private fun setupUI() {
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener { finish() }

        binding.exchangeRecyclerView.adapter = TipoCambioAdapter {}

        adapter = TipoCambioAdapter { }

        binding.exchangeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TipoCambioActivity)
            adapter = this@TipoCambioActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.indicadorEconomico.observe(this) { state -> // Cambiamos la variable observada
            when (state) {
                is TipoCambioState.Loading -> showLoading()
                is TipoCambioState.Success -> {
                    hideLoading()
                    state.data?.let { adapter.submitList(it) }
                }
                is TipoCambioState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
            }
        }
    }


    private fun showLoading() {
        binding.exchangeRecyclerView.visibility = View.GONE // Ocultamos la lista mientras carga
    }

    private fun hideLoading() {
        // Ocultar el indicador de carga
        binding.exchangeRecyclerView.visibility = View.VISIBLE // Mostramos la lista o los datos
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Reintentar") { viewModel.loadIndicadorEconomico() } // Cambiamos la llamada al método
            .show()
    }
}