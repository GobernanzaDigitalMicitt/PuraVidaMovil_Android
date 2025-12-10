package gov.raon.micitt.di.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import gov.raon.micitt.R
import gov.raon.micitt.models.response.NotificationData
import gov.raon.micitt.utils.Log

class NoticeAdapter(
    private val notiCnt: Int,
    private val notiList: MutableList<NotificationData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var footerListener: () -> Unit
    private lateinit var itemClickedListener: () -> Unit

    private var item : NotificationData? = null

    companion object TYPE {
        val VIEW_TYPE_NOTICE = R.layout.noti_item
        val VIEW_TYPE_LOAD_MORE = R.layout.layout_btn_more
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = when (viewType) {
            VIEW_TYPE_NOTICE -> LayoutInflater.from(parent.context)
                .inflate(R.layout.noti_item, parent, false)

            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_btn_more, parent, false)
        }

        if (viewType == VIEW_TYPE_LOAD_MORE) {
            return FooterViewHolder(binding)
        }

        return NoticeViewHolder(binding)
    }

    override fun getItemCount(): Int = notiList.size + 1

    fun setMoreNotification(listener: () -> Unit) {
        this.footerListener = listener
    }

    fun setOnItemClicked(listener: () -> Unit) {
        this.itemClickedListener = listener
    }

    fun getItem():NotificationData{
        return this.item!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NoticeViewHolder) {
            if (position <= notiList.size - 1) {
                holder.bind(notiList[position])
            }
        } else {
            (holder as FooterViewHolder).bind()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position <= notiList.size - 1) {
            VIEW_TYPE_NOTICE
        } else {
            VIEW_TYPE_LOAD_MORE
        }
    }

    fun addList(list: MutableList<NotificationData>) {
        this.notiList.addAll(list)
        notifyDataSetChanged()
    }


    inner class NoticeViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        private val date: TextView = binding.findViewById(R.id.noti_date)
        private val title: TextView = binding.findViewById(R.id.noti_title)
        private val summary: TextView = binding.findViewById(R.id.noti_sum)
        private val line: View = binding.findViewById(R.id.noti_break_line)
        private val layout: LinearLayout = binding.findViewById(R.id.noti_item_ll)


        fun bind(notiData: NotificationData) {
            date.text = notiData.createdDt
            title.text = notiData.title
            summary.text = notiData.content

            if (notiData == notiList.last()) {
                line.visibility = View.GONE
            } else {
                line.visibility = View.VISIBLE
            }

            layout.setOnClickListener {
                item = notiData
                itemClickedListener()
            }
        }


    }

    inner class FooterViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        private val footer: ConstraintLayout = binding.findViewById(R.id.btn_more_cl)
        fun bind() {
            footer.setOnClickListener {
                footerListener()
            }
        }
    }

}
