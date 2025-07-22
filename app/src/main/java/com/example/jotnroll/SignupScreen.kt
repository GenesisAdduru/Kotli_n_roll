// 📄 SignupScreen.kt
package com.example.jotnroll

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jotnroll.databinding.ActivitySignupScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySignupScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupFieldValidation()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (firstName.length < 2 || lastName.length < 2 || username.length < 8 ||
                !isValidEmail(email) || !isValidPassword(password) || password != confirmPassword
            ) {
                Toast.makeText(this, "Please fix the errors above", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                    val userMap = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "username" to username,
                        "email" to email
                    )
                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginScreen::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        findViewById<ImageButton>(R.id.btnBackIcon).setOnClickListener {
            finish()
        }
    }

    private fun setupFieldValidation() {
        validateOnChange(binding.etFirstName, { it.length >= 2 }, "First name must be at least 2 characters")
        validateOnChange(binding.etLastName, { it.length >= 2 }, "Last name must be at least 2 characters")
        validateOnChange(binding.etUsername, { it.length >= 8 }, "Username must be at least 8 characters")

        // Email format
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                binding.etEmail.error = if (email.isNotEmpty() && !isValidEmail(email)) {
                    "Email must contain '@' and end with '.com'"
                } else null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Password validation
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pwd = s.toString()
                binding.etPassword.error = if (pwd.isNotEmpty() && !isValidPassword(pwd)) {
                    "Must be ≥8 chars, include 1 number, 1 uppercase, 1 symbol"
                } else null
                validatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Confirm password
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = validatePasswordMatch()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".com") && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isDigit() } &&
                password.any { it.isUpperCase() } &&
                password.any { "!@#\$%^&*()_+-=".contains(it) }
    }

    private fun validateOnChange(view: EditText, validator: (String) -> Boolean, errorMessage: String) {
        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                view.error = if (input.isNotEmpty() && !validator(input)) errorMessage else null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validatePasswordMatch() {
        val pass = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()
        binding.etConfirmPassword.error = when {
            confirm.isEmpty() -> null
            pass != confirm -> "Passwords do not match"
            else -> null
        }
    }
}
