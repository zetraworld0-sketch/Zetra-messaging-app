package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

        // Safety Check: If the button is null, the XML ID is wrong
        if (btnNext == null) {
            Toast.makeText(this, "ERROR: btnNext ID not found in XML!", Toast.LENGTH_LONG).show()
            return
        }

        btnNext.setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            } else {
                saveData(name)
            }
        }
    }

    private fun saveData(name: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "DEBUG_TEST_ID"
        val database = FirebaseDatabase.getInstance().reference

        val userMap = mapOf(
            "username" to name,
            "status" to "Active"
        )

        database.child("registeredUsers").child(uid).setValue(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Success! Entering...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                // Clear the stack so user can't go back to this entry screen
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "DB Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
