package gov.raon.micitt.ui

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivityWebviewBinding
import gov.raon.micitt.di.common.BaseActivity

class WebViewActivity : BaseActivity(){
    private lateinit var binding: ActivityWebviewBinding
    private var termsA = "www.micitt.go.cr/billetera-digital/terminos-condiciones-uso"
    private var privacyA = "www.micitt.go.cr/billetera-digital/politicas-privacidad"
    private var faqA = "www.micitt.go.cr/billetera-digital/faq"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}