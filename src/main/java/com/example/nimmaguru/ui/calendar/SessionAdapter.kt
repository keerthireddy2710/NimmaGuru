package com.example.nimmaguru.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimmaguru.databinding.ItemSessionCardBinding
import com.example.nimmaguru.model.Session
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionAdapter :
    ListAdapter<Session, SessionAdapter.SessionViewHolder>(DiffCallback()) {

    inner class SessionViewHolder(
        private val binding: ItemSessionCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(session: Session) {

            binding.tvSessionSubject.text =
                "📚 ${session.subject}"

            binding.tvSessionGuru.text =
                "🧑‍🏫 ${session.guruName}"

            binding.tvSessionVenue.text =
                "📍 ${session.venue}, ${session.village}"

            val formattedDate = SimpleDateFormat(
                "EEE, dd MMM yyyy",
                Locale.getDefault()
            ).format(Date(session.dateMillis))

            binding.tvSessionTime.text =
                "🕐 $formattedDate • ${session.timeSlot}"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionViewHolder {

        val binding = ItemSessionCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SessionViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Session>() {

        override fun areItemsTheSame(
            oldItem: Session,
            newItem: Session
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Session,
            newItem: Session
        ): Boolean {
            return oldItem == newItem
        }
    }
}