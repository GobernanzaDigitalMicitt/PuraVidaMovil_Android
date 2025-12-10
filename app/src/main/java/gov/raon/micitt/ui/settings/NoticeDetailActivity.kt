package gov.raon.micitt.ui.settings

import android.os.Bundle
import android.view.View
import gov.raon.micitt.databinding.ActivitySettingNoticeDetailBinding
import gov.raon.micitt.di.common.BaseActivity

class NoticeDetailActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingNoticeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingNoticeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener {
            finish()
        }

        val title = intent.getStringExtra("title")
        binding.titleTv.text = title

        val content = intent.getStringExtra("content")
        binding.contentTv.text = content

        val updated = intent.getStringExtra("updated")
        binding.updatedTv.text = updated

        binding.btnBack.setOnClickListener {
            finish()
        }

    }
}