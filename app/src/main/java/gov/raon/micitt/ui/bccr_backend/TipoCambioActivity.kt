package gov.raon.micitt.ui.bccr_backend

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.databinding.ActivityExchangeDetailBinding
import gov.raon.micitt.di.common.BaseActivity


import gov.raon.micitt.di.network.TipoCambioResponse


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
        viewModel.loadTipoCambio()
    }

    private fun setupUI() {
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener { finish() }

        adapter = TipoCambioAdapter { tipoCambio ->
            showTarifaDetail(tipoCambio)
        }
        binding.exchangeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TipoCambioActivity)
            adapter = this@TipoCambioActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.tipoCambio.observe(this) { state ->
            when (state) {
                is TipoCambioState.Loading -> showLoading()
                is TipoCambioState.Success -> {
                    hideLoading()
                    adapter.submitList(state.data)
                }
                is TipoCambioState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
            }
        }
    }

    private fun showTarifaDetail(tipoCambio: TipoCambioResponse) {
        // Navegar a detalle si es necesario
    }

    private fun showLoading() {
        binding.exchangeRecyclerView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.exchangeRecyclerView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        // Opción 1: Snackbar (recomendado)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Reintentar") { viewModel.loadTipoCambio() }
            .show()

        // Opción 2: Toast (más simple)
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
