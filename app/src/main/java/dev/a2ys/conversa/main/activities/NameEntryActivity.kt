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

class NameEntryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_name_entry", "layout", packageName))

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        // Key alignment fixed to match the RegisterActivity intent bundle
        phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: ""

        val nameInput = findViewById<EditText>(resources.getIdentifier("nameInput", "id", packageName))
        val btnFinish = findViewById<Button>(resources.getIdentifier("btnNext", "id", packageName))

        btnFinish?.setOnClickListener {
            val name = nameInput?.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name to continue", Toast.LENGTH_SHORT).show()
            } else {
                saveUserDataAndProceed(phoneNumber, name)
            }
        }
    }

    private fun saveUserDataAndProceed(phone: String, name: String) {
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
                "phoneNumber" to phone,
                "basicInfo" to basicInfoMap
            )

            // Streamlining directly into your established database architecture
            database.child("registeredUsers").child(uid).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Welcome to Netscape!", Toast.LENGTH_SHORT).show()
                        
                        // Navigate directly to the main application interface window
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Database entry failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "Session expired. Please restart registration.", Toast.LENGTH_SHORT).show()
        }
    }
}
