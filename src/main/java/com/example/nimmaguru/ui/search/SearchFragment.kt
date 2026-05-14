package com.example.nimmaguru.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.example.nimmaguru.data.NimmaGuruDatabase
import com.example.nimmaguru.databinding.FragmentSearchBinding
import com.example.nimmaguru.model.Guru
import com.example.nimmaguru.repository.GuruRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GuruAdapter
    private lateinit var guruRepo: GuruRepository
    private var currentFlow: Flow<List<Guru>>? = null

    private val skills = listOf(
        "Math", "Science", "Kannada", "English",
        "Carpentry", "Computers", "Music", "Art"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = NimmaGuruDatabase.getDatabase(requireContext())
        guruRepo = GuruRepository(db.guruDao())

        adapter = GuruAdapter {}
        binding.rvGurus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGurus.adapter = adapter

        setupChips()
        setupSearch()
        observeGurus(guruRepo.getAllGurus())
    }

    private fun setupChips() {
        skills.forEach { skill ->
            val chip = Chip(requireContext()).apply {
                text = skill
                isCheckable = true
                textSize = 14f
                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        observeGurus(guruRepo.getGurusBySkill(skill))
                    } else {
                        observeGurus(guruRepo.getAllGurus())
                    }
                }
            }
            binding.chipGroupSkills.addView(chip)
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(q: String?) = false
                override fun onQueryTextChange(q: String?): Boolean {
                    val query = q?.trim() ?: ""
                    observeGurus(
                        if (query.isEmpty()) guruRepo.getAllGurus()
                        else guruRepo.searchGurus(query)
                    )
                    return true
                }
            }
        )
    }

    private fun observeGurus(flow: Flow<List<Guru>>) {
        viewLifecycleOwner.lifecycleScope.launch {
            flow.collectLatest { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.rvGurus.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}