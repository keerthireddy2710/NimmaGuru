package com.example.nimmaguru.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.nimmaguru.databinding.ItemGuruCardBinding
import com.example.nimmaguru.model.Guru
import java.io.File

class GuruAdapter(
    private val onItemClick: (Guru) -> Unit
) : ListAdapter<Guru, GuruAdapter.GuruViewHolder>(DiffCallback()) {

    inner class GuruViewHolder(
        private val binding: ItemGuruCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(guru: Guru) {

            binding.tvGuruName.text = guru.name

            binding.tvGuruVillage.text =
                "📍 ${guru.village}"

            binding.tvGuruSkills.text =
                guru.skills.replace(",", " • ")

            binding.tvFreeHours.text =
                if (guru.freeHours.isNotEmpty())
                    "🕐 ${guru.freeHours}"
                else
                    ""

            binding.tvThankCount.text =
                if (guru.thankYouCount > 0)
                    "🙏 ${guru.thankYouCount}"
                else
                    ""

            if (guru.photoPath.isNotEmpty()) {

                val file = File(guru.photoPath)

                if (file.exists()) {

                    binding.imgGuru.load(file) {
                        transformations(
                            CircleCropTransformation()
                        )
                        crossfade(true)
                    }
                }
            }

            binding.root.setOnClickListener {
                onItemClick(guru)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GuruViewHolder {

        val binding = ItemGuruCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GuruViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: GuruViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Guru>() {

        override fun areItemsTheSame(
            oldItem: Guru,
            newItem: Guru
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Guru,
            newItem: Guru
        ): Boolean {
            return oldItem == newItem
        }
    }
}