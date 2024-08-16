package com.example.android3homework9mc5.ui.fragments.singup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android3homework9mc5.R
import com.example.android3homework9mc5.databinding.FragmentSingInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SingInFragment : Fragment() {

    private var _binding: FragmentSingInBinding? = null
    private val binding: FragmentSingInBinding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingInBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.title.text.toString().trim()

            if (validateInputs(email, password)) {
                signInUser(email, password)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.etEmail.error = "Требуется адрес электронный почты"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Не верный формат элуктронный почты"
                false
            }
            password.isEmpty() -> {
                binding.title.error = "Требуется пароль"
                false
            }
            password.length < 6 -> {
                binding.title.error = "Пароль должен быть не менее 6 символов"
                false
            }
            else -> true
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(context, "Ошибка входа в систему: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            findNavController().navigate(R.id.homeFragment)
            Toast.makeText(context, "С возращением, ${user.displayName ?: "User"}!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Не удолось войти в систему", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
