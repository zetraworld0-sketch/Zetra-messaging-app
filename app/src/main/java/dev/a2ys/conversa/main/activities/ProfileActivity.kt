package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dev.a2ys.conversa.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etBio: EditText
    private lateinit var etLocation: EditText
    private lateinit var etOccupation: EditText
    private lateinit var etHobbies: EditText
    private lateinit var etLanguages: EditText
    private lateinit var etInstagram: EditText
    private lateinit var etFacebook: EditText
    private lateinit var etLinkedin: EditText
    private lateinit var etTwitter: EditText
    private lateinit var etWebsite: EditText
    
    private lateinit var spinnerSex: Spinner
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnSaveProfile: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Customise Profile"

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        initViews()
        setupDropdownMenus()
        loadCurrentUserData()

        btnSaveProfile.setOnClickListener {
            saveProfileDataToFirebase()
        }
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etBio = findViewById(R.id.etBio)
        etLocation = findViewById(R.id.etLocation)
        etOccupation = findViewById(R.id.etOccupation)
        etHobbies = findViewById(R.id.etHobbies)
        etLanguages = findViewById(R.id.etLanguages)
        etInstagram = findViewById(R.id.etInstagram)
        etFacebook = findViewById(R.id.etFacebook)
        etLinkedin = findViewById(R.id.etLinkedin)
        etTwitter = findViewById(R.id.etTwitter)
        etWebsite = findViewById(R.id.etWebsite)
        
        spinnerSex = findViewById(R.id.spinnerSex)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupDropdownMenus() {
        val genders = arrayOf("Select Sex", "Male", "Female", "Other", "Prefer not to say")
        spinnerSex.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)

        val statusOptions = arrayOf("Single", "In a relationship", "Married", "It's complicated")
        spinnerStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusOptions)
    }

    private fun loadCurrentUserData() {
        val uid = auth.uid ?: return
        progressBar.visibility = View.VISIBLE

        database.reference.child("registeredUsers").child(uid).child("profile")
            .get().addOnSuccessListener { snapshot ->
                progressBar.visibility = View.GONE
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("name").value?.toString() ?: "")
                    etAge.setText(snapshot.child("age").value?.toString() ?: "")
                    etBio.setText(snapshot.child("bio").value?.toString() ?: "")
                    etLocation.setText(snapshot.child("location").value?.toString() ?: "")
                    etOccupation.setText(snapshot.child("occupation").value?.toString() ?: "")
                    etHobbies.setText(snapshot.child("hobbies").value?.toString() ?: "")
                    etLanguages.setText(snapshot.child("languages").value?.toString() ?: "")
                    etInstagram.setText(snapshot.child("instagramLink").value?.toString() ?: "")
                    etFacebook.setText(snapshot.child("facebookLink").value?.toString() ?: "")
                    etLinkedin.setText(snapshot.child("linkedinLink").value?.toString() ?: "")
                    etTwitter.setText(snapshot.child("twitterLink").value?.toString() ?: "")
                    etWebsite.setText(snapshot.child("website").value?.toString() ?: "")
                }
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
            }
    }

    private fun saveProfileDataToFirebase() {
        val uid = auth.uid
        if (uid == null) {
            Toast.makeText(this, "Authentication token expired.", Toast.LENGTH_LONG).show()
            return
        }

        val name = etName.text.toString().trim()
        val ageStr = etAge.text.toString().trim()
        val sex = spinnerSex.selectedItem.toString()
        val bio = etBio.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val occupation = etOccupation.text.toString().trim()
        val hobbies = etHobbies.text.toString().trim()
        val languages = etLanguages.text.toString().trim()
        
        val instagram = etInstagram.text.toString().trim()
        val facebook = etFacebook.text.toString().trim()
        val linkedin = etLinkedin.text.toString().trim()
        val twitter = etTwitter.text.toString().trim()
        val website = etWebsite.text.toString().trim()
        val status = spinnerStatus.selectedItem.toString()

        if (name.isEmpty()) {
            etName.error = "Name identity parameter required"
            return
        }
        if (ageStr.isEmpty()) {
            etAge.error = "Age metric required"
            return
        }
        if (spinnerSex.selectedItemPosition == 0) {
            Toast.makeText(this, "Please specify your sex profile", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        val profileMap = hashMapOf(
            "name" to name,
            "age" to (ageStr.toIntOrNull() ?: 0),
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
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile deployment synchronized.", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Database pipeline error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
