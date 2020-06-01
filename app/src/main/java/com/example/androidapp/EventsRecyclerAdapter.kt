package com.example.androidapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventsRecyclerAdapter(context: Context, private val eventList: MutableList<Event>) : RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    private var removedPosition: Int = 0
    private var removedItem = Event("","","")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.event_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = eventList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventList[position]
        holder.dateText.text = event.selectedDate
        holder.eventName.text = event.eventName
        holder.timeLeft.text = event.dateDiff

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText = itemView.findViewById<TextView>(R.id.dateTextView)
        val eventName = itemView.findViewById<TextView>(R.id.event_name_text)
        val timeLeft = itemView.findViewById<TextView>(R.id.time_left_text)
    }

    fun removeEvent(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedItem = eventList[viewHolder.adapterPosition]

        eventList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
    }

    fun restoreItem() {
        eventList.add(removedPosition, removedItem)
        notifyItemInserted(removedPosition)
    }



}