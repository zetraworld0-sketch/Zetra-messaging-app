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
        val nameInput = findViewById<EditText>(R.id.nameInput)
        
        // Changed R.id.btnFinish to R.id.btnNext to match your XML layout ID
        val btnFinish = findViewById<Button>(R.id.btnNext)

        btnFinish.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            val name = nameInput.text.toString().trim()

            if (phone.length < 10) {
                Toast.makeText(this, "Enter valid phone", Toast.LENGTH_SHORT).show()
            } else if (name.isEmpty()) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registering $name...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
