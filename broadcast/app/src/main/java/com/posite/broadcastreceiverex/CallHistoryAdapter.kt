package com.posite.broadcastreceiverex

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.posite.broadcastreceiverex.databinding.ItemCallhistoryBinding

class CallHistoryAdapter :
    ListAdapter<CallHistory, CallHistoryAdapter.CallHistoryViewHolder>(CallHistoryAdapter.diff) {
    inner class CallHistoryViewHolder(private val binding: ItemCallhistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(callHistory: CallHistory) {
            binding.dateTxt.text = callHistory.date
            binding.typeTxt.text = callHistory.type
            binding.numberTxt.text = callHistory.number
            binding.durationTxt.text = callHistory.duration
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CallHistoryAdapter.CallHistoryViewHolder {
        return CallHistoryViewHolder(
            ItemCallhistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CallHistoryAdapter.CallHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<CallHistory>() {
            override fun areItemsTheSame(
                oldItem: CallHistory,
                newItem: CallHistory
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: CallHistory,
                newItem: CallHistory
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}