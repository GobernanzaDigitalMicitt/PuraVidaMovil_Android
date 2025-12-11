package gov.raon.micitt.ui.main

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.app.ActivityCompat
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivityMainBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.models.CheckAuthModel
import gov.raon.micitt.models.SignModel
import gov.raon.micitt.models.response.SignRes
import gov.raon.micitt.ui.home.HomeActivity
import gov.raon.micitt.utils.Log
import gov.raon.micitt.utils.Util
import android.animation.ObjectAnimator
import android.view.animation.BounceInterpolator
import android.widget.RelativeLayout


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var nId: String? = null
    private lateinit var authDialog: AuthenticationDialog
    private var signUpAlertDialog: AlertDialog? = null
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

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
        binding.fabAgent.setOnClickListener {
            showAgentDialog()
        }

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
        startBounceAnimation()
    }

    private fun startBounceAnimation() {
        // Mueve el botón hacia arriba y luego lo deja "caer" con un interpolador de rebote.
        // Se corrige la referencia a binding.fabAgent en lugar de binding.header.agentButton
        val animator = ObjectAnimator.ofFloat(binding.fabAgent, "translationY", 0f, -20f, 0f).apply {
            duration = 1500 // 1.5 segundos para un rebote completo
            repeatCount = ObjectAnimator.INFINITE // Bucle infinito
            repeatMode = ObjectAnimator.RESTART
            interpolator = BounceInterpolator() // Usa un interpolador de rebote nativo
        }
        animator.start()
    }
    private fun showAgentDialog(){
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_agent)
        val webView = dialog.findViewById<WebView>(R.id.webView)
        val backButton = dialog.findViewById<RelativeLayout>(R.id.back_rl)

        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (request.origin.toString().startsWith("https://assistant.ai-kuanta.com/")) {
                    ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        CAMERA_PERMISSION_REQUEST_CODE)
                    request.grant(request.resources)
                } else {
                    request.deny()
                }
            }
        }
        webView.loadUrl("https://assistant.ai-kuanta.com/en/chatbot/embed/21295d48-7220-43ee-9705-a6696e702036?position=right")

        backButton?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

        // BYPASS TEMPORAL: Redirige directamente a HomeActivity con datos de prueba.
        nId = binding.etNid.text.toString()
        val dummyNid = "000000000"

        if (nId.isNullOrEmpty() || nId!!.length < 9) {
            // Usa un ID ficticio para evitar fallos en Util.hashSHA256(nId!!)
            nId = dummyNid
        }

        val dummyHashedToken = "TEMPORARY_TOKEN_FOR_VISUAL_CHANGES"
        val dummyUserName = "Usuario Temporal"

        // Lógica de navegación directa, omitiendo la autenticación y la gestión de authDialog
        editor.putString("nid", nId)
        editor.putString("hashedToken", dummyHashedToken)
        editor.putString("userName", dummyUserName)
        editor.apply()
        Intent(this, HomeActivity::class.java).also { intent ->
            intent.putExtra("hashedNid", Util.hashSHA256(nId!!))
            intent.putExtra("hashedToken", dummyHashedToken)
            startActivity(intent)
            finish()
        }

        return
        // END TEMPORARY BYPASS

        /* ORIGINAL LOGIC (COMMENTED OUT)
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
        */
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
