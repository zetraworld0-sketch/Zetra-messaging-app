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
            var rawInput = binding.phoneNumber.editText?.text?.toString()?.trim() ?: ""

            if (rawInput.isEmpty()) {
                showError("Please enter a valid phone number!")
            } else {
                // Institutional Phone Protocol Formatter (E.164 compliance)
                val cleanNumber = formatToE164(rawInput)
                
                // Initialize the structural verification stream
                startPhoneAuthentication(cleanNumber)
            }
        }

        return binding.root
    }

    private fun formatToE164(input: String): String {
        // Strip out any accidental spaces or dashes
        var normalized = input.replace("\\s+".toRegex(), "").replace("-", "")

        // If the user already provided the country code with +, use it directly
        if (normalized.startsWith("+")) {
            return normalized
        }

        // Handle standard regional formatting context (e.g., converting 080... to +23480...)
        if (normalized.startsWith("0")) {
            normalized = normalized.substring(1)
        }

        // Inject your country code protocol prefix (+234 for Nigeria)
        // Change "234" below if your primary staging environment is in another region
        return "+234$normalized"
    }

    private fun startPhoneAuthentication(phoneNumber: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                // Handle instant verification contexts gracefully if needed
            }

            override fun onVerificationFailed(e: FirebaseException) {
                showError("Network Handshake Rejected: ${e.localizedMessage}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Protocol key transmitted successfully. Save states and proceed to security gate.
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("number", phoneNumber)
                    putString("verification_id", verificationId)
                    apply()
                }
                navigateToOtpVerificationFragment()
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
            
        PhoneAuthProvider.verifyPhoneNumber(options)
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
