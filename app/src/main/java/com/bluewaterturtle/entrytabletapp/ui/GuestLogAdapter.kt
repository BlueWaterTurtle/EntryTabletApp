package com.bluewaterturtle.entrytabletapp.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bluewaterturtle.entrytabletapp.data.GuestEntity
import com.bluewaterturtle.entrytabletapp.databinding.ItemGuestLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GuestLogAdapter : ListAdapter<GuestEntity, GuestLogAdapter.GuestViewHolder>(DiffCallback) {

    private val displayFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val binding = ItemGuestLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class GuestViewHolder(private val binding: ItemGuestLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(guest: GuestEntity, position: Int) {
            binding.tvName.text = guest.name
            binding.tvPersonToSee.text = guest.personToSee
            binding.tvSignInTime.text = displayFormat.format(Date(guest.signInTime))
            binding.tvSignOutTime.text = guest.signOutTime?.let { displayFormat.format(Date(it)) } ?: "—"

            binding.root.setBackgroundColor(if (position % 2 == 0) ROW_COLOR_EVEN else ROW_COLOR_ODD)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GuestEntity>() {
        private val ROW_COLOR_EVEN = Color.parseColor("#1AFFFFFF")
        private val ROW_COLOR_ODD = Color.parseColor("#33FFFFFF")

        override fun areItemsTheSame(oldItem: GuestEntity, newItem: GuestEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GuestEntity, newItem: GuestEntity) =
            oldItem == newItem
    }
}
