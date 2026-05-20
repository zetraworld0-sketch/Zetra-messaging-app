package dev.a2ys.conversa.main.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NameEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Skip name entry and go straight to the Hub
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
