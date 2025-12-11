package gov.raon.micitt.ui

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivityWebviewBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.ui.aresep.TarifaCombustibleActivity
import gov.raon.micitt.ui.bccr.TipoCambioActivity
import gov.raon.micitt.ui.settings.NoticeActivity
import gov.raon.micitt.ui.settings.SettingActivity
import android.animation.ObjectAnimator
import android.view.animation.BounceInterpolator

class WebViewActivity : BaseActivity(){
    private lateinit var binding: ActivityWebviewBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private var termsA = "www.micitt.go.cr/billetera-digital/terminos-condiciones-uso"
    private var privacyA = "www.micitt.go.cr/billetera-digital/politicas-privacidad"
    private var faqA = "www.micitt.go.cr/billetera-digital/faq"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initHeaderView()
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener{
            finish()
        }

        val whereTo = intent.getStringExtra("address")

        val webView : WebView = findViewById(R.id.webview_layout)
        webView.webViewClient = WebViewClient()

        var url : String = ""
        when(whereTo){
            "terms" -> url = termsA
            "privacy" -> url = privacyA
            "faq" -> url = faqA
        }

        webView.loadUrl(url)
    }

    private fun initHeaderView() {
        binding.header.moreRl.visibility = View.VISIBLE
        binding.header.agentButton.visibility = View.VISIBLE
        binding.header.moreRl.setOnClickListener { view ->
            viewPopup(view)
        }
        binding.header.agentButton.setOnClickListener {
            showAgentDialog()
        }
        startBounceAnimation()
    }

    private fun startBounceAnimation() {
        val animator = ObjectAnimator.ofFloat(binding.header.agentButton, "translationY", 0f, -8f, 0f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            interpolator = BounceInterpolator()
        }
        animator.start()
    }

    private fun viewPopup(view: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.menu_popup, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                android.R.color.transparent
            )
        )

        val profileItem: TextView = popupView.findViewById(R.id.profile_item)
        val noticeItem: TextView = popupView.findViewById(R.id.notice_item)
        val faqItem: TextView = popupView.findViewById(R.id.faq_item)
        val licenceItem: TextView = popupView.findViewById(R.id.licence_item)
        val fuelItem: TextView = popupView.findViewById(R.id.fuel_item)
        val exchangeItem: TextView = popupView.findViewById(R.id.exchange_item)

        profileItem.setOnClickListener {
            Intent(this, SettingActivity::class.java).also { intent ->
                intent.putExtra("nid", sharedPreferences.getString("nid", "null"))
                intent.putExtra("userName", sharedPreferences.getString("userName", "null"))
                startActivity(intent)
            }
            popupWindow.dismiss()
        }

        noticeItem.setOnClickListener {
            Intent(this, NoticeActivity::class.java).also { intent ->
                startActivity(intent)
            }
            popupWindow.dismiss()
        }

        faqItem.setOnClickListener {
            Intent(this, WebViewActivity::class.java).also { intent ->
                intent.putExtra("address", "faq")
                startActivity(intent)
            }
            popupWindow.dismiss()
        }

        licenceItem.setOnClickListener {
            Intent(this, OssLicensesMenuActivity::class.java).also { intent ->
                OssLicensesMenuActivity.setActivityTitle("Información de Licencias")
                startActivity(intent)
            }
            popupWindow.dismiss()
        }

        fuelItem.setOnClickListener {
            Intent(this, TarifaCombustibleActivity::class.java).also { intent ->
                startActivity(intent)
            }
            popupWindow.dismiss()
        }

        exchangeItem.setOnClickListener {
            Intent(this, TipoCambioActivity::class.java).also { intent ->
                startActivity(intent)
            }
            popupWindow.dismiss()
        }

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val screenWidth = size.x

        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val xOffset = screenWidth - (location[0] + view.width)
        val yOffset = 0

        popupWindow.showAsDropDown(view, xOffset, yOffset, Gravity.END)
    }

    private fun showAgentDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_agent)
        val webView = dialog.findViewById<WebView>(R.id.webView)
        val backButton = dialog.findViewById<RelativeLayout>(R.id.back_rl)

        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (request.origin.toString().startsWith("https://assistant.ai-kuanta.com/")) {
                    ActivityCompat.requestPermissions(this@WebViewActivity,
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
}