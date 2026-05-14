package com.example.nimmaguru.ui.walloffame

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimmaguru.databinding.ItemThankYouCardBinding
import com.example.nimmaguru.model.ThankYouNote

class ThankYouAdapter :
    ListAdapter<ThankYouNote, ThankYouAdapter.NoteViewHolder>(DiffCallback()) {

    inner class NoteViewHolder(
        private val binding: ItemThankYouCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: ThankYouNote) {

            binding.tvGuruName.text =
                "🧑‍🏫 ${note.guruName}"

            binding.tvMessage.text =
                "\"${note.message}\""

            binding.tvStudentName.text =
                "— ${note.studentName}" +
                        if (note.subject.isNotEmpty())
                            " • ${note.subject}"
                        else
                            ""
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {

        val binding = ItemThankYouCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ThankYouNote>() {

        override fun areItemsTheSame(
            oldItem: ThankYouNote,
            newItem: ThankYouNote
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ThankYouNote,
            newItem: ThankYouNote
        ): Boolean {
            return oldItem == newItem
        }
    }
}