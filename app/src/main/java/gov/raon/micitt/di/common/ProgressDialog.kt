package gov.raon.micitt.di.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import gov.raon.micitt.R
import gov.raon.micitt.databinding.DialogProgressBinding

class ProgressDialog(context: Context) : Dialog(context, R.style.Theme_Micitt){
    private lateinit var binding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.6f
        window?.let {
            it.attributes = layoutParams
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        binding = DialogProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottieProgress.playAnimation()
//        setCancelable(false)
    }
}