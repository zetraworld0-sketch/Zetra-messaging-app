package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dev.a2ys.conversa.R
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private var selectedCountryCode = ""
    private val countryCodeMap = HashMap<String, String>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val countryView = findViewById<AutoCompleteTextView>(R.id.countryAutoComplete)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput) // Ensure you have this in XML
        val passwordInput = findViewById<EditText>(R.id.passwordInput) // Ensure you have this in XML
        val btnNext = findViewById<Button>(R.id.btnNext)

        initializeGlobalCountryCodes()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countryCodeMap.keys.toTypedArray().sortedArray())
        countryView.setAdapter(adapter)

        countryView.setOnItemClickListener { parent, _, position, _ ->
            selectedCountryCode = countryCodeMap[parent.getItemAtPosition(position).toString()] ?: ""
        }

        btnNext.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()
            
            if (selectedCountryCode.isEmpty() || email.isEmpty() || pass.length < 6) {
                Toast.makeText(this, "Complete all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Create the user
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 2. ONLY move to NameEntryActivity AFTER success
                    val intent = Intent(this, NameEntryActivity::class.java)
                    intent.putExtra("PHONE", "+$selectedCountryCode${phoneInput.text}")
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Auth Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initializeGlobalCountryCodes() {
        // Keeping your existing map logic...
        countryCodeMap["Nigeria (+234)"] = "234"
        countryCodeMap["United Arab Emirates (+971)"] = "971"
        // ... (add your other mappings here)
    }
}
