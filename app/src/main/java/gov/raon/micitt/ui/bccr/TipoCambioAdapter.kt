package gov.raon.micitt.ui.bccr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gov.raon.micitt.R


class TipoCambioAdapter(
    private val onItemClick: (IndicadorEconomico) -> Unit = {}
) : RecyclerView.Adapter<TipoCambioAdapter.ExchangeViewHolder>() {

    private var exchangeList: List<IndicadorEconomico> = emptyList()

    fun submitList(newList: ListaTipoCambio) {
        exchangeList = newList.listado
    }

    inner class ExchangeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val exchangeType: TextView = view.findViewById(R.id.exchangeType)
        private val exchangePrice: TextView = view.findViewById(R.id.exchangePrice)

        fun bind(item: IndicadorEconomico) {

            exchangeType.text = item.getDescripcionIndicador()
            exchangePrice.text = item.getPrecioFormateado()
            itemView.setOnClickListener {  }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exchange, parent, false)
        return ExchangeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        holder.bind(exchangeList[position])
    }

    override fun getItemCount(): Int = exchangeList.size
}