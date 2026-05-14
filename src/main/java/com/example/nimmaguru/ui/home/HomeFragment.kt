package com.example.nimmaguru.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimmaguru.R
import com.example.nimmaguru.data.NimmaGuruDatabase
import com.example.nimmaguru.databinding.FragmentHomeBinding
import com.example.nimmaguru.repository.GuruRepository
import com.example.nimmaguru.repository.SessionRepository
import com.example.nimmaguru.ui.search.GuruAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GuruAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("nimmaguru_prefs", 0)
        val name = prefs.getString("user_name", "Friend") ?: "Friend"
        binding.tvUserName.text = name

        val db = NimmaGuruDatabase.getDatabase(requireContext())
        val guruRepo = GuruRepository(db.guruDao())
        val sessionRepo = SessionRepository(db.sessionDao())

        adapter = GuruAdapter {}
        binding.rvRecentGurus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentGurus.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            guruRepo.getAllGurus().collectLatest { list ->
                adapter.submitList(list.take(5))
                binding.tvGuruCount.text = list.size.toString()
                binding.tvNoGurus.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.rvRecentGurus.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sessionRepo.getUpcomingSessionCount().collectLatest {
                binding.tvSessionCount.text = it.toString()
            }
        }

        binding.cardEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_profile)
        }

        binding.cardFindGuru.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }

        binding.cardSignOut.setOnClickListener {
            requireContext().getSharedPreferences("nimmaguru_prefs", 0)
                .edit().clear().apply()
            findNavController().navigate(R.id.action_home_to_login)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}