package dev.a2ys.conversa.authentication.fragments.phoneNumberVerification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dev.a2ys.conversa.R
import dev.a2ys.conversa.databinding.FragmentOtpVerificationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dev.a2ys.conversa.main.activities.MainActivity
import dev.a2ys.conversa.authentication.activities.ProfileSetupActivity

class OtpVerificationFragment : Fragment() {

    private lateinit var binding: FragmentOtpVerificationBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val zetraId = sharedPref.getString("number", "Unknown Node")

        // Display the Zetra ID / Phone Number we are authorizing
        binding.phn.text = zetraId

        binding.change.setOnClickListener {
            navigateToPhoneNumberFragment()
        }

        binding.submit.setOnClickListener {
            val authKey = binding.otp.editText!!.text.trim().toString()

            if (authKey.length != 6) {
                showError("Invalid Authorization Key!")
            } else {
                if (zetraId != null && zetraId != "Unknown Node") {
                    checkUserStatusAndRoute(zetraId)
                } else {
                    showError("Authentication error: Missing Node Identity.")
                }
            }
        }

        return binding.root
    }

    private fun checkUserStatusAndRoute(phoneNumber: String) {
        // Institutional Check: Look into our Zetra database registry
        db.collection("zetra_users").document(phoneNumber).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists() && documentSnapshot.contains("identity")) {
                    // Existing User detected -> Proceed straight to communication hub
                    navigateToMainActivity()
                } else {
                    // New User detected -> Direct to profile creation matrix
                    navigateToProfileSetupActivity(phoneNumber)
                }
            }
            .addOnFailureListener {
                showError("Database connection timed out. Retrying...")
            }
    }

    private fun showError(message: String) {
        Snackbar.make(requireActivity().findViewById(R.id.container), message, Snackbar.LENGTH_SHORT)
            .setAction("Got it") {}
            .show()
    }

    private fun navigateToPhoneNumberFragment() {
        Navigation.findNavController(requireActivity(), R.id.user_authentication_navigation_fragment)
            .navigateUp()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToProfileSetupActivity(phoneNumber: String) {
        val intent = Intent(requireContext(), ProfileSetupActivity::class.java)
        intent.putExtra("USER_PHONE_KEY", phoneNumber)
        startActivity(intent)
        requireActivity().finish()
    }
}
