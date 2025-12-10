package gov.raon.micitt.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.BuildConfig
import gov.raon.micitt.R
import gov.raon.micitt.databinding.ActivityHomeBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.models.AgencyModel
import gov.raon.micitt.models.CECIDataModel
import gov.raon.micitt.models.CFIADataModel
import gov.raon.micitt.models.CheckDocumentModel
import gov.raon.micitt.models.DocumentModel
import gov.raon.micitt.models.SaveDocumentModel
import gov.raon.micitt.models.SignDocumentModel
import gov.raon.micitt.models.response.AgencyInfo
import gov.raon.micitt.models.xmlDataModel
import gov.raon.micitt.ui.WebViewActivity
import gov.raon.micitt.ui.aresep.TarifaCombustibleActivity
import gov.raon.micitt.ui.bccr.TipoCambioActivity
import gov.raon.micitt.ui.certificate.CFIACertDetailActivity
import gov.raon.micitt.ui.certificate.CertDetailActivity
import gov.raon.micitt.ui.main.AuthenticationDialog
import gov.raon.micitt.ui.settings.NoticeActivity
import gov.raon.micitt.ui.settings.SettingActivity
import gov.raon.micitt.utils.Util
import java.util.UUID


@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()
    private var hashedNid: String? = null
    private var hashedToken: String? = null
    private var eDocDataType: String? = null

    private var agencyAdapter: AgencyAdapter? = null
    private var documentAdapter: DocumentAdapter? = null
    private var selectDocumentModel: DocumentModel? = null
    private var selectDocumentAgencyName: String? = null
    private var agencyCode: String? = null
    private var dataFormat: String? = null
    private var dataType: String? = null

    private var authenticationDialog: AuthenticationDialog? = null
    private var listSaveDocumentModel: MutableList<SaveDocumentModel>? = null
    private var eDoc: String? = null
    private var isMiCertifi = true
    private lateinit var sharedPreferences: SharedPreferences


    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            getDocument()
        }
    }

    private fun generateShortUuid(): String {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return uuid.chunked(10).first() // 또는 substring(0, 10)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initObservers()

        homeViewModel.getDocumentList(hashedNid!!)
    }

    private fun initView() {
        binding.header.moreRl.visibility = View.VISIBLE
        binding.header.moreRl.setOnClickListener { view ->
            viewPopup(view)
        }

        hashedNid = intent.getStringExtra("hashedNid")
        hashedToken = intent.getStringExtra("hashedToken")

        binding.layerMiCertifi.setOnClickListener {
            isMiCertifi = true

            updateCertUIView()
        }

        binding.layerSoliCertifi.setOnClickListener {

            isMiCertifi = false

            updateCertUIView()

            val agencyModel = AgencyModel("all")
            homeViewModel.getAgencyList(agencyModel)
        }

        binding.layerCertifiEmpty.setOnClickListener {
            isMiCertifi = false

            updateCertUIView()

            val agencyModel = AgencyModel("all")
            homeViewModel.getAgencyList(agencyModel)
        }
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

    private fun updateCertUIView() {
        if (isMiCertifi) {
            binding.layerCertifi.visibility = View.VISIBLE
            binding.layerAgency.visibility = View.GONE

            binding.viewMiCerti.visibility = View.VISIBLE
            binding.viewSoliCerti.visibility = View.GONE

            binding.tvMiCertifi.setTextColor(getColor(R.color.white))
            binding.tvSoliCertifi.setTextColor(getColor(R.color.G40))

            binding.layerMiCertifi.setBackgroundResource(R.color.regal_blue)
            binding.layerSoliCertifi.setBackgroundResource(R.drawable.home_border)

        } else {
            binding.layerCertifi.visibility = View.GONE
            binding.layerAgency.visibility = View.VISIBLE

            binding.viewMiCerti.visibility = View.GONE
            binding.viewSoliCerti.visibility = View.VISIBLE

            binding.tvMiCertifi.setTextColor(getColor(R.color.G40))
            binding.tvSoliCertifi.setTextColor(getColor(R.color.white))

            binding.layerMiCertifi.setBackgroundResource(R.drawable.home_border)
            binding.layerSoliCertifi.setBackgroundResource(R.color.regal_blue)

        }
    }

    private fun updateUIView() {
        if (!listSaveDocumentModel.isNullOrEmpty()) {
            binding.layerCertifiEmpty.visibility = View.GONE
            binding.listCertifi.visibility = View.VISIBLE
        } else {
            binding.layerCertifiEmpty.visibility = View.VISIBLE
            binding.listCertifi.visibility = View.GONE
        }

        updateCertUIView()
    }

    private fun initObservers() {
        homeViewModel.liveSaveDocumentDataList.observe(this) { it ->
            listSaveDocumentModel = it

            if (documentAdapter == null) {
                documentAdapter = DocumentAdapter(this, it)
                documentAdapter!!.setDocumentClickListener {

                    if (it.agencyCode == "0004") {
                        // 여기서 cfia detail activty call
                        Intent(this, CFIACertDetailActivity::class.java).also { act ->
                            act.putExtra("cardObj", it.toJson().toString())
                            startActivity(act)
                        }
                    } else {
                        Intent(this, CertDetailActivity::class.java).also { act ->
                            act.putExtra("cardObj", it.toJson().toString())
                            act.putExtra("hashedToken", hashedToken)
                            startActivity(act)
                        }
                    }
                }

                documentAdapter!!.setOnButtonClicked {
                    getDialogBuilder { it2 ->
                        it2.title("¿Desea eliminar este certificado?")
                        it2.message("El certificado y la información relacionada serán eliminados de inmediato y podrán ser emitidos nuevamente si es necesario.")
                        it2.btnConfirm(getString(R.string.str_delete))
                        it2.btnCancel(getString(R.string.str_cancel))

                        showDialog(it2) { result, _ ->
                            if (result) {
                                documentAdapter!!.deleteItem(it)
                                homeViewModel.deleteDocument(it)

                                listSaveDocumentModel?.remove(it)
                                updateUIView()
                            }
                        }
                    }

                }
                binding.listCertifi.adapter = documentAdapter
            } else {
                documentAdapter!!.clear()
                documentAdapter!!.addList(it)
            }
            updateUIView()
        }

        homeViewModel.liveAgencyList.observe(this) { agencyList ->
            if (agencyAdapter == null) {
                setList(agencyList)
            } else {
                agencyAdapter!!.clear()
                agencyAdapter!!.addList(agencyList)
            }
        }

        homeViewModel.liveDocument.observe(this) {
            if (it.resultCode == "000") {
                eDoc = it.resultData.eDoc

                val eDocData = Util.base64UrlDecode(it.resultData.eDoc)

                if (agencyCode == "0004") {
                    val data = Gson().fromJson(
                        eDocData,
                        CFIADataModel::class.java
                    )

                    if (data.criterioAceptacion == 1) {
                        val signDocumentModel = SignDocumentModel(
                            hashedToken!!,
                            Util.base64UrlEncode("""<NewDataSet><Table><![CDATA[${eDocData}]]></Table></NewDataSet>"""), eDocDataType!!
                        )

                        homeViewModel.signDocument(signDocumentModel)
                    } else {
                        hideProgress()
                        showToast("Este certificado está deshabilitado.")
                    }
                } else if (agencyCode == "0005") {
                    val data = Gson().fromJson(
                        eDocData.replace("\\\"", "\"")
                            .replace("\"{", "{")
                            .replace("}\"", "}"),
                        CECIDataModel::class.java
                    )


                    val signDocumentModel = SignDocumentModel(
                        hashedToken!!,
                        Util.base64UrlEncode("""<NewDataSet><Table><![CDATA[${eDocData}]]></Table></NewDataSet>"""), eDocDataType!!
                    )

                    homeViewModel.signDocument(signDocumentModel)
                } else {
                    val data = Gson().fromJson(
                        eDocData,
                        xmlDataModel::class.java
                    )

                    val signDocumentModel = SignDocumentModel(
                        hashedToken!!,
                        Util.base64UrlEncode(data.strXml), eDocDataType!!
                    )

                    homeViewModel.signDocument(signDocumentModel)
                }
            } else {
                hideProgress()
            }
        }

        homeViewModel.liveSignDocumentStatus.observe(this) {
            authenticationDialog!!.hide()
            if (authenticationDialog!!.isShowing) {
                authenticationDialog!!.hide()
            }

            val signedDoc = homeViewModel.liveSignDocumentStatus.value!!.resultData.toString()


            val eDocData = Util.base64UrlDecode(eDoc)

            val data = Gson().fromJson(eDocData, xmlDataModel::class.java)

            val date = Util.getCurrentDate()
            val fileName =
                if (dataFormat == "JSON") "${BuildConfig.APP_NAME}_${date}" else "${BuildConfig.APP_NAME}_${data.strIdentificacion}"

            try {
                Util.saveFile(this@HomeActivity, fileName, signedDoc)
                Util.saveFileExternal(this@HomeActivity, fileName, signedDoc)
            } catch (e: Exception) {
                getDialogBuilder { builder ->
                    builder.title("Se produjo un error durante la descarga")
                    builder.message("Por favor, intenta de nuevo más tarde.")
                    builder.btnConfirm("Aceptar")

                    showDialog(builder) { result, _ ->

                    }
                }
            }

            if (agencyCode == "0004") {
                homeViewModel.updateDocument(
                    selectDocumentModel!!, hashedNid!!, generateShortUuid(),
                    selectDocumentAgencyName!!, eDoc!!, date
                )
            } else if (agencyCode == "0005") {
                homeViewModel.updateDocument(
                    selectDocumentModel!!, hashedNid!!, generateShortUuid(),
                    selectDocumentAgencyName!!, eDoc!!, date
                )
            } else {
                homeViewModel.updateDocument(
                    selectDocumentModel!!, hashedNid!!, data.strIdentificacion,
                    selectDocumentAgencyName!!, eDoc!!, date
                )
            }



            showToast("Certificado expedido con éxito")

            isMiCertifi = true
            homeViewModel.getDocumentList(hashedNid!!)

            hideProgress()
        }

        homeViewModel.liveSignDocument.observe(this) {
            authenticationDialog = AuthenticationDialog(
                this,
                it.resultData.verificationCode,
                it.resultData.maximumSignatureTimeInSeconds
            )
            authenticationDialog!!.setListener { result ->
                showProgress()
                if (!result) {
                    authenticationDialog!!.setRefreshTime()
                } else {
                    homeViewModel.checkSignDocumentStatus(
                        CheckDocumentModel(
                            hashedToken!!,
                            it.resultData.requestId
                        )
                    )
                }
            }
            authenticationDialog!!.setRefreshListener {
                authenticationDialog?.dismiss()
                getDocument()
            }
            authenticationDialog!!.show()
        }

        homeViewModel.liveErrorData.observe(this) {
            checkSession(this, it)
            hideProgress()
        }
    }

    private fun setList(agencyInfos: MutableList<AgencyInfo>) {
        agencyAdapter = AgencyAdapter(this, agencyInfos)
        binding.listAgency.adapter = this@HomeActivity.agencyAdapter

        agencyAdapter!!.setEmitirListener { item ->
            selectDocumentAgencyName = item.description
            agencyCode = item.agencyCode
            dataFormat = item.dataFormatList!![0]
            dataType = item.dataTypeList!![0]

            getDialogBuilder {
                it.title("¿Desea emitir este certificado?")
                it.message("El certificado se descargará en Pura Vida Móvil.")
                it.btnConfirm(getString(R.string.str_generate))
                it.btnCancel(getString(R.string.str_cancel))
                showDialog(it) { result, _ ->
                    if (result) {
                        if (agencyCode == "0004") {
                            val intent = Intent(this, CFIAConfirmIssuedActivity::class.java)
                            intent.putExtra("agencyName", selectDocumentAgencyName)
                            intent.putExtra("dataFormat", dataFormat)
                            activityResultLauncher.launch(intent)
                        } else {
                            val intent = Intent(this, ConfirmIssuedActivity::class.java)
                            intent.putExtra("agencyName", selectDocumentAgencyName)
                            intent.putExtra("dataFormat", dataFormat)
                            intent.putExtra("agencyCode", agencyCode)
                            activityResultLauncher.launch(intent)
                        }
                    }
                }
            }
        }
    }

    private fun getDocument() {
        showProgress()
        selectDocumentModel = DocumentModel(
            hashedToken!!,
            this.agencyCode!!,
            this.dataFormat!!,
            this.dataType ?: "TAX"
        )
        eDocDataType = selectDocumentModel!!.dataType
        homeViewModel.getDocument(selectDocumentModel!!)
    }

//    private fun certificateDocument() {
//        showProgress()
//        homeViewModel.certificateDocument(
//            CertificateModel(
//                hashedToken!!,
//                this.agencyCode!!,
//                this.dataFormat!!,
//                this.dataType ?: "",
//                ""
//            )
//        )
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            getDocument()
        }
    }

//    override fun onBackPressed() {
//        getDialogBuilder {
//            it.title("Logout")
//            it.message("¿Quieres cerrar sesión en la aplicación?")
//            it.btnConfirm("Sí")
//            it.btnCancel("No")
//            showDialog(it) { result, _ ->
//                if (result) {
//                    this.moveTaskToBack(true)
//                    this.finishAndRemoveTask()
//                    android.os.Process.killProcess(android.os.Process.myPid())
//                }
//            }
//        }
//    }
}

