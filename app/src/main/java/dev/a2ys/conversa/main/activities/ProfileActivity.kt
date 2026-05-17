package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.a2ys.conversa.R
import dev.a2ys.conversa.databinding.ActivityProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.profile_toolbar))
        supportActionBar?.title = "Customise Profile"

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        setupDropdownMenus()
        loadCurrentUserData()

        binding.btnSaveProfile.setOnClickListener {
            saveProfileDataToFirebase()
        }
    }

    private fun setupDropdownMenus() {
        val genders = arrayOf("Select Sex", "Male", "Female", "Other", "Prefer not to say")
        binding.spinnerSex.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)

        val statusOptions = arrayOf("Single", "In a relationship", "Married", "It's complicated")
        binding.spinnerStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusOptions)
    }

    private fun loadCurrentUserData() {
        val uid = auth.uid ?: return
        binding.progressBar.visibility = View.VISIBLE

        database.reference.child("registeredUsers").child(uid).child("profile")
            .get().addOnSuccessListener { snapshot ->
                binding.progressBar.visibility = View.GONE
                if (snapshot.exists()) {
                    binding.etName.setText(snapshot.child("name").value.toString())
                    binding.etAge.setText(snapshot.child("age").value.toString())
                    binding.etBio.setText(snapshot.child("bio").value.toString())
                    binding.etLocation.setText(snapshot.child("location").value.toString())
                    binding.etOccupation.setText(snapshot.child("occupation").value.toString())
                    binding.etHobbies.setText(snapshot.child("hobbies").value.toString())
                    binding.etLanguages.setText(snapshot.child("languages").value.toString())
                    
                    // Social Integrations
                    binding.etInstagram.setText(snapshot.child("instagramLink").value.toString())
                    binding.etFacebook.setText(snapshot.child("facebookLink").value.toString())
                    binding.etLinkedin.setText(snapshot.child("linkedinLink").value.toString())
                    binding.etTwitter.setText(snapshot.child("twitterLink").value.toString())
                    binding.etWebsite.setText(snapshot.child("website").value.toString())
                }
            }.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun saveProfileDataToFirebase() {
        val uid = auth.uid
        if (uid == null) {
            Snackbar.make(binding.root, "Authentication token expired.", Snackbar.LENGTH_LONG).show()
            return
        }

        val name = binding.etName.text.toString().trim()
        val ageStr = binding.etAge.text.toString().trim()
        val sex = binding.spinnerSex.selectedItem.toString()
        val bio = binding.etBio.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val occupation = binding.etOccupation.text.toString().trim()
        val hobbies = binding.etHobbies.text.toString().trim()
        val languages = binding.etLanguages.text.toString().trim()
        
        // Social Link Vectors
        val instagram = binding.etInstagram.text.toString().trim()
        val facebook = binding.etFacebook.text.toString().trim()
        val linkedin = binding.etLinkedin.text.toString().trim()
        val twitter = binding.etTwitter.text.toString().trim()
        val website = binding.etWebsite.text.toString().trim()
        val status = binding.spinnerStatus.selectedItem.toString()

        if (name.isEmpty()) {
            binding.etName.error = "Name identity parameter required"
            return
        }
        if (ageStr.isEmpty()) {
            binding.etAge.error = "Age metric required"
            return
        }
        if (binding.spinnerSex.selectedItemPosition == 0) {
            Toast.makeText(this, "Please specify your sex profile", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        val profileMap = hashMapOf(
            "name" to name,
            "age" to ageStr.toIntValueOrZero(),
            "sex" to sex,
            "bio" to bio,
            "location" to location,
            "occupation" to occupation,
            "hobbies" to hobbies,
            "languages" to languages,
            "instagramLink" to instagram,
            "facebookLink" to facebook,
            "linkedinLink" to linkedin,
            "twitterLink" to twitter,
            "website" to website,
            "relationshipStatus" to status,
            "profileSetupCompleted" to true
        )

        database.reference.child("registeredUsers").child(uid).child("profile")
            .setValue(profileMap)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile deployment matrix fully synchronized.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Snackbar.make(binding.root, "Database pipeline error: ${exception.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun String.toIntValueOrZero(): Int {
        return try {
            this.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}
