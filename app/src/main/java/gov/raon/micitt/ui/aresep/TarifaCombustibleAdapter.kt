package gov.raon.micitt.ui.aresep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gov.raon.micitt.R

class TarifaCombustibleAdapter(
    private val onItemClick: (TarifaCombustible) -> Unit = {}
) : RecyclerView.Adapter<TarifaCombustibleAdapter.FuelViewHolder>() {

    private var fuelList: List<TarifaCombustible> = emptyList()

    fun submitList(newList: List<TarifaCombustible>) {
        fuelList = newList
    }

    inner class FuelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fuelType: TextView = view.findViewById(R.id.fuelType)
        private val fuelPrice: TextView = view.findViewById(R.id.fuelPrice)

        fun bind(item: TarifaCombustible) {
            fuelType.text = item.nombre
            fuelPrice.text = item.getPrecioFormateado()
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fuel, parent, false)
        return FuelViewHolder(view)
    }

    override fun onBindViewHolder(holder: FuelViewHolder, position: Int) {
        holder.bind(fuelList[position])
    }

    override fun getItemCount(): Int = fuelList.size
}

