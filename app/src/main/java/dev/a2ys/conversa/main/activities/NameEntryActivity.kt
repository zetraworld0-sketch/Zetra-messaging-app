package dev.a2ys.conversa.main.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.LinearLayout
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity

class NameEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a layout programmatically (bypasses XML files entirely)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER

        val btn = Button(this)
        btn.text = "TEST BUTTON - CLICK ME"
        layout.addView(btn)

        setContentView(layout)

        // Test if the code actually runs
        btn.setOnClickListener {
            Toast.makeText(this, "CODE IS WORKING", Toast.LENGTH_LONG).show()
        }
    }
}
