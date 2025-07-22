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

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
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

        // 🔁 NEW: Back icon top-right
        findViewById<ImageButton>(R.id.btnBackIcon).setOnClickListener {
            finish() // or use startActivity(Intent(this, LoginScreen::class.java))
        }
    }

    private fun setupFieldValidation() {
        // First name and last name ≥ 2 characters
        validateOnChange(binding.etFirstName) { it.length >= 2 } to "Minimum 2 characters"
        validateOnChange(binding.etLastName) { it.length >= 2 } to "Minimum 2 characters"

        // Username ≥ 8 characters
        validateOnChange(binding.etUsername) { it.length >= 8 } to "Minimum 8 characters"

        // Email format
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                binding.etEmail.error = when {
                    email.isEmpty() -> null
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
                    !email.contains("@") -> "Email must contain '@'"
                    !email.contains(".com") -> "Email must end with '.com'"
                    else -> null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Password constraints
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pwd = s.toString()
                binding.etPassword.error = when {
                    pwd.isEmpty() -> null
                    pwd.length < 8 -> "At least 8 characters"
                    !pwd.any { it.isDigit() } -> "Must contain a number"
                    !pwd.any { it.isUpperCase() } -> "Must contain a capital letter"
                    !pwd.any { "!@#$%^&*()_+-=".contains(it) } -> "Must contain a symbol"
                    else -> null
                }
                validatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Confirm password match
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = validatePasswordMatch()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateOnChange(view: EditText, validator: (String) -> Boolean): Pair<EditText, (String) -> Boolean> {
        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                view.error = if (input.isNotEmpty() && !validator(input)) "Invalid input" else null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        return view to validator
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
