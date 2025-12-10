package gov.raon.micitt.ui.certificate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import gov.raon.micitt.R
import gov.raon.micitt.databinding.CfiaCertDetailItemBinding
import gov.raon.micitt.databinding.ItemCfiaDocumentBinding
import gov.raon.micitt.models.CFIADataModel
import gov.raon.micitt.models.SaveDocumentModel
import gov.raon.micitt.ui.certificate.model.ParentItem
import gov.raon.micitt.utils.Util

class CFIACardHolder(val binding: ItemCfiaDocumentBinding) : RecyclerView.ViewHolder(binding.root)
class CFIACertDetailHolder(val binding: CfiaCertDetailItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class CFIACertDetailAdapter(
    private val pItem: Map<Int, List<ParentItem>>,
    private val card: SaveDocumentModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object TYPE {
        val VIEW_TYPE_DETAIL_TITLE = R.layout.cfia_cert_detail_item
        val VIEW_TYPE_CFIA_CARD = R.layout.item_cfia_document
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DETAIL_TITLE -> CFIACertDetailHolder(
                CfiaCertDetailItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )

            else -> CFIACardHolder(
                ItemCfiaDocumentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (position == 0) {
            val binding = (holder as CFIACardHolder).binding

            binding.cfiaLogoSmall.visibility = View.GONE
            binding.cfiaLogoLarge.visibility = View.VISIBLE
            binding.btnDelete.visibility = View.GONE
            binding.tvType.visibility = View.GONE
            binding.memType.visibility = View.VISIBLE
            binding.defaultLayout.visibility = View.GONE
            binding.memberLayout.visibility = View.VISIBLE
            binding.defaultValueLayout.visibility = View.GONE
            binding.memberValueLayout.visibility = View.VISIBLE


            val data = Gson().fromJson(
                Util.base64UrlDecode(card.eDoc),
                CFIADataModel::class.java
            )

            binding.tvCode.text = data.cedula
            binding.tvLicense.text = data.carne.joinToString(separator = ", ")
            binding.tvName.text = """${data.nombre} ${data.apellidoUno} ${data.apellidoDos}"""
            binding.memType.text = """${data.condicionMiembro}"""
            binding.tvDate.text = card.date

        } else {
            val binding = (holder as CFIACertDetailHolder).binding
            val infoContainer: LinearLayout = binding.certDetailElements
            val btnMore: RelativeLayout = binding.arrowUpRl
            val arrowUp: View = binding.arrowUp
            val arrowDown: View = binding.arrowDown
            var arrowStatus = false

            if (position == 1) {
                binding.detailItem.text = "Datos de Miembro"
            } else {
                binding.detailItem.text = "Datos de Membresía"
            }

            if (arrowUp.isVisible) {
                arrowDown.visibility = View.GONE
            } else {
                arrowDown.visibility = View.VISIBLE
            }

            btnMore.setOnClickListener {
                arrowStatus = !arrowStatus
                arrowDown.visibility = if (arrowStatus) View.VISIBLE else View.GONE
                arrowUp.visibility = if (arrowStatus) View.GONE else View.VISIBLE

                toggleVisibility(infoContainer)
                infoContainer.removeAllViews()
                pItem[position - 1]?.forEach { parent ->
                    parent.elements?.forEachIndexed { index, item ->
                        val cView = LayoutInflater.from(holder.itemView.context)
                            .inflate(R.layout.cfia_cert_detail, infoContainer, false)

                        val params = cView.layoutParams as ViewGroup.MarginLayoutParams
                        if (index == 0) {
                            params.topMargin = 0
                        } else {
                            params.topMargin = 28
                        }
                        cView.layoutParams = params

                        val key: TextView = cView.findViewById(R.id.cert_key)
                        val value: TextView = cView.findViewById(R.id.cert_value)

                        key.text = item.key
                        value.text = item.value

                        infoContainer.addView(cView)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_CFIA_CARD else VIEW_TYPE_DETAIL_TITLE
    }

    override fun getItemCount(): Int {
        return pItem.size + 1
    }

    private fun toggleVisibility(vararg views: View) {
        views.forEach { view ->
            view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }
}