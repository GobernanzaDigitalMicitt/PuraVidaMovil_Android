package gov.raon.micitt.ui.home

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import gov.raon.micitt.R
import gov.raon.micitt.models.response.AgencyInfo

class AgencyAdapter(val context: Context, val itemList: MutableList<AgencyInfo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mEmitirListener: ((AgencyInfo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_agency, parent, false)
        return AgencyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AgencyViewHolder).bind(itemList[position], mEmitirListener)
    }

    fun setEmitirListener(listener: (AgencyInfo) -> Unit) {
        this.mEmitirListener = listener
    }

    fun addList(list: MutableList<AgencyInfo>) {
        this.itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        itemList.clear()
        notifyDataSetChanged()
    }

    inner class AgencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvAgencyName: TextView
        var layerAgencyEmitir: ViewGroup
        var tvAgencyEmitir: TextView
        var outerLayer : LinearLayoutCompat

        var disableBtn = false

        init {
            tvAgencyName = view.findViewById(R.id.tv_agency_name)
            layerAgencyEmitir = view.findViewById(R.id.layer_agency_emitir)
            tvAgencyEmitir = view.findViewById(R.id.tv_agency_emitir)
            outerLayer = view.findViewById(R.id.agency_outer_layer)
        }

        fun bind(item: AgencyInfo, mEmitirListener: ((AgencyInfo) -> Unit)?) {
            val outerlayerBg = outerLayer.background as GradientDrawable
            tvAgencyName.text = item.description

            val btnLayer = layerAgencyEmitir.background as GradientDrawable
            val layoutParams = layerAgencyEmitir.layoutParams as ViewGroup.LayoutParams
            val scale = context.resources.displayMetrics.density // dp 값을 px로 변환

            if(item.dataFormatList.isNullOrEmpty()){
                disableBtn = true
            }

            if(item.agencyCode == "0000" || item.agencyCode == "9999" || disableBtn) {
                tvAgencyName.setTextColor(context.getColor(R.color.G40))
                tvAgencyEmitir.setTextColor(context.getColor(R.color.G50))
                outerlayerBg.setStroke(1,context.getColor(R.color.G50))
                btnLayer.setColor(context.getColor(R.color.G20))

                tvAgencyEmitir.text = "Próximamente"
                layoutParams.width = (100 * scale).toInt()
            } else {
                layoutParams.width = (72 * scale).toInt()
                tvAgencyEmitir.text = context.getString(R.string.str_generate)


                btnLayer.setColor(context.getColor(R.color.micitt_theme))
                outerlayerBg.setStroke(2, context.getColor(R.color.micitt_theme))

                layerAgencyEmitir.setOnClickListener {
                    mEmitirListener?.let { it1 -> it1(item) }
                }
            }
        }

    }

}