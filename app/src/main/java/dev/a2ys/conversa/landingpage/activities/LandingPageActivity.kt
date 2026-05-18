package dev.a2ys.conversa.landingpage.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.a2ys.conversa.databinding.ActivityLandingPageBinding
import dev.a2ys.conversa.main.activities.RegisterActivity

class LandingPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The button ID from your XML is 'access_via_id_bridge'
        binding.accessViaIdBridge.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
