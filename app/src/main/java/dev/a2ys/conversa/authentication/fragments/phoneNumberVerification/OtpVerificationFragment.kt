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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dev.a2ys.conversa.main.activities.MainActivity
import dev.a2ys.conversa.authentication.activities.ProfileSetupActivity

class OtpVerificationFragment : Fragment() {

    private lateinit var binding: FragmentOtpVerificationBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val zetraId = sharedPref.getString("number", "Unknown Node")
        // Retrieve the operational verification ID passed from the Phone Input Fragment
        val verificationId = sharedPref.getString("verification_id", "") ?: ""

        binding.phn.text = zetraId

        binding.change.setOnClickListener {
            navigateToPhoneNumberFragment()
        }

        binding.submit.setOnClickListener {
            val authKey = binding.otp.editText?.text?.toString()?.trim() ?: ""

            if (authKey.length != 6) {
                showError("Invalid Authorization Key! Must be 6 digits.")
            } else {
                if (verificationId.isNotEmpty()) {
                    // Authenticate the protocol credentials with Firebase first
                    val credential = PhoneAuthProvider.getCredential(verificationId, authKey)
                    signInWithPhoneAuthCredential(credential, zetraId ?: "Unknown Node")
                } else {
                    showError("Authentication error: Session token invalid.")
                }
            }
        }

        return binding.root
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, phoneNumber: String) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                // Secure tunnel established. Proceed safely to database lookup.
                checkUserStatusAndRoute(phoneNumber)
            }
            .addOnFailureListener {
                showError("Invalid authentication key payload verification failed.")
            }
    }

    private fun checkUserStatusAndRoute(phoneNumber: String) {
        db.collection("zetra_users").document(phoneNumber).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists() && documentSnapshot.contains("identity")) {
                    navigateToMainActivity()
                } else {
                    navigateToProfileSetupActivity(phoneNumber)
                }
            }
            .addOnFailureListener {
                showError("Registry synchronization timed out. Checking local state...")
                // Fallback option to protect onboarding pipeline if firestore sync delays
                navigateToProfileSetupActivity(phoneNumber)
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
