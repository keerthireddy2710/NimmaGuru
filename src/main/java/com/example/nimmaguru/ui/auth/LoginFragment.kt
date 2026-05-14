package com.example.nimmaguru.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.example.nimmaguru.R
import com.example.nimmaguru.data.NimmaGuruDatabase
import com.example.nimmaguru.databinding.FragmentLoginBinding
import com.example.nimmaguru.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepo: AuthRepository
    private var isLoginMode = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = NimmaGuruDatabase.getDatabase(requireContext())
        authRepo = AuthRepository(db.userDao())

        // If already logged in skip to home
        val prefs = requireContext().getSharedPreferences("nimmaguru_prefs", 0)
        if (prefs.getBoolean("is_logged_in", false)) {
            findNavController().navigate(R.id.action_login_to_home)
            return
        }

        setupTabs()

        binding.btnSubmit.setOnClickListener {
            if (isLoginMode) loginUser() else registerUser()
        }

        binding.tvForgot.setOnClickListener {
            Toast.makeText(context, "Please contact admin to reset password.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Login"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Register"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isLoginMode = tab.position == 0
                if (isLoginMode) {
                    binding.tilName.visibility = View.GONE
                    binding.layoutRole.visibility = View.GONE
                    binding.btnSubmit.text = "Login"
                    binding.tvForgot.visibility = View.VISIBLE
                } else {
                    binding.tilName.visibility = View.VISIBLE
                    binding.layoutRole.visibility = View.VISIBLE
                    binding.btnSubmit.text = "Create Account"
                    binding.tvForgot.visibility = View.GONE
                }
                clearErrors()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim().lowercase()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) { binding.tilEmail.error = "Enter your email"; return }
        if (password.length < 6) { binding.tilPassword.error = "Minimum 6 characters"; return }

        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            val result = authRepo.login(email, password)
            showLoading(false)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                saveSession(user.email, user.name, user.role)
                findNavController().navigate(R.id.action_login_to_home)
            } else {
                showError(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim().lowercase()
        val name = binding.etName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val role = if (binding.rbGuru.isChecked) "guru" else "student"

        if (email.isEmpty()) { binding.tilEmail.error = "Enter your email"; return }
        if (name.isEmpty()) { binding.tilName.error = "Enter your name"; return }
        if (password.length < 6) { binding.tilPassword.error = "Minimum 6 characters"; return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"; return
        }

        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            val result = authRepo.register(email, name, password, role)
            showLoading(false)
            if (result.isSuccess) {
                saveSession(email, name, role)
                Toast.makeText(context, "Welcome, $name! 🎓", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_login_to_home)
            } else {
                showError(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    private fun saveSession(email: String, name: String, role: String) {
        requireContext().getSharedPreferences("nimmaguru_prefs", 0)
            .edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", email)
            .putString("user_name", name)
            .putString("user_role", role)
            .apply()
    }

    private fun showLoading(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !show
    }

    private fun showError(msg: String) {
        binding.tvError.text = msg
        binding.tvError.visibility = View.VISIBLE
    }

    private fun clearErrors() {
        binding.tvError.visibility = View.GONE
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilName.error = null
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}