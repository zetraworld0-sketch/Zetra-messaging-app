package dev.a2ys.conversa.landingpage.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.a2ys.conversa.databinding.ActivityLandingPageBinding

class LandingPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.accessViaIdBridge.setOnClickListener {
            // Explicitly targeting the full class path to bypass Manifest package resolution blocks
            val intent = Intent()
            intent.setClassName(this, "dev.a2ys.conversa.main.activities.RegisterActivity")
            startActivity(intent)
        }
    }
}
