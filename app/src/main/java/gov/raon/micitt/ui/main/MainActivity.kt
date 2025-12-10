package gov.raon.micitt.ui.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivityMainBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.models.CheckAuthModel
import gov.raon.micitt.models.SignModel
import gov.raon.micitt.models.response.SignRes
import gov.raon.micitt.ui.home.HomeActivity
import gov.raon.micitt.utils.Log
import gov.raon.micitt.utils.Util


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var nId: String? = null
    private lateinit var authDialog: AuthenticationDialog
    private var signUpAlertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initObservers()
    }

    private fun initView() {
        binding.tvSignup.setOnClickListener {
            handleSignUp()
        }

        binding.tvSignin.btnConfirm.visibility = View.GONE
        binding.tvSignin.btnConfirm.text = getString(R.string.str_login)
        binding.tvSignin.btnCancel.text = getString(R.string.str_login)
        binding.tvSignin.btnCancel.setTextColor(getColor(R.color.G50))
        binding.tvSignin.btnCancel.setBackgroundResource(R.drawable.btn_login)
        binding.etNid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.length >= 9) {
                    binding.tvSignin.btnConfirm.visibility = View.VISIBLE
                    binding.tvSignin.btnCancel.visibility = View.GONE
                } else {
                    binding.tvSignin.btnConfirm.visibility = View.GONE
                    binding.tvSignin.btnCancel.visibility = View.VISIBLE
                }
            }
        })

        binding.tvSignin.btnConfirm.setOnClickListener {
            handleSignIn()
        }
    }

    private fun handleSignUp() {
        val nId = binding.etNid.text.toString()

        if (nId.length < 9 || nId.isEmpty()) {
            showToast("La cédula debe ser de al menos 9 dígitos")
            return
        }

        if (nId.isNotEmpty()) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_sign_up, null)
            val checkboxAccept = dialogView.findViewById<CheckBox>(R.id.checkbox_accept)
            val termsLink = dialogView.findViewById<TextView>(R.id.tv_terms_link)

            termsLink.setOnClickListener {
                val termsUrl = getString(R.string.str_terms_url)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl))
                startActivity(intent)
            }

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Requiere autenticación GAUDI")
                .setView(dialogView)
                .setPositiveButton("Continuar", null) // No asignes el listener aquí
                .setNegativeButton(getString(R.string.str_cancel), null) // No asignes el listener aquí

            signUpAlertDialog = dialogBuilder.create()
            signUpAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(getColor(R.color.transparent)))
            signUpAlertDialog?.show()

            // Personalizar el botón "Continuar"
            signUpAlertDialog?.let { alertDialog ->
                val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(getColor(R.color.white)) // Cambiar color del texto
                positiveButton.setBackgroundColor(getColor(R.color.regal_blue)) // Cambiar color de fondo

                // Asignar el listener al botón "Continuar"
                positiveButton.setOnClickListener {
                    if (checkboxAccept.isChecked) {
                        showProgress()
                        val signModel = SignModel(Util.hashSHA256(nId).toString(), nId)
                        mainViewModel.reqSignUp(this, signModel)
                    } else {
                        showToast(getString(R.string.err_terms_and_conditions))
                    }
                }

                // Personalizar el botón "Cancelar"
                val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setTextColor(getColor(R.color.regal_blue)) // Cambiar color del texto
                negativeButton.setBackgroundColor(getColor(R.color.white)) // Cambiar color de fondo

                // Asignar el listener al botón "Cancelar"
                negativeButton.setOnClickListener {
                    alertDialog.dismiss()
                    signUpAlertDialog = null
                }
            }
        }
    }

    private fun handleSignIn() {
        nId = binding.etNid.text.toString()
        if (nId!!.length < 9) {
            showToast("Por favor introduzca al menos 9 dígitos")
            return
        }
        if (!nId.isNullOrEmpty()) {
            val signModel = SignModel(Util.hashSHA256(nId!!).toString(), null)
            showProgress()
            mainViewModel.reqSignIn(this, signModel)
        }
    }

    private fun initObservers() {
        mainViewModel.liveSignUpResponse.observe(this) { response ->
            handleAuthResponse(response, isSignUp = true)
        }

        mainViewModel.liveSignInResponse.observe(this) { response ->
            handleAuthResponse(response, isSignUp = false)
        }

        mainViewModel.liveCheckSignInStatus.observe(this) {
            val userName = it.resultData.userName.substringBefore("autoriza")
            navigateToHome(it.resultData.hashedToken, userName)
        }

        mainViewModel.liveCheckSignUpStatus.observe(this) {
            hideProgress()
            authDialog.dismiss()
            showToast("Registro completado con éxito")
            signUpAlertDialog?.dismiss()
        }

        mainViewModel.liveSignErrorResponse.observe(this) { result ->
            getDialogBuilder { builder ->
                builder.title("Pura Vida Móvil")
                builder.message(result.resultMsg)
                builder.btnConfirm("Confirmar")

                showDialog(builder) { it, _ ->
                    Log.d(result.resultMsg)
                    hideProgress()
                }
            }
        }

        mainViewModel.liveCheckAuthErrorResponse.observe(this) {
            handleError(it.resultMsg)
        }
    }

    private fun handleAuthResponse(response: SignRes, isSignUp: Boolean) {
        hideProgress()

        authDialog = AuthenticationDialog(
            this,
            response.resultData.verificationCode,
            response.resultData.maximumSignatureTimeInSeconds
        ).apply {
            setListener {
                showProgress()
                val checkAuthModel = CheckAuthModel(response.resultData.requestId)
                if (isSignUp) {
                    mainViewModel.reqCheckSignUpStatus(checkAuthModel)
                } else {
                    mainViewModel.reqSignInStatus(checkAuthModel)
                }
            }

            setRefreshListener {
                authDialog.dismiss()
                handleSignIn()
            }
        }

        authDialog.show()
    }

    private fun navigateToHome(hashedToken: String, userName: String) {
        hideProgress()
        authDialog.dismiss()
        editor.putString("nid", nId)
        editor.putString("hashedToken", hashedToken)
        editor.putString("userName", userName)
        editor.apply()
        Intent(this, HomeActivity::class.java).also { intent ->
            intent.putExtra("hashedNid", Util.hashSHA256(nId!!))
            intent.putExtra("hashedToken", hashedToken)
            startActivity(intent)
            finish()
        }
    }

    private fun handleError(errorMessage: String) {
        Log.d("ERROR :: $errorMessage")
        hideProgress()
        showToast(errorMessage)
    }

}