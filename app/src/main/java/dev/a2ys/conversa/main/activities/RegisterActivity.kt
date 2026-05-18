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

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_register", "layout", packageName))

        // Initialize Firebase instances
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        // Dynamic resource matching matching your core activity design pattern
        val phoneInput = findViewById<EditText>(resources.getIdentifier("phoneInput", "id", packageName))
        val nameInput = findViewById<EditText>(resources.getIdentifier("nameInput", "id", packageName))
        val btnFinish = findViewById<Button>(resources.getIdentifier("btnNext", "id", packageName))

        btnFinish?.setOnClickListener {
            val phone = phoneInput?.text.toString().trim()
            val name = nameInput?.text.toString().trim()

            if (phone.length < 10) {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            } else if (name.isEmpty()) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
            } else {
                saveUserData(phone, name)
            }
        } ?: run {
            // Safe fallback if the button component identifier fails execution rules
            Toast.makeText(this, "Interface initialization error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData(phone: String, name: String) {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val basicInfoMap = hashMapOf(
                "name" to name,
                "dateOfBirth" to "",
                "gender" to ""
            )

            val userMap = hashMapOf(
                "userId" to uid,
                "username" to name,
                "basicInfo" to basicInfoMap
            )

            database.child("registeredUsers").child(uid).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show()
                        
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Database error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "Authentication error. Please restart the app.", Toast.LENGTH_SHORT).show()
        }
    }
}
