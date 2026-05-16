package dev.a2ys.conversa.authentication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import dev.a2ys.conversa.databinding.ActivityInfoBinding
import dev.a2ys.conversa.main.activities.MainActivity

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Linking up precisely with your existing activity_info.xml structure
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPhone = intent.getStringExtra("USER_PHONE_KEY") ?: ""

        // Safe dynamic binding for your registration inputs
        binding.root.findViewById<android.view.View>(dev.a2ys.conversa.R.id.submit)?.setOnClickListener {
            val alias = binding.username.editText?.text?.toString()?.trim() ?: ""
            val name = binding.name.editText?.text?.toString()?.trim() ?: ""
            val gender = binding.gender.editText?.text?.toString()?.trim() ?: ""

            if (alias.isEmpty() || name.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "All network identification profiles must be populated.", Toast.LENGTH_SHORT).show()
            } else {
                val userProfile = hashMapOf(
                    "identity" to userPhone,
                    "alias" to alias,
                    "name" to name,
                    "gender" to gender,
                    "created_at" to System.currentTimeMillis()
                )

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
