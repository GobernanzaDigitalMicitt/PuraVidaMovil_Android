package gov.raon.micitt.ui.certificate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import gov.raon.micitt.R
import gov.raon.micitt.models.SaveDocumentModel
import gov.raon.micitt.ui.certificate.model.ParentItem

class CertDetailAdapter(
    private val pItem: Map<Int, List<ParentItem>>,
    private val card: SaveDocumentModel,
    private val removedList: List<Pair<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: () -> Unit
    private var btnShareClickListener: ((code: String) -> Unit)? = null

    companion object TYPE {
        val VIEW_TYPE_DETAIL_TITLE = R.layout.cert_detail_item
        val VIEW_TYPE_CARD = R.layout.item_document
        val VIEW_TYPE_BUTTON = R.layout.layout_btn_more
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = when (viewType) {
            VIEW_TYPE_DETAIL_TITLE -> LayoutInflater.from(parent.context)
                .inflate(R.layout.cert_detail_item, parent, false)

            VIEW_TYPE_CARD -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_document, parent, false)

            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.cert_detail_item, parent, false)
        }

        return when (viewType) {
            VIEW_TYPE_DETAIL_TITLE -> DetailTitleViewHolder(binding)
            VIEW_TYPE_CARD -> CardViewHolder(binding)
            else -> DetailTitleViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (card.agencyCode == "0005") {
            return VIEW_TYPE_DETAIL_TITLE
        } else {
            if (position == 0) {
                return VIEW_TYPE_CARD
            }
            return VIEW_TYPE_DETAIL_TITLE
        }
    }

    override fun getItemCount(): Int {
        if (card.agencyCode == "0005") {
            return pItem.size
        } else {
            return pItem.size + 1
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (card.agencyCode == "0005") {
            (holder as DetailTitleViewHolder).bind(
                pItem.keys.toList()[position],
                btnShareClickListener
            )
        } else {
            if (position == 0) {
                when (holder) {
                    is CardViewHolder -> {
                        holder.bind()
                    }
                }
            } else {
                when (holder) {
                    is DetailTitleViewHolder -> {
                        holder.bind(pItem.keys.toList()[position - 1], btnShareClickListener)
                    }
                }
            }
        }

    }

    inner class DetailTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ceciCard: RelativeLayout = itemView.findViewById(R.id.detail_ceci_card)
        private val tvNameLayout: RelativeLayout = itemView.findViewById(R.id.tv_name_layout)
        private val btnDelete: ImageView = ceciCard.findViewById(R.id.btn_delete)
        private val btnShare: ImageView = ceciCard.findViewById(R.id.btn_ceci_share)
        private val dataType: TextView = ceciCard.findViewById(R.id.tv_type)
        private val dataName: TextView = ceciCard.findViewById(R.id.tv_type)
        private val ceciIssuer: LinearLayout = ceciCard.findViewById(R.id.ceci_issuer)
        private val ceciIssueDate: LinearLayout = ceciCard.findViewById(R.id.ceci_issue_date)
        private val ceciCourseInfo: LinearLayout = ceciCard.findViewById(R.id.ceci_course_info)
        private val ceciDateInfo: LinearLayout = ceciCard.findViewById(R.id.ceci_date_info)
        private val ceciCardNumberInfo: LinearLayout =
            ceciCard.findViewById(R.id.ceci_card_number_info)
        private val ceciCourseTextView: TextView = ceciCard.findViewById(R.id.item_ceci_card_course)
        private val ceciDateTextView: TextView = ceciCard.findViewById(R.id.item_ceci_card_date)
        private val ceciNumberTextView: TextView = ceciCard.findViewById(R.id.item_ceci_card_number)

        private val title: TextView = itemView.findViewById(R.id.detail_item)
        private val btnMore: RelativeLayout = itemView.findViewById(R.id.arrow_up_rl)
        private val arrowUp: View = itemView.findViewById(R.id.arrow_up)
        private val arrowDown: View = itemView.findViewById(R.id.arrow_down)
        private val elemContainer: LinearLayout = itemView.findViewById(R.id.cert_detail_elements)
        private val line: View = itemView.findViewById(R.id.cert_bl)

        private var arrowStatus = false

        fun bind(parentItem: Int, btnShareClickListener: ((code: String) -> Unit)?) {
            if (card.agencyCode == "0005") {
                title.text = "Training Information"
                ceciCard.visibility = View.VISIBLE
                btnShare.visibility = View.VISIBLE
                ceciCourseInfo.visibility = View.VISIBLE
                ceciDateInfo.visibility = View.VISIBLE
                ceciCardNumberInfo.visibility = View.VISIBLE

                tvNameLayout.visibility = View.GONE
                btnDelete.visibility = View.GONE
                dataType.visibility = View.GONE
                dataName.visibility = View.GONE
                ceciIssuer.visibility = View.GONE
                ceciIssueDate.visibility = View.GONE

                if (arrowUp.isVisible) {
                    arrowDown.visibility = View.GONE
                } else {
                    arrowDown.visibility = View.VISIBLE
                }

                var targetCode = ""
                pItem[parentItem]?.forEach {
                    it.elements?.forEachIndexed { index, that ->
                        if (that.key == "curso") {
                            ceciCourseTextView.text = that.value
                        }

                        if (that.key == "date") {
                            ceciDateTextView.text = that.value
                        }

                        if (that.key == "codigo") {
                            ceciNumberTextView.text = that.value
                            targetCode = that.value ?: ""
                        }
                    }
                }

                btnShare.setOnClickListener {
                    btnShareClickListener?.let { that ->
                        that(targetCode)
                    }
                }

                btnMore.setOnClickListener {
                    checkStatus()
                    toggleVisibility(elemContainer)
                    elemContainer.removeAllViews()
                    pItem[parentItem]?.forEach { parent ->
                        parent.elements?.forEachIndexed { index, elem ->
                            if (elem.key == "detail") {
                                val list = elem.value?.split("|")
                                list?.forEach { that ->
                                    val lecture = that.split(":")
                                    val cView = LayoutInflater.from(itemView.context)
                                        .inflate(R.layout.cert_detail, elemContainer, false)
                                    val params = cView.layoutParams as ViewGroup.MarginLayoutParams
                                    params.bottomMargin = 0
                                    cView.layoutParams = params
                                    cView.setPadding(
                                        cView.paddingLeft,
                                        cView.paddingTop,
                                        cView.paddingRight,
                                        24
                                    )

                                    val key: TextView = cView.findViewById(R.id.cert_key)
                                    val value: TextView = cView.findViewById(R.id.cert_value)
                                    val line2: View = cView.findViewById(R.id.cert_detail_bl)

                                    if (lecture.size > 1) {
                                        key.text = lecture[0]
                                        value.text = lecture[1]
                                    }

                                    if (that == "") {
                                        val empty: TextView = cView.findViewById(R.id.cert_empty)
                                        empty.visibility = View.VISIBLE
                                    }

                                    elemContainer.addView(cView)
                                }
                            }
                        } ?: run {
                            val cView = LayoutInflater.from(itemView.context)
                                .inflate(R.layout.cert_detail, elemContainer, false)

                            val key: TextView = cView.findViewById(R.id.cert_key)
                            key.visibility = View.GONE

                            val value: TextView = cView.findViewById(R.id.cert_value)
                            value.visibility = View.GONE

                            val empty: TextView = cView.findViewById(R.id.cert_empty)
                            empty.visibility = View.VISIBLE

                            elemContainer.addView(cView)
                        }
                    }
                }
            } else {
                title.text = titleList[parentItem]
                if (arrowUp.isVisible) {
                    arrowDown.visibility = View.GONE
                } else {
                    arrowDown.visibility = View.VISIBLE
                }

                btnMore.setOnClickListener {
                    checkStatus()
                    toggleVisibility(elemContainer, line)
                    elemContainer.removeAllViews()
                    pItem[parentItem]?.forEach { parent ->
                        parent.elements?.forEachIndexed { index, elem ->
                            val cView = LayoutInflater.from(itemView.context)
                                .inflate(R.layout.cert_detail, elemContainer, false)

                            val key: TextView = cView.findViewById(R.id.cert_key)
                            val value: TextView = cView.findViewById(R.id.cert_value)
                            val line2: View = cView.findViewById(R.id.cert_detail_bl)

                            if (!elem.key.isNullOrEmpty()) {
                                key.text = fixed(titleList[parentItem], elem.key)
                                value.text = elem.value
                            }

                            elemContainer.addView(cView)

                            line2.visibility =
                                if (index == parent.elements.size - 1) View.VISIBLE else View.GONE
                        } ?: run {
                            val cView = LayoutInflater.from(itemView.context)
                                .inflate(R.layout.cert_detail, elemContainer, false)

                            val key: TextView = cView.findViewById(R.id.cert_key)
                            key.visibility = View.GONE

                            val value: TextView = cView.findViewById(R.id.cert_value)
                            value.visibility = View.GONE

                            val empty: TextView = cView.findViewById(R.id.cert_empty)
                            empty.visibility = View.VISIBLE

                            elemContainer.addView(cView)
                        }
                    }

                }
            }


        }

        private fun checkStatus() {
            arrowStatus = !arrowStatus
            arrowDown.visibility = if (arrowStatus) View.VISIBLE else View.GONE
            arrowUp.visibility = if (arrowStatus) View.GONE else View.VISIBLE
        }

        private fun toggleVisibility(vararg views: View) {
            views.forEach { view ->
                view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val type: TextView = itemView.findViewById(R.id.tv_type)
        private val dataType: TextView = itemView.findViewById(R.id.tv_name)
        private val issuerName: TextView = itemView.findViewById(R.id.tv_vc)
        private val date: TextView = itemView.findViewById(R.id.tv_date)

        private val button: ImageView = itemView.findViewById(R.id.btn_delete)

        fun bind() {
            button.visibility = View.GONE
            type.text = card.dataFormat
            dataType.text = card.agencyName
            date.text = card.date
        }
    }

    private val titleList = mapOf(
        0 to "Actividades Económicas",
        1 to "Obligaciones Tributarias",
        2 to "Representantes Legales",
        4 to "Método de Facturación",
        5 to "Regímenes Especiales",
        6 to "Factores de Retención IVA",
        7 to "Factores de Retención Renta",
        8 to "Información",
        9 to "Training Information"
    )

    private fun fixed(tableNum: String?, key: String?): String? {
        val result = when (tableNum) {
            "Información" -> when (key) {
                "strCondicion" -> "Estado Tributario"
                "strEsMoroso" -> "Es Moroso"
                "strEsOmiso" -> "Es Omiso"
                "nroInternoID" -> "Consecutivo"
                "strFechaActualizacion" -> "Fecha de Actualización"
                "strFechaDesinscripcion" -> "Fecha de Desinscripción"
                "strFechaInscripcion" -> "Fecha de Inscripción"
                "strIdentificacion" -> "Identificación"
                "strSistema" -> "Sistema"
                "strAdministracion" -> "Administración"
                "strEstadoTributario" -> "Estado Tributario"
                "strNombreComercial" -> "Nombre y/o Razón Social"
                "strRazonSocial" -> "Nombre y/o Razón Social\n"
                else -> key
            }

            "Actividades Económicas" -> when (key) {
                "ACTIVIDADES_ECONOMICAS" -> "Nombre Actividad"
                "CODIGO_ACTIVIDAD" -> "Código Actividad"
                "TIPO_ESTADO" -> "Estado"
                "FECHA_I_ACTIVIDAD" -> "Fecha Inicio"
                "FECHA_F_ACTIVIDAD" -> "Fecha Fin Actividad"
                else -> key
            }

            "Obligaciones Tributarias" -> when (key) {
                "MODELO" -> "Modelo"
                "DESCRIPCION_MODELO" -> "Descripción"
                "FECHA_INICIO" -> "Fecha de Inicio"
                "FECHA_FIN" -> "Fecha de Fin"
                "TIPO_OBLIGACION" -> "Clasificación"
                "ESTADO" -> "Estado"
                "REGIMEN" -> "Regimen"
                else -> key
            }

            "Representantes Legales" -> when (key) {
                "IDENTIFICACION" -> "Identificación"
                "nroRelacion" -> "nroRelacion"                              // ?????
                "nroInternoIDRepresentante" -> "nroInternoIDRepresentante"  // ?????
                "NOMBRE" -> "Nombre"
                "ESTADO_CONTRIBUYENTE" -> "Registrado como Obligado Tributario"
                "FUENTE_CONTRIBUYENTE" -> "Fuente de información"
                "FECHA_DE_INICIO" -> "Fecha de Inicio"
                else -> key
            }

            "Método de Facturación" -> when (key) {
                "METODOFACTURACION" -> "Método Facturación"
                "FECHAINICIOFACT" -> "Fecha Inicio"
                "NUMERODOCUMENTO" -> "NUMERO DOCUMENTO"
                else -> key
            }

            "Regímenes Especiales" -> when (key) {
                "Tipo_x0020_Regimen" -> "Tipo Régimen"
                "Fecha_x0020_de_x0020_inicio" -> "Fecha de inicio"
                "Documento_x0020_de_x0020_Alta" -> "Documento de Alta"
                "Documento_x0020_de_x0020_Baja" -> "Documento de Baja"
                "Estado" -> "Estado"
                else -> key
            }

            "Factores de Retención IVA" -> when (key) {
                "Ano" -> "Año"
                "Semestre" -> "Semestre"
                "FactorRetencion" -> "Factor Retención"
                "FechaVencimiento" -> "Fecha Vencimiento"
                "FechaCarga" -> "Fecha Carga"
                else -> key
            }

            "Factores de Retención Renta" -> when (key) {
                "Ano" -> "Año"
                "FechaCarga" -> "Fecha Carga"
                "FechaVencimiento" -> "Fecha Vencimiento"
                "FactorRetencion" -> "Factor Retención"
                else -> key
            }

            "Información" -> when (key) {

                else -> " "
            }

            "Training Information" -> when (key) {
                else -> key
            }

            else -> " "
        }
        return result
    }

    fun setBtnShareClickListener(listener: (code: String) -> Unit) {
        this.btnShareClickListener = listener
    }
}
