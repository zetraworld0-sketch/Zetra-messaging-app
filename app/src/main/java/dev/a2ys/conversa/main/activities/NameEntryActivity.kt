package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dev.a2ys.conversa.R

class NameEntryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var phoneNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_entry)

        // Initialize Firebase
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        // Capture phone number from previous activity
        phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: ""

        // Find views directly by ID
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val btnNext = findViewById<Button>(R.id.btnNext)

        // Set click listener
        btnNext.setOnClickListener {
            val name = nameInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                saveUserDataAndProceed(name)
            }
        }
    }

    private fun saveUserDataAndProceed(name: String) {
        val uid = auth.currentUser?.uid ?: "TEST_USER_DEBUG_NODE"

        val userMap = hashMapOf(
            "userId" to uid,
            "username" to name,
            "phoneNumber" to phoneNumber,
            "basicInfo" to hashMapOf(
                "name" to name,
                "dateOfBirth" to "",
                "gender" to ""
            )
        )

        database.child("registeredUsers").child(uid).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show()
                    
                    // Proceed to Main Dashboard
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
