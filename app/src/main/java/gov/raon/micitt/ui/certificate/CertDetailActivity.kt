package gov.raon.micitt.ui.certificate

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import gov.raon.micitt.databinding.ActivityCertDetailBinding
import gov.raon.micitt.di.DataState
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.di.repository.HttpRepository
import gov.raon.micitt.di.repository.http.HttpListener
import gov.raon.micitt.di.xml.Parser
import gov.raon.micitt.models.CECICertificateDataModel
import gov.raon.micitt.models.CECIDataModel
import gov.raon.micitt.models.CertificateModel
import gov.raon.micitt.models.SaveDocumentModel
import gov.raon.micitt.models.response.DocumentRes
import gov.raon.micitt.models.response.ErrorRes
import gov.raon.micitt.ui.certificate.model.ChildItem
import gov.raon.micitt.ui.certificate.model.ParentItem
import gov.raon.micitt.utils.Log
import gov.raon.micitt.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import javax.inject.Inject

/*
1번째 Table   > Actividades Económicas
3번째 Table1  > Obligaciones Tributarias      :: TIPO_OBLIGATION -> Classification
2번째 Table2  > Representantes Legales        :: nroRelacion / nroInternoIDRepresentatne 없음, 웹에서 표기 안함
5번째 Table3  > Registros Especiales          :: 없앰 추가 안해도뎀
6번째 Table4  > Metodo de Facturacion         :: Numberodocumento 웹에서 표기 안함
4번째 Table5  > Regimenes Especiales
7번째 Table6  > Factores de Retencion IVA
8번째 Table7  > Factories de Retencion Renta
 */

@AndroidEntryPoint
class CertDetailActivity : BaseActivity() {
    @Inject
    lateinit var httpRepository: HttpRepository
    private lateinit var binding: ActivityCertDetailBinding
    private lateinit var adapter: CertDetailAdapter
    val liveCertificate = MutableLiveData<DocumentRes>()
    val liveErrorData = MutableLiveData<ErrorRes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener {
            finish()
        }

        initDocInfo()

        liveCertificate.observe(this) { it ->
            if (it.resultCode == "000") {
                val data = Gson().fromJson(
                    Util.base64UrlDecode(it.resultData.eDoc),
                    CECICertificateDataModel::class.java
                )

                val card: SaveDocumentModel =
                    Gson().fromJson(intent.getStringExtra("cardObj"), SaveDocumentModel::class.java)
                val ceciData = Gson().fromJson(
                    Util.base64UrlDecode(card.eDoc).replace("\\\"", "\"")
                        .replace("\"{", "{")
                        .replace("}\"", "}"),
                    CECIDataModel::class.java
                )

                var code = ""

                if (ceciData.cursos.isNotEmpty()) {
                    ceciData.cursos.get(0).let {
                        code = it.codigo
                    }
                }

                val pdfData: ByteArray = Base64.decode(data.data, Base64.DEFAULT)
                val cacheFile = File(this.cacheDir, """CECI_${code.split("-").let {
                    it.size.let { it1 -> if (it1 > 2) it.get(2) ?: "" else "" }
                }}.pdf""")
                cacheFile.writeBytes(pdfData)

                val uri = FileProvider.getUriForFile(
                    this,
                    "${this.packageName}.fileprovider",
                    cacheFile
                )



                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                this.startActivity(Intent.createChooser(intent, "공유하기"))

                hideProgress()
            } else {
                hideProgress()
            }
        }

        liveErrorData.observe(this) {
            hideProgress()
            showToast("No hay certificado.")
        }
    }

    private fun initDocInfo() {
        val data: SaveDocumentModel =
            Gson().fromJson(intent.getStringExtra("cardObj"), SaveDocumentModel::class.java)
        val xmlData = Util.base64UrlDecode(data.eDoc)
        prepareDocument(xmlData, data)
    }

    private fun prepareDocument(xmlString: String, cardInfo: SaveDocumentModel) {
        if (cardInfo.agencyCode == "0005") {
            val data = Gson().fromJson(
                xmlString.replace("\\\"", "\"")
                    .replace("\"{", "{")
                    .replace("}\"", "}"),
                CECIDataModel::class.java
            )

            val map = mutableMapOf<Int, List<ParentItem>>()

            var parentList = mutableListOf<ParentItem>()
            var childList = mutableListOf<ChildItem>()

            data.cursos.forEachIndexed { index, it ->
                childList.add(ChildItem("curso", it.curso))
                childList.add(ChildItem("date", cardInfo.date))
                childList.add(ChildItem("codigo", it.codigo))
                childList.add(ChildItem("detail", it.detail?.joinToString("|") { item -> item.line } ?: ""))

                parentList.add(ParentItem(0, childList))
                map.put(index, parentList)

                parentList = mutableListOf()
                childList = mutableListOf()
            }

            adapter = CertDetailAdapter(map, cardInfo, emptyList())
            adapter.setBtnShareClickListener {
                showProgress()
                CoroutineScope(Dispatchers.IO).launch {
                    httpRepository.certificateDocument(
                        CertificateModel(
                            intent.getStringExtra("hashedToken") ?: "",
                            cardInfo.agencyCode ?: "",
                            cardInfo.dataFormat ?: "",
                            cardInfo.dataType ?: "",
                            it
                        )
                    ).collect { that ->
                        when (that) {
                            DataState.Loading -> {

                            }

                            is DataState.Success -> {
                                httpRepository.filterResponse(
                                    that.data as Response<*>,
                                    HttpListener({ success ->
                                        try {
                                            val data = Gson().fromJson(success.toString(), DocumentRes::class.java)
                                            if (data != null) {
                                                liveCertificate.postValue(data)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }, { fail ->
                                        try {
                                            val errorData = Gson().fromJson(fail.toString(), ErrorRes::class.java)
                                            liveErrorData.postValue(errorData)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    })
                                )
                            }

                            is DataState.Error -> {
                                Log.d("Request Network Error")
                            }
                        }

                    }
                }
            }
        } else {
            val parser = Parser()
            parser.parse(xmlString)
            val pItem = parser.getElements().groupBy { it.tableNum }
            adapter = CertDetailAdapter(pItem, cardInfo, parser.getEdited())
        }

        binding.detailRv.layoutManager = LinearLayoutManager(this)
        binding.detailRv.adapter = adapter
    }
}
