package gov.raon.micitt.ui.main

import android.app.Dialog
import android.content.Context
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import gov.raon.micitt.R
import gov.raon.micitt.databinding.DialogAuthenticationBinding

class AuthenticationDialog(context: Context, authCode: String?, private var time: Int) :
    Dialog(context, android.R.style.Theme_Translucent) {
    private var binding: DialogAuthenticationBinding =
        DialogAuthenticationBinding.inflate(layoutInflater)
    private var listener: ((result: Boolean) -> Unit)? = null
    private var refreshListener: (() -> Unit)? = null
    private lateinit var testListener: () -> Unit
    private var countDownTimer: CountDownTimer? = null
    private var redoTimer = true

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.tvResultCode.text = authCode

        binding.layerAuth.setOnClickListener {
            if (binding.iconRefresh.visibility == View.VISIBLE) {
                binding.iconRefresh.visibility = View.GONE
                binding.tvAuth.text = context.getString(R.string.gaudi_auth)
                refreshListener?.invoke()
                runTimer()
            } else {
                if (binding.tvAuth.text.equals(context.getString(R.string.gaudi_auth))) {
                    listener?.invoke(true)
                }
            }
        }

        binding.tvAuthTest.setOnClickListener {
            testListener()
        }

        runTimer()

    }

    private fun runTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(this.time.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 60000).toInt()
                val seconds = ((millisUntilFinished % 60000) / 1000).toInt()
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.tvAuth.text = context.getString(R.string.gaudi_auth_update)
                redoTimer = true
                binding.iconRefresh.visibility = View.VISIBLE
            }
        }.start()
    }

    fun setListener(listener: (result: Boolean) -> Unit) {
        this.listener = listener
        binding.tvTimer.visibility = View.VISIBLE
    }

    fun setRefreshListener(refreshListener: () -> Unit) {
        this.refreshListener = refreshListener
    }

    fun setRefreshTime() {
        runTimer()
    }
}