package gov.raon.micitt.di.common

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import gov.raon.micitt.R
import gov.raon.micitt.models.response.ErrorRes
import gov.raon.micitt.ui.main.MainActivity

open class BaseActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var progress: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        progress = ProgressDialog(this)
    }

    fun showDialog(
        builder: MDialog.Builder,
        listener: (result: Boolean, obj: Any?) -> Unit
    ): Dialog {
        val dialog = builder.build()

        runOnUiThread {
            dialog.setListener { result, obj ->
                dialog.dismiss()
                listener(result, obj)
            }
            if (!isDestroyed) dialog.show()
        }

        return dialog
    }

    fun getDialogBuilder(listener: (MDialog.Builder) -> Unit) = runOnUiThread {
        val builder = MDialog.Builder(this)
        listener(builder)
    }

    fun isProgress(): Boolean {
        return progress != null && progress!!.isShowing
    }

    fun showProgress() = runOnUiThread {
        showProgress(null)
    }

    fun showProgress(listener: (() -> Unit)?) = runOnUiThread {
        if (progress == null) {
            progress = ProgressDialog(this)
        }

        progress!!.setOnShowListener {
            try {
                listener!!()
            } catch (e: Exception) {
            }
        }

        if (!isDestroyed) progress!!.show()
    }

    fun hideProgress() = runOnUiThread {
        if (progress != null && progress!!.isShowing) {
            progress!!.dismiss()
        }
    }

    fun capturePrevention() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    fun checkSession(ctx: Context, resultCode: String) {
        if (resultCode == "902" || resultCode == "903" || resultCode == "901") {
            getDialogBuilder { builder ->
                builder.title(getString(R.string.err_session_expired))
                builder.message(getString(R.string.err_login_again))
                builder.btnConfirm("Aceptar")
                showDialog(builder) { result, _ ->
                    if (result) {
                        sharedPreferences.edit(commit = true) {
                            remove("nid")
                            remove("hashedToken")
                        }

                        Intent(ctx, MainActivity::class.java).also { act ->
                            act.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(act)
                            finish()
                        }
                    }
                }
            }
        }
    }

    fun checkSession(ctx: Context, errorRes: ErrorRes) {
        if (errorRes.resultCode == "902" || errorRes.resultCode == "903" || errorRes.resultCode == "901") {
            getDialogBuilder { builder ->
                builder.title(getString(R.string.err_session_expired))
                builder.message(getString(R.string.err_login_again))
                builder.btnConfirm("Aceptar")
                showDialog(builder) { result, _ ->
                    if (result) {
                        sharedPreferences.edit(commit = true) {
                            remove("nid")
                            remove("hashedToken")
                        }

                        Intent(ctx, MainActivity::class.java).also { act ->
                            act.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(act)
                            finish()
                        }
                    }
                }
            }
        } else {
            showToast("[${errorRes.resultCode}] ${errorRes.resultMsg}")
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}