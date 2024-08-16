package com.example.android3homework9mc5.ui.fragments.singup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android3homework9mc5.R
import com.example.android3homework9mc5.databinding.FragmentSingUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SingUpToFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSingUpBinding? = null
    private val binding: FragmentSingUpBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setUpListener()
        setUpListe()
    }

    private fun setUpListener() {
        binding.tvForgotPassword.setOnClickListener{
            findNavController().navigate(R.id.homeFragment)
        }
    }


    private fun setUpListe() {
        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString().trim()
            val lastName = binding.lastNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInputs(firstName, lastName, email, password)) {
                showLoading(true)
                registerUser(firstName, lastName, email, password)
            }
        }
    }

    private fun validateInputs(firstName: String, lastName: String, email: String, password: String): Boolean {
        var isValid = true

        if (firstName.isEmpty()) {
            binding.firstNameEditText.error = "Необходима указать имя"
            isValid = false
        } else {
            binding.firstNameEditText.error = null
        }

        if (lastName.isEmpty()) {
            binding.lastNameEditText.error = "Необходима фамилия"
            isValid = false
        } else {
            binding.lastNameEditText.error = null
        }

        if (email.isEmpty()) {
            binding.emailEditText.error = "Требуется адрес электронный почты"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Неверный адрес электронной почты "
            isValid = false
        } else {
            binding.emailEditText.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditText.error = "Требуется пароль"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordEditText.error = "Пароль должен содержать не менее 6 символов"
            isValid = false
        } else {
            binding.passwordEditText.error = null
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        updateUserProfile(it, firstName, lastName)
                    }
                } else {
                    showLoading(false)
                    Log.e("Register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Регистрация не удалось: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUserProfile(user: FirebaseUser, firstName: String, lastName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$firstName $lastName")
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else {
                    Log.e("Register", "updateProfile:failure", task.exception)
                    Toast.makeText(context, "Ошбка обновления профиля: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            findNavController().navigate(R.id.action_singUpFragment2_to_homeFragment)

            // Создаем и отображаем Snackbar
            Snackbar.make(
                binding.root, // View для Snackbar
                "Регитрация прошла успешна! Добро пожаловать, ${user.displayName}",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

