package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.a2ys.conversa.R

class NameEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_entry)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                Toast.makeText(this, "Error: No session found. Please re-register.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user profile to Firebase Realtime Database
            FirebaseDatabase.getInstance().reference
                .child("registeredUsers")
                .child(user.uid)
                .setValue(mapOf("username" to name, "status" to "Active"))
                .addOnSuccessListener {
                    Toast.makeText(this, "Welcome to the Hub!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "DB Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
