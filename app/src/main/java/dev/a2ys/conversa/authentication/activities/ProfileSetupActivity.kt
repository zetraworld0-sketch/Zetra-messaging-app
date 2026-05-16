package dev.a2ys.conversa.authentication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import dev.a2ys.conversa.databinding.ActivityProfileSetupBinding
import dev.a2ys.conversa.main.activities.MainActivity

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPhone = intent.getStringExtra("USER_PHONE_KEY") ?: ""

        binding.submitProfileButton.setOnClickListener {
            val alias = binding.usernameInput.text.toString().trim()
            val name = binding.nameInput.text.toString().trim()
            val age = binding.ageInput.text.toString().trim()
            val role = binding.roleInput.text.toString().trim()

            if (alias.isEmpty() || name.isEmpty() || age.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "All identification data is required.", Toast.LENGTH_SHORT).show()
            } else {
                val userProfile = hashMapOf(
                    "identity" to userPhone,
                    "alias" to alias,
                    "name" to name,
                    "age" to age,
                    "role" to role,
                    "created_at" to System.currentTimeMillis()
                )

                db.collection("zetra_users").document(userPhone)
                    .set(userProfile)
                    .addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Registry synchronization failed.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
