package gov.raon.micitt.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivityCfiaconfirmIssuedBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.utils.Util

class CFIAConfirmIssuedActivity : BaseActivity() {
    private lateinit var binding: ActivityCfiaconfirmIssuedBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        binding = ActivityCfiaconfirmIssuedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener {
            finish()
        }

        val date = Util.getCurrentDate()
        binding.cfiaIssuedSample.btnDelete.visibility = View.GONE
//        binding.cfiaIssuedSample.tvName.text = intent.getStringExtra("agencyName")
        binding.cfiaIssuedSample.tvDate.text = date
//        binding.cfiaIssuedSample.tvType.text = intent.getStringExtra("dataFormat")

        binding.vcCompleteNid.text = sharedPreferences.getString("nid","null")
        binding.vcCompleteName.text = sharedPreferences.getString("userName","null")
        binding.vcCompleteIssuedDate.text = date

        binding.btnConfirm.setOnClickListener {
            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}