package gov.raon.micitt.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gov.raon.micitt.R
import gov.raon.micitt.models.SaveDocumentModel

class DocumentAdapter(val context: Context, val itemList: MutableList<SaveDocumentModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var documentClickListener: ((SaveDocumentModel) -> Unit)? = null
    private var btnClickListener: ((SaveDocumentModel) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].agencyCode) {
            "0004" -> 1
            "0005" -> 2
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.item_cfia_document, parent, false)
                return DocumentViewHolder(view)
            }

            2 -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.item_ceci_card, parent, false)
                return DocumentViewHolder(view)
            }

            else -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.item_document, parent, false)
                return DocumentViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DocumentViewHolder).bind(
            itemList[position],
            documentClickListener,
            btnClickListener
        )
    }

    fun setDocumentClickListener(listener: (SaveDocumentModel) -> Unit) {
        this.documentClickListener = listener
    }

    fun setOnButtonClicked(listener: (SaveDocumentModel) -> Unit) {
        this.btnClickListener = listener
    }

    fun addList(list: MutableList<SaveDocumentModel>) {
        this.itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun deleteItem(item: SaveDocumentModel) {
        itemList.remove(item)
        notifyDataSetChanged()
    }

    fun clear() {
        itemList.clear()
        notifyDataSetChanged()
    }

    inner class DocumentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val type: TextView = view.findViewById(R.id.tv_type)
        private val title: TextView = view.findViewById(R.id.tv_name)
        private val issuerName: TextView = view.findViewById(R.id.tv_vc)
        private val date: TextView = view.findViewById(R.id.tv_date)

        private val btnDelete: ImageView = view.findViewById(R.id.btn_delete)

        val layerDocument: ViewGroup

        init {
            layerDocument = view.findViewById(R.id.layer_document)
        }

        fun bind(
            item: SaveDocumentModel,
            documentClickListener: ((SaveDocumentModel) -> Unit)?,
            btnClickListener: ((SaveDocumentModel) -> Unit)?
        ) {

            if (item.agencyCode == "0005") {
                type.text = item.dataFormat
                title.text = item.agencyName
                issuerName.text = item.dataType
                date.text = item.date
            } else {
                type.text = item.dataFormat
                title.text = item.agencyName
                date.text = item.date
            }

            layerDocument.setOnClickListener {
                documentClickListener?.let { it1 -> it1(item) }
            }

            btnDelete.setOnClickListener {
                btnClickListener?.let { it1 -> it1(item) }
            }
        }
    }

}