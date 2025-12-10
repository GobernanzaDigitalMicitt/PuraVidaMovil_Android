package gov.raon.micitt.ui.bccr_backend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gov.raon.micitt.R
import gov.raon.micitt.di.network.TipoCambioResponse


class TipoCambioAdapter(
    private val onItemClick: (TipoCambioResponse) -> Unit = {}
) : RecyclerView.Adapter<TipoCambioAdapter.ExchangeViewHolder>() {

    private var exchangeList: List<TipoCambioResponse> = emptyList()

    fun submitList(newList: List<TipoCambioResponse>) {
        exchangeList = newList
        notifyDataSetChanged()
    }

    inner class ExchangeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val exchangeType: TextView = view.findViewById(R.id.exchangeType)
        private val exchangePrice: TextView = view.findViewById(R.id.exchangePrice)

        fun bind(item: TipoCambioResponse) {
            exchangeType.text = item.descripcion
            exchangePrice.text = item.getPrecioFormateado()
            itemView.setOnClickListener { onItemClick(item) }
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

