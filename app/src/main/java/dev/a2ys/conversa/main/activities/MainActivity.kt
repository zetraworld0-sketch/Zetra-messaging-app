package dev.a2ys.conversa.main.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dev.a2ys.conversa.R
import dev.a2ys.conversa.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up standard toolbar support for the options dropdown menu
        setSupportActionBar(findViewById(R.id.toolbar))

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val uid: String? = auth.uid

        uid?.let {
            database.reference.child("registeredUsers").child(uid).child("onlineStatus").setValue(true)
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_navigation)
        val navController = navHostFragment!!.findNavController()
        binding.bottomNavigation.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Snackbar.make(binding.root, "Settings configuration protocol launched.", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.action_new_group -> {
                Snackbar.make(binding.root, "Group initialization layer active.", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.action_starred -> {
                Snackbar.make(binding.root, "Loading secure bookmarked assets.", Snackbar.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        val uid: String? = auth.uid

        uid?.let {
            database.reference.child("registeredUsers").child(uid).child("onlineStatus").setValue(true)
        }

        super.onResume()
    }

    override fun onStop() {
        val uid: String? = auth.uid

        uid?.let {
            database.reference.child("registeredUsers").child(uid).child("onlineStatus").setValue(false)
        }

        super.onStop()
    }
}
