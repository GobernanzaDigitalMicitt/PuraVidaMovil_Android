package gov.raon.micitt.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.databinding.ActivitySettingNoticeBinding
import gov.raon.micitt.di.adapter.NoticeAdapter
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.models.NotificationModel
import gov.raon.micitt.models.response.NotificationData
import kotlinx.serialization.json.JsonObject

@AndroidEntryPoint
class NoticeActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingNoticeBinding
    private lateinit var adapter: NoticeAdapter
    private val notiViewModel: NotiViewModel by viewModels()

    private var pageNum = 1 // 현재 페이지 갯수
    private var pageCnt = 3 // 페이지 내에 있는 공지사항 갯수 (둘다 0인 경우 전체 출력)

    private val notificationModel = NotificationModel(pageNum, pageCnt)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}