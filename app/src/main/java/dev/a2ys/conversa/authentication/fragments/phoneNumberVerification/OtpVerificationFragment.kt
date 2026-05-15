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
import dev.a2ys.conversa.main.activities.MainActivity

class OtpVerificationFragment : Fragment() {

    private lateinit var binding: FragmentOtpVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val zetraId = sharedPref.getString("number", "Unknown Node")

        // Display the Zetra ID we are authorizing
        binding.phn.text = zetraId

        binding.change.setOnClickListener {
            navigateToPhoneNumberFragment()
        }

        binding.submit.setOnClickListener {
            val authKey = binding.otp.editText!!.text.trim().toString()

            // Institutional bypass for development
            if (authKey.length != 6) {
                showError("Invalid Authorization Key!")
            } else {
                // Grant Access to the Sovereign Hub
                navigateToMainActivity()
            }
        }

        return binding.root
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
}
