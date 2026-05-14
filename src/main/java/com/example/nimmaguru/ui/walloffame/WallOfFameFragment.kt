package com.example.nimmaguru.ui.walloffame

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimmaguru.data.NimmaGuruDatabase
import com.example.nimmaguru.databinding.FragmentWallOfFameBinding
import com.example.nimmaguru.model.ThankYouNote
import com.example.nimmaguru.repository.ThankYouRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WallOfFameFragment : Fragment() {

    private var _binding: FragmentWallOfFameBinding? = null
    private val binding get() = _binding!!
    private lateinit var thankYouRepo: ThankYouRepository
    private lateinit var adapter: ThankYouAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWallOfFameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = NimmaGuruDatabase.getDatabase(requireContext())
        thankYouRepo = ThankYouRepository(db.thankYouDao())

        adapter = ThankYouAdapter()
        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.adapter = adapter

        binding.fabAddNote.setOnClickListener { showAddNoteDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            thankYouRepo.getAllNotes().collectLatest { list ->
                adapter.submitList(list)
                binding.tvEmptyNotes.visibility =
                    if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.rvNotes.visibility =
                    if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun showAddNoteDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val etGuru = EditText(requireContext()).apply { hint = "Guru's name"; textSize = 16f }
        val etSubject = EditText(requireContext()).apply { hint = "Subject (e.g. Math)"; textSize = 16f }
        val etStudent = EditText(requireContext()).apply { hint = "Your name"; textSize = 16f }
        val etMsg = EditText(requireContext()).apply {
            hint = "Your thank you message…"
            textSize = 16f
            minLines = 3
        }

        layout.addView(etGuru)
        layout.addView(etSubject)
        layout.addView(etStudent)
        layout.addView(etMsg)

        AlertDialog.Builder(requireContext())
            .setTitle("Thank Your Guru 🙏")
            .setView(layout)
            .setPositiveButton("Post") { _, _ ->
                val guruName = etGuru.text.toString().trim()
                val message = etMsg.text.toString().trim()
                if (guruName.isEmpty() || message.isEmpty()) {
                    Toast.makeText(context, "Guru name and message are required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    thankYouRepo.postNote(
                        ThankYouNote(
                            guruName = guruName,
                            subject = etSubject.text.toString().trim(),
                            studentName = etStudent.text.toString().trim(),
                            message = message
                        )
                    )
                    Toast.makeText(context, "Thank you posted! 🎉", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}