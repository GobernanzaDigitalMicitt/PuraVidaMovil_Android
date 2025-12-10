package gov.raon.micitt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.databinding.ActivitySplashBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.ui.home.HomeActivity
import gov.raon.micitt.ui.main.MainActivity
import gov.raon.micitt.utils.PermissionHelper
import gov.raon.micitt.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var denyCount = 0
    private var permissionHelper: PermissionHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPushPermission()
    }

    private fun checkPushPermission() {
        if (permissionHelper == null) {
            permissionHelper = PermissionHelper()
        }

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1500)
                    val nId = sharedPreferences.getString("nid", "null")
                    val hashedToken = sharedPreferences.getString("hashedToken", "null")

                    if (hashedToken !== "null") {
                        Intent(this@SplashActivity, HomeActivity::class.java).also { intent ->
                            intent.putExtra("hashedNid", Util.hashSHA256(nId!!))
                            intent.putExtra("hashedToken", hashedToken)
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)

                        startActivity(intent)
                    }
                }
            } else {
                if (denyCount >= 3) {
                    getDialogBuilder {
                        it.title("Solicitudes de autorización manual")
                        it.message("Pura Vida Móvil requiere permiso de almacenamiento para su uso")
                        it.btnCancel("")
                        it.btnConfirm("pasar a la configuración")
                        showDialog(it) { result, _ ->
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    permissionHelper!!.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION)
                    ) { isGranted ->
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1500)
                            if (isGranted) {
                                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                denyCount++
                                getDialogBuilder {
                                    it.title("Error en la autorización")
                                    it.message("Pura Vida Móvil requiere permiso de almacenamiento para su uso")
                                    it.btnConfirm("aplicación de salida")
                                    showDialog(it) { result, _ ->
                                        checkPushPermission()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1500)
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper?.onRequestPermissionsResult(requestCode, grantResults);
    }

    override fun onBackPressed() {
        this.moveTaskToBack(true)
        this.finishAndRemoveTask()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}