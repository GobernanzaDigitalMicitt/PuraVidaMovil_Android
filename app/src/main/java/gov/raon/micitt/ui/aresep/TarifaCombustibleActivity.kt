package gov.raon.micitt.ui.aresep

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

import gov.raon.micitt.databinding.ActivityFuelDetailBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.di.network.RetrofitClient

import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TarifaCombustibleActivity : BaseActivity() {
    private lateinit var binding: ActivityFuelDetailBinding
    private val viewModel: TarifaCombustibleViewModel by viewModels()
    private lateinit var adapter: TarifaCombustibleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFuelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
        viewModel.loadTarifas()
    }

    private fun setupUI() {
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener { finish() }

        adapter = TarifaCombustibleAdapter {}
        binding.fuelRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TarifaCombustibleActivity)
            adapter = this@TarifaCombustibleActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.tarifas.observe(this) { state ->
            when (state) {
                is TarifaCombustibleState.Loading -> showLoading()
                is TarifaCombustibleState.Success -> {
                    hideLoading()
                    adapter.submitList(state.data)
                }
                is TarifaCombustibleState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        binding.fuelRecyclerView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.fuelRecyclerView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        // Opción 1: Snackbar (recomendado)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Reintentar") { viewModel.loadTarifas() }
            .show()
    }
}
