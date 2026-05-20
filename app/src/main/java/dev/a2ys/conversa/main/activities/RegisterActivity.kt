package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import dev.a2ys.conversa.R
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private var selectedCountryCode = ""
    private val countryCodeMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val countryView = findViewById<AutoCompleteTextView>(R.id.countryAutoComplete)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val btnNext = findViewById<Button>(R.id.btnNext)

        initializeGlobalCountryCodes()

        val countriesArray = countryCodeMap.keys.toTypedArray().sortedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countriesArray)
        countryView?.setAdapter(adapter)

        countryView?.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            selectedCountryCode = countryCodeMap[selectedItem] ?: ""
            Toast.makeText(this, "Code: +$selectedCountryCode", Toast.LENGTH_SHORT).show()
        }

        btnNext?.setOnClickListener {
            val localNumber = phoneInput?.text.toString().trim()

            if (selectedCountryCode.isEmpty()) {
                Toast.makeText(this, "Select your country", Toast.LENGTH_SHORT).show()
            } else if (localNumber.isEmpty() || localNumber.length < 7) {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            } else {
                val fullGlobalPhoneNumber = "+$selectedCountryCode$localNumber"
                
                // Move to Name Entry
                val intent = Intent(this, NameEntryActivity::class.java).apply {
                    putExtra("PHONE_NUMBER", fullGlobalPhoneNumber)
                }
                startActivity(intent)
            }
        }
    }

    private fun initializeGlobalCountryCodes() {
        // Essential mappings
        countryCodeMap["Nigeria (+234)"] = "234"
        countryCodeMap["Kuwait (+965)"] = "965"
        countryCodeMap["United Arab Emirates (+971)"] = "971"
        countryCodeMap["China (+86)"] = "86"
        countryCodeMap["Poland (+48)"] = "48"
        // Add other countries as needed
    }
}
