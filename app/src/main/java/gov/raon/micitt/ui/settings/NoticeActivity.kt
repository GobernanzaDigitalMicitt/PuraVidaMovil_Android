package gov.raon.micitt.ui.settings

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
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivitySettingNoticeBinding
import gov.raon.micitt.di.adapter.NoticeAdapter
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.models.NotificationModel
import gov.raon.micitt.models.response.NotificationData
import gov.raon.micitt.ui.WebViewActivity
import gov.raon.micitt.ui.aresep.TarifaCombustibleActivity
import gov.raon.micitt.ui.bccr.TipoCambioActivity
import kotlinx.serialization.json.JsonObject
import android.animation.ObjectAnimator
import android.view.animation.BounceInterpolator

@AndroidEntryPoint
class NoticeActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingNoticeBinding
    private lateinit var adapter: NoticeAdapter
    private val notiViewModel: NotiViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    private var pageNum = 1 // 현재 페이지 갯수
    private var pageCnt = 3 // 페이지 내에 있는 공지사항 갯수 (둘다 0인 경우 전체 출력)

    private val notificationModel = NotificationModel(pageNum, pageCnt)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        binding = ActivitySettingNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initHeaderView()
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener {
            finish()
        }

        initObservers()
        notiViewModel.getNotice<JsonObject>(this, notificationModel)

    }

    private fun initObservers() {
        notiViewModel.notiLiveList.observe(this) {
            hideProgress()
            if (binding.notiList.adapter == null) {
                if (it.resultData.notificationList.size > 0) {
                    binding.notiEmpty.visibility = View.GONE
                } else {
                    binding.notiEmpty.visibility = View.VISIBLE
                }
                setNotifications(it.resultData.notificationCnt, it.resultData.notificationList)
            } else {
                (binding.notiList.adapter as NoticeAdapter).addList(it.resultData.notificationList)
            }
            binding.notiList.scrollToPosition(adapter.itemCount - 1)
        }

        notiViewModel.liveErrorData.observe(this){
            checkSession(this,it)
        }

    }

    private fun setNotifications(notiCnt: Int, notiList: MutableList<NotificationData>) {
        adapter = NoticeAdapter(notiCnt, notiList)
        binding.notiList.layoutManager = LinearLayoutManager(this)
        binding.notiList.adapter = adapter
        adapter.setMoreNotification {
            showProgress()
            if (pageNum * pageCnt >= notiViewModel.notiLiveList.value?.resultData!!.notificationCnt) {
                showToast("No hay más anuncios que mostrar")
            }

            pageNum++
            val sample = NotificationModel(pageNum, pageCnt)
            notiViewModel.getNotice<JsonObject>(this, sample)
        }

        adapter.setOnItemClicked {
            val item: NotificationData = adapter.getItem()
            val intent = Intent(this@NoticeActivity, NoticeDetailActivity::class.java)
            intent.putExtra("title", item.title)
            intent.putExtra("content", item.content)
            intent.putExtra("updated", item.updatedDt)

            startActivity(intent)
        }
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
                    ActivityCompat.requestPermissions(this@NoticeActivity,
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