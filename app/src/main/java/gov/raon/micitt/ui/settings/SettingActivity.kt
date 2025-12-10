package gov.raon.micitt.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.BuildConfig
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivitySettingBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.ui.WebViewActivity
import gov.raon.micitt.ui.main.MainActivity
import kotlin.or
import androidx.core.content.edit


@AndroidEntryPoint
class SettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener {
            finish()
        }

        binding.settingLogout.setOnClickListener {
            getDialogBuilder { builder ->
                builder.title("¿Deseas cerrar sesión?")
                builder.message("Incluso después de cerrar sesión, puedes verificar los certificados emitidos iniciando sesión nuevamente.")
                builder.btnConfirm(getString(R.string.str_logout))
                builder.btnCancel(getString(R.string.str_cancel))

                showDialog(builder) { result, _ ->
                    if (result) {
                        showProgress()
                        val hashToken = sharedPreferences.getString("hashedToken", "null")

                        sharedPreferences.edit(commit = true) {
                            remove("nid")
                            remove("hashedToken")
                        }

                        viewModel.logOut<JsonObject>(this, hashToken!!)
                        viewModel.logoutLiveList.observe(this) {
                            if (it.resultCode == "000") {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                                Runtime.getRuntime().exit(0)
                            } else {
                                checkSession(this,it.resultCode)
                            }
                        }
                    }
                }
            }
        }

        viewModel.liveErrorData.observe(this){
            checkSession(this,it)
        }

        binding.settingPrivacy.setOnClickListener{
            Intent(this, WebViewActivity::class.java).also { intent ->
                intent.putExtra("address","privacy")
                startActivity(intent)
            }
        }
        binding.settingCondition.setOnClickListener{
            Intent(this, WebViewActivity::class.java).also { intent ->
                intent.putExtra("address","terms")
                startActivity(intent)
            }
        }

        binding.settingRevokeService.setOnClickListener {
            getDialogBuilder {
                Intent(this, SettingUnsubscribeActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }

        val nid = sharedPreferences.getString("nid","null")
        binding.infoNidString.text = nid

        val userName = sharedPreferences.getString("userName","null")
        binding.infoNameString.text=userName

        val version = "v.${BuildConfig.VERSION_NAME}"
        binding.settingGuideAppVersion.text = version
    }
}