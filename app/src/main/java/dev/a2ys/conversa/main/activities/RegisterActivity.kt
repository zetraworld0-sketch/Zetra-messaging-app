package dev.a2ys.conversa.main.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.a2ys.conversa.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {
            val phoneNumber = phoneInput.text.toString().trim()

            if (phoneNumber.length < 10) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            } else {
                // We are keeping it simple for now so it builds!
                Toast.makeText(this, "Number saved: $phoneNumber", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
