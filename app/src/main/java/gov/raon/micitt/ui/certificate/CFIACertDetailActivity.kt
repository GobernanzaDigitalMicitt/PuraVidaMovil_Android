package gov.raon.micitt.ui.certificate

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import gov.raon.micitt.databinding.ActivityCfiacertDetailBinding
import gov.raon.micitt.di.common.BaseActivity
import gov.raon.micitt.models.CFIADataModel
import gov.raon.micitt.models.SaveDocumentModel
import gov.raon.micitt.ui.certificate.model.ChildItem
import gov.raon.micitt.ui.certificate.model.ParentItem
import gov.raon.micitt.utils.Util

class CFIACertDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityCfiacertDetailBinding
    private lateinit var adapter: CFIACertDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCfiacertDetailBinding.inflate(layoutInflater)
        binding.header.prevRl.visibility = View.VISIBLE
        binding.header.prevRl.setOnClickListener {
            finish()
        }

        setContentView(binding.root)

        initDocInfo()
    }

    private fun initDocInfo() {
        val data: SaveDocumentModel =
            Gson().fromJson(intent.getStringExtra("cardObj"), SaveDocumentModel::class.java)
        val jsonData = Util.base64UrlDecode(data.eDoc)
        prepareDocument(jsonData, data)
    }

    private fun prepareDocument(jsonString: String, cardInfo: SaveDocumentModel) {
        val data = Gson().fromJson(
            jsonString,
            CFIADataModel::class.java
        )

        val map = mutableMapOf<Int, List<ParentItem>>()

        var parentList = mutableListOf<ParentItem>()
        var childList = mutableListOf<ChildItem>()

        childList.add(ChildItem("Nombre completo", """${data.nombre} ${data.apellidoUno} ${data.apellidoDos}"""))
        childList.add(ChildItem("Número de identificación", data.cedula))
        /**
         * 보안이슈로 인한 주석처리
         */
        // childList.add(ChildItem("Teléfono", data.telefonos.joinToString(separator = ", ")))
        // childList.add(ChildItem("Correo electrónico", data.correosElectronicos.split(";").joinToString(separator = ", ")))

        parentList.add(ParentItem(0, childList))
        map.put(0, parentList)

        parentList = mutableListOf<ParentItem>()
        childList = mutableListOf<ChildItem>()

        childList.add(ChildItem("Carnet", data.carne.joinToString(separator = ", ")))
        childList.add(ChildItem("Rama", data.ramas.joinToString(separator = ", ")))
        childList.add(ChildItem("Colegio", data.colegiosIncoporado.joinToString(separator = ", ")))
        childList.add(ChildItem("Condición", data.condicionMiembro))
        childList.add(ChildItem("Fecha de Incorporación", data.fechasIncorporacion.joinToString(separator = ", ")))

        parentList.add(ParentItem(1, childList))
        map.put(1, parentList)

        adapter = CFIACertDetailAdapter(map, cardInfo)

        binding.detailRv.layoutManager = LinearLayoutManager(this)
        binding.detailRv.adapter = adapter
    }

}