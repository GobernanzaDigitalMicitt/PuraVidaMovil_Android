package gov.raon.micitt.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivityVcIssueCompleteBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.utils.Util

class ConfirmIssuedActivity : BaseActivity() {

    private lateinit var binding: ActivityVcIssueCompleteBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        binding = ActivityVcIssueCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener {
            finish()
        }

        binding.vcCompleteBtn.btnCancel.visibility = View.GONE
        binding.vcCompleteBtn.btnConfirm.text = "Aceptar"

        /* vc detail */
        val date = Util.getCurrentDate()

        if (intent.getStringExtra("agencyCode") == "0005") {
            binding.vcCompleteBtn.btnConfirm.setBackgroundColor(Color.parseColor("#CAE857"))
            binding.vcCompleteBtn.btnConfirm.setTextColor(Color.parseColor("#000000"))
            findViewById<View>(R.id.vc_ceci_issued_sample).visibility = View.VISIBLE
            findViewById<View>(R.id.vc_issued_sample).visibility = View.GONE
            binding.vcCeciIssuedSample.btnDelete.visibility = View.GONE
//            binding.vcCeciIssuedSample.tvName.text = intent.getStringExtra("agencyName")
            binding.vcCeciIssuedSample.tvDate.text = date
            binding.vcCeciIssuedSample.tvType.text = intent.getStringExtra("dataFormat")
            binding.vcCeciIssuedSample.tvVc.text = "CECI"

            binding.vcCompleteNid.text = sharedPreferences.getString("nid", "null")
            binding.vcCompleteName.text = sharedPreferences.getString("userName", "null")
            binding.vcCompleteIssuedDate.text = date
        } else {
            findViewById<View>(R.id.vc_ceci_issued_sample).visibility = View.GONE
            findViewById<View>(R.id.vc_issued_sample).visibility = View.VISIBLE
            binding.vcIssuedSample.btnDelete.visibility = View.GONE
            binding.vcIssuedSample.tvName.text = intent.getStringExtra("agencyName")
            binding.vcIssuedSample.tvDate.text = date
            binding.vcIssuedSample.tvType.text = intent.getStringExtra("dataFormat")

            binding.vcCompleteNid.text = sharedPreferences.getString("nid", "null")
            binding.vcCompleteName.text = sharedPreferences.getString("userName", "null")
            binding.vcCompleteIssuedDate.text = date
        }

        binding.vcCompleteBtn.btnConfirm.setOnClickListener {
            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun findViewById(id: Any) {}

}