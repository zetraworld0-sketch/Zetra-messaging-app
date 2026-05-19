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

        // Initialize Firebase Components
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        // Capture phone bundle variable passed from RegisterActivity
        phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: ""

        // Resolve View Nodes using native resource identifier mappings
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

    /**
     * Complete pipeline mapping structural user objects straight into the 
     * Firebase Realtime Database schema architecture. Includes a fallback channel
     * to ensure safe system execution during direct layout testing configurations.
     */
    private fun saveUserDataAndProceed(phone: String, name: String) {
        // Resolve active authentication token node, or switch safely to debug testing node
        val uid = auth.currentUser?.uid ?: "TEST_USER_DEBUG_NODE"

        // Map core profile components
        val basicInfoMap = hashMapOf(
            "name" to name,
            "dateOfBirth" to "",
            "gender" to ""
        )

        // Structure master user schema mapping layout
        val userMap = hashMapOf(
            "userId" to uid,
            "username" to name,
            "phoneNumber" to phone,
            "basicInfo" to basicInfoMap
        )

        // Commit structure transactions straight into database instance tree nodes
        database.child("registeredUsers").child(uid).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Welcome to Netscape!", Toast.LENGTH_SHORT).show()
                    
                    // Break current stack frame layout and launch main navigation dashboard
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Database entry failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
