package com.example.nimmaguru.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.chip.Chip
import com.example.nimmaguru.data.NimmaGuruDatabase
import com.example.nimmaguru.databinding.FragmentGuruProfileBinding
import com.example.nimmaguru.model.Guru
import com.example.nimmaguru.repository.GuruRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class GuruProfileFragment : Fragment() {

    private var _binding: FragmentGuruProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var guruRepo: GuruRepository
    private var selectedPhotoPath = ""
    private var existingGuruId = 0

    private val skills = listOf(
        "Math", "Science", "Kannada", "English",
        "Carpentry", "Computers", "Music", "Art"
    )

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val path = saveImageLocally(it)
            if (path.isNotEmpty()) {
                selectedPhotoPath = path
                binding.imgProfile.load(File(path)) {
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuruProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = NimmaGuruDatabase.getDatabase(requireContext())
        guruRepo = GuruRepository(db.guruDao())

        setupSkillChips()
        loadExistingProfile()

        binding.imgProfile.setOnClickListener { pickImage.launch("image/*") }

        binding.btnGenerateBio.setOnClickListener {
            val selectedSkillsList = getSelectedSkills()
            val name = binding.etName.text.toString().trim()
            if (name.isEmpty() || selectedSkillsList.isEmpty()) {
                Toast.makeText(context, "Enter name and select skills first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bio = "I am $name, a retired professional from ${binding.etVillage.text}. " +
                    "I voluntarily teach ${selectedSkillsList.joinToString(", ")} to local students " +
                    "during my free hours at the community center. " +
                    "I believe in Gyaan-Daan — sharing knowledge for the betterment of our community."
            binding.etBio.setText(bio)
            Toast.makeText(context, "Bio filled! Feel free to edit it. ✅", Toast.LENGTH_SHORT).show()
        }

        binding.btnSave.setOnClickListener { saveProfile() }
    }

    private fun setupSkillChips() {
        skills.forEach { skill ->
            val chip = Chip(requireContext()).apply {
                text = skill
                isCheckable = true
                textSize = 14f
            }
            binding.chipGroupSkills.addView(chip)
        }
    }

    private fun loadExistingProfile() {
        val prefs = requireContext().getSharedPreferences("nimmaguru_prefs", 0)
        val email = prefs.getString("user_email", "") ?: ""
        val savedName = prefs.getString("user_name", "") ?: ""

        if (binding.etName.text.isNullOrEmpty()) {
            binding.etName.setText(savedName)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val guru = guruRepo.getGuruByEmail(email)
            guru?.let {
                existingGuruId = it.id
                binding.etName.setText(it.name)
                binding.etVillage.setText(it.village)
                binding.etFreeHours.setText(it.freeHours)
                binding.etBio.setText(it.bio)
                selectedPhotoPath = it.photoPath

                if (it.photoPath.isNotEmpty()) {
                    val file = File(it.photoPath)
                    if (file.exists()) {
                        binding.imgProfile.load(file) {
                            transformations(CircleCropTransformation())
                        }
                    }
                }

                val savedSkills = it.skills.split(",").map { s -> s.trim() }
                for (i in 0 until binding.chipGroupSkills.childCount) {
                    val chip = binding.chipGroupSkills.getChildAt(i) as? Chip
                    chip?.isChecked = savedSkills.contains(chip?.text.toString())
                }
            }
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val village = binding.etVillage.text.toString().trim()
        val freeHours = binding.etFreeHours.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()
        val skillsList = getSelectedSkills()

        if (name.isEmpty() || village.isEmpty()) {
            Toast.makeText(context, "Name and village are required", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences("nimmaguru_prefs", 0)
        val email = prefs.getString("user_email", "") ?: ""

        binding.progress.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            guruRepo.saveGuru(
                Guru(
                    id = existingGuruId,
                    userEmail = email,
                    name = name,
                    village = village,
                    freeHours = freeHours,
                    bio = bio,
                    skills = skillsList.joinToString(","),
                    photoPath = selectedPhotoPath
                )
            )
            binding.progress.visibility = View.GONE
            binding.btnSave.isEnabled = true
            Toast.makeText(context, "Profile saved! ✅", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedSkills(): List<String> {
        val selected = mutableListOf<String>()
        for (i in 0 until binding.chipGroupSkills.childCount) {
            val chip = binding.chipGroupSkills.getChildAt(i) as? Chip
            if (chip?.isChecked == true) selected.add(chip.text.toString())
        }
        return selected
    }

    private fun saveImageLocally(uri: Uri): String {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return ""
            val file = File(requireContext().filesDir, "profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            file.absolutePath
        } catch (e: Exception) {
            ""
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}