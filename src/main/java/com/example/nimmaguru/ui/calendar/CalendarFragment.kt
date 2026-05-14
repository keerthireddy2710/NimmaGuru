package com.example.nimmaguru.ui.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.example.nimmaguru.databinding.FragmentCalendarBinding
import com.example.nimmaguru.model.Session
import com.example.nimmaguru.repository.SessionRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionRepo: SessionRepository
    private lateinit var adapter: SessionAdapter
    private var selectedDateMillis = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = NimmaGuruDatabase.getDatabase(requireContext())
        sessionRepo = SessionRepository(db.sessionDao())

        adapter = SessionAdapter()
        binding.rvSessions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSessions.adapter = adapter

        binding.btnAddSession.setOnClickListener { showAddSessionDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            sessionRepo.getUpcomingSessions().collectLatest { list ->
                adapter.submitList(list)
                binding.tvEmptySessions.visibility =
                    if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.rvSessions.visibility =
                    if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun showAddSessionDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val prefs = requireContext().getSharedPreferences("nimmaguru_prefs", 0)
        val savedName = prefs.getString("user_name", "") ?: ""

        val etSubject = EditText(requireContext()).apply { hint = "Subject (e.g. Math)"; textSize = 16f }
        val etGuruName = EditText(requireContext()).apply { hint = "Guru Name"; textSize = 16f; setText(savedName) }
        val etVillage = EditText(requireContext()).apply { hint = "Village"; textSize = 16f }
        val etTimeSlot = EditText(requireContext()).apply { hint = "Time (e.g. 10am–12pm)"; textSize = 16f }
        val etVenue = EditText(requireContext()).apply { hint = "Venue"; textSize = 16f; setText("Samudaya Bhavana") }

        layout.addView(etSubject)
        layout.addView(etGuruName)
        layout.addView(etVillage)
        layout.addView(etTimeSlot)
        layout.addView(etVenue)

        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                cal.set(y, m, d)
                selectedDateMillis = cal.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()

        AlertDialog.Builder(requireContext())
            .setTitle("Add Session 📅")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val subject = etSubject.text.toString().trim()
                if (subject.isEmpty()) {
                    Toast.makeText(context, "Subject is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    sessionRepo.addSession(
                        Session(
                            guruName = etGuruName.text.toString().trim(),
                            subject = subject,
                            village = etVillage.text.toString().trim(),
                            timeSlot = etTimeSlot.text.toString().trim(),
                            venue = etVenue.text.toString().trim().ifEmpty { "Samudaya Bhavana" },
                            dateMillis = selectedDateMillis
                        )
                    )
                    Toast.makeText(context, "Session added ✅", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}