package com.example.yourtime

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(private var clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val mList = ArrayList<Event>()
    private val mColors = arrayOf("#F6E58D", "#FFBE76", "#FF7979", "#A29BFE")
    var card: CardView? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_card_view, parent, false)
        card = itemView.findViewById(R.id.card)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // set card view background color
        card?.setCardBackgroundColor(Color.parseColor(mColors[position % 4]))
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
        private val photo: ImageView = view.findViewById(R.id.photo)
        private val note: TextView = view.findViewById(R.id.note)
        private val duration: TextView = view.findViewById(R.id.duration)

        fun bindItems(item: Event, action: OnItemClickListener) {
            when (item.title) {
                "work" -> {
                    photo.setImageResource(R.drawable.work)
                }
                "game" -> {
                    photo.setImageResource(R.drawable.game)
                }
                "exercise" -> {
                    photo.setImageResource(R.drawable.exercise)
                }
                "movie" -> {
                    photo.setImageResource(R.drawable.movie)
                }
                "restaurant" -> {
                    photo.setImageResource(R.drawable.restaurant)
                }
                "other" -> {
                    photo.setImageResource(R.drawable.other)
                }
            }

            note.text = item.note
            duration.text = item.duration

            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }
    }
}