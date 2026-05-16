package dev.a2ys.conversa.authentication.fragments.phoneNumberVerification

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dev.a2ys.conversa.R
import dev.a2ys.conversa.databinding.FragmentPhoneNumberBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneNumberFragment : Fragment() {

    private lateinit var binding: FragmentPhoneNumberBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.submit.setOnClickListener {
            val rawInput = binding.phoneNumber.editText?.text?.toString()?.trim() ?: ""

            if (rawInput.isEmpty() || rawInput.length < 7) {
                showError("Please enter a complete international number (e.g. +234...)")
            } else {
                val formattedNumber = formatFlexibleInternational(rawInput)
                
                // Show loader, hide button to prevent double-clicks
                binding.submit.visibility = View.GONE
                binding.progressCircular.visibility = View.VISIBLE
                
                // Dispatch live network request to Firebase
                startPhoneAuthentication(formattedNumber)
            }
        }

        return binding.root
    }

    private fun formatFlexibleInternational(input: String): String {
        var normalized = input.replace("\\s+".toRegex(), "").replace("-", "")

        if (normalized.startsWith("+")) {
            return normalized
        }

        return "+$normalized"
    }

    private fun startPhoneAuthentication(phoneNumber: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                binding.progressCircular.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressCircular.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
                showError("SMS Routing Failed: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.progressCircular.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE

                // Save parameters using unified "phone_number" key matching OtpVerificationFragment
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("phone_number", phoneNumber)
                    putString("verification_id", verificationId)
                    apply()
                }
                
                // Now that the network has confirmed shipment, transition screens safely
                navigateToOtpVerificationFragment()
            }
        }

        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS) // Standard production window allowance
                .setActivity(requireActivity())
                .setCallbacks(callbacks)
                .build()
                
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            binding.progressCircular.visibility = View.GONE
            binding.submit.visibility = View.VISIBLE
            showError("Initialization Error: ${e.message}")
        }
    }

    private fun showError(message: String) {
        Snackbar.make(requireActivity().findViewById(R.id.container), message, Snackbar.LENGTH_SHORT)
            .setAction("Got it") {}
            .show()
    }

    private fun navigateToOtpVerificationFragment() {
        Navigation.findNavController(requireActivity(), R.id.user_authentication_navigation_fragment)
            .navigate(R.id.action_phoneNumberFragment_to_otpVerificationFragment)
    }
}
