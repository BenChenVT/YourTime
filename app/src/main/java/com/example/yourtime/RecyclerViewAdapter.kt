package com.example.yourtime

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter (private var clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val mList = ArrayList<Event>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_card_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mList[position], clickListener)
    }

    interface OnItemClickListener {
        fun onItemClick(user: Event, position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserList(newList: List<Event>) {
        mList.clear()
        mList.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.image_view
        private val address: TextView = view.address
        private val coordinate: TextView = view.coordinate
        private val time: TextView = view.time

        fun bindItems(item: Event, action: OnItemClickListener) {
            address.text = item.address
            coordinate.text = item.coordinates
            time.text = item.date
            Picasso.get().load(item.imageToken).into(imageView)
            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }
    }
}