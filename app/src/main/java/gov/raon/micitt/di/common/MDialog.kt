package gov.raon.micitt.di.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.TextView
import gov.raon.micitt.R
import gov.raon.micitt.databinding.DialogCommonBinding

class MDialog(context: Context, type: Int, title: String?, message: String?, btnCancelStr: String?, btnConfirmStr: String?) :
    Dialog(context, android.R.style.Theme_Translucent) {

    private var binding: DialogCommonBinding = DialogCommonBinding.inflate(layoutInflater)
    private lateinit var listener : (result: Boolean, obj: Any?)-> Unit

    init{
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.dialogTitle.text = title
        binding.dialogMessage.text = message

        binding.btnLayout.btnConfirm.text = btnConfirmStr
        binding.btnLayout.btnConfirm.setOnClickListener {
            listener(true, null)
            dismiss()
        }

        // 0 > layoutbtn2
        // 1 > layoutbtn
        if(type == 0){
            binding.btnLayout2.btnCl.visibility = View.VISIBLE
            binding.btnLayout.btnCl.visibility = View.GONE
        } else {
            binding.btnLayout2.btnCl.visibility = View.GONE
            binding.btnLayout.btnCl.visibility = View.VISIBLE
        }

        binding.btnLayout.btnCancel.text = btnCancelStr
        binding.btnLayout.btnCancel.setOnClickListener {
            listener(false, null)
            dismiss()
        }

        binding.btnLayout2.btnNational.setOnTouchListener{ view, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    changeTextViewAppearance(view as TextView)
                }
                MotionEvent.ACTION_UP -> {
                    listener(true, null)
                    dismiss()
                }
            }
            true
        }

        binding.btnLayout2.btnDimex.setOnTouchListener{ view, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    changeTextViewAppearance(view as TextView)
                }
                MotionEvent.ACTION_UP -> {
                    listener(true, null)
                    dismiss()
                }
            }
            true
        }

        if(btnCancelStr.isNullOrEmpty()){
            binding.btnLayout.btnCancel.visibility = View.GONE
        }
    }

    private fun changeTextViewAppearance(textView: TextView) {
        val background = textView.background
        if (background is StateListDrawable) {
            val drawable = background.getCurrent()
            if (drawable is GradientDrawable) {
                drawable.setColor(context.resources.getColor(R.color.regal_blue))
            }
        }
        textView.setTextColor(Color.WHITE)
    }

    fun setListener(listener: (result: Boolean, obj: Any?)-> Unit) {
        this.listener = listener
    }

    class Builder(context: Context) {
        private val context: Context = context
        private var title: String? = null
        private var message: String? = null
        private var btnCancel: String? = null
        private var btnConfirm: String? = null
        private var type : Int = 1

        fun title(title: String?): Builder {
            this.title = title
            return this
        }

        fun message(message: String?): Builder {
            this.message = message
            return this
        }

        fun btnCancel(btnCancel: String?): Builder {
            this.btnCancel = btnCancel
            return this
        }

        fun btnConfirm(btnConfirm: String?): Builder {
            this.btnConfirm = btnConfirm
            return this
        }

        fun build(): MDialog {
            return MDialog(context, type ,title, message, btnCancel, btnConfirm)
        }

        fun btnStyle(type : Int):Builder{
            this.type = type
            return this
        }
    }
}