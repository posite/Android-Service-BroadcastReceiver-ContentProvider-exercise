package com.posite.broadcastreceiverex.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.posite.broadcastreceiverex.databinding.ItemContactBinding

class ContactsAdapter : ListAdapter<Contacts, ContactsAdapter.ContactsViewHolder>(diff) {
    inner class ContactsViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contacts: Contacts) {
            binding.nameTxt.text = contacts.name
            binding.numberTxt.text = contacts.number
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsViewHolder {
        return ContactsViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<Contacts>() {
            override fun areItemsTheSame(
                oldItem: Contacts,
                newItem: Contacts
            ): Boolean {
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(
                oldItem: Contacts,
                newItem: Contacts
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}