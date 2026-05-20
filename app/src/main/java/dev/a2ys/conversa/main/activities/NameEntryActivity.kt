package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        try {
            setContentView(R.layout.activity_name_entry)
            val nameInput = findViewById<EditText>(R.id.nameInput)
            val btnNext = findViewById<Button>(R.id.btnNext)

            btnNext.setOnClickListener {
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
                } else {
                    saveData(name)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "CRASH: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("DEBUG_ERROR", "Setup failed", e)
        }
    }

    private fun saveData(name: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Error: No User ID", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseDatabase.getInstance().reference.child("registeredUsers").child(uid).child("username")
            .setValue(name)
            .addOnSuccessListener {
                Toast.makeText(this, "Saved! Starting Main...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "DB ERROR: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("DEBUG_ERROR", "DB Failed", e)
            }
    }
}
