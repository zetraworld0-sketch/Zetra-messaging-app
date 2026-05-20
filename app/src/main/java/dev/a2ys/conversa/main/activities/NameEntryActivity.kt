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

        btnNext.setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the current user directly from the Auth instance
            val user = FirebaseAuth.getInstance().currentUser
            
            if (user == null) {
                // If this happens, your RegisterActivity is NOT waiting for the task to finish
                Toast.makeText(this, "Session Error: User not found. Please re-register.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val uid = user.uid
            val database = FirebaseDatabase.getInstance().reference

            database.child("registeredUsers").child(uid).child("username").setValue(name)
                .addOnSuccessListener {
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
