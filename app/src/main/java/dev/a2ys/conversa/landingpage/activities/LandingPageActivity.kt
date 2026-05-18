package dev.a2ys.conversa.landingpage.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.a2ys.conversa.databinding.ActivityLandingPageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dev.a2ys.conversa.authentication.activities.InfoActivity
import dev.a2ys.conversa.main.activities.MainActivity
import dev.a2ys.conversa.main.activities.RegisterActivity // Import your new activity

class LandingPageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        // We REMOVED checkUserAuthentication() from here.
        // Now the app will show your landing page layout (with the buttons)
        // instead of jumping to the email screen immediately.

        // Set up the button click listener
        binding.btnAccessViaPhone.setOnClickListener { // Make sure this ID matches your XML
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
