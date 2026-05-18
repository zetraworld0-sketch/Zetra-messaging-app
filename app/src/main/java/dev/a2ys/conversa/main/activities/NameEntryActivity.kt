package dev.a2ys.conversa.main.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.a2ys.conversa.R

class NameEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_entry)

        // Get the phone number from the previous screen
        val phoneNumber = intent.getStringExtra("user_phone")

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val btnFinish = findViewById<Button>(R.id.btnNext)

        btnFinish.setOnClickListener {
            val name = nameInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                // Here you would normally save the name and phone number to Firebase
                Toast.makeText(this, "Profile created for: $name", Toast.LENGTH_SHORT).show()
                
                // Add your logic to move to MainActivity here
            }
        }
    }
}
