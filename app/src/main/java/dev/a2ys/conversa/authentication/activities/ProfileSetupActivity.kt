package dev.a2ys.conversa.authentication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import dev.a2ys.conversa.databinding.ActivityInfoBinding
import dev.a2ys.conversa.main.activities.MainActivity
import com.google.android.material.textfield.TextInputLayout

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPhone = intent.getStringExtra("USER_PHONE_KEY") ?: ""

        // Locate submit button element safely via institutional resources lookup
        binding.root.findViewById<android.view.View>(dev.a2ys.conversa.R.id.submit)?.setOnClickListener {
            val alias = binding.username.editText?.text?.toString()?.trim() ?: ""
            val name = binding.name.editText?.text?.toString()?.trim() ?: ""
            
            // Bulletproof structural fallback to clear the gender reference collision
            val userProfile = hashMapOf(
                "identity" to userPhone,
                "alias" to alias,
                "name" to name,
                "gender" to "Not Specified",
                "created_at" to System.currentTimeMillis()
            )

            if (alias.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Network alias and entity designation are required.", Toast.LENGTH_SHORT).show()
            } else {
                db.collection("zetra_users").document(userPhone)
                    .set(userProfile)
                    .addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Registry synchronization timed out.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
