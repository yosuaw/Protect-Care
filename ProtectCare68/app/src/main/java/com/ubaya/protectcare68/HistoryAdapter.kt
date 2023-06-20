package com.ubaya.protectcare68

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_history.view.*

class HistoryAdapter(val histories: ArrayList<History>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(){
    class HistoryViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = histories[position]
        with(holder.view) {
            if(history.checkOut == "") {
                history.checkOut = "Not yet checked out"
            }

            txtHistoryLocation.text = history.location
            txtHistoryCheckIn.text = "Check in: ${history.checkIn}"
            txtHistoryCheckOut.text = "Check out: ${history.checkOut}"

            if(history.vaccine_count == 1) {
                cardHistory.setCardBackgroundColor(Color.parseColor("#FFFF00"))
            } else if(history.vaccine_count > 1) {
                cardHistory.setCardBackgroundColor(Color.parseColor("#4DCB63"))
            }
        }
    }

    override fun getItemCount() = histories.size
}