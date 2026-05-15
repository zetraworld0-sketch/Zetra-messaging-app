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
import dev.a2ys.conversa.databinding.FragmentPhoneNumberBinding
import com.google.android.material.snackbar.Snackbar
import dev.a2ys.conversa.main.activities.MainActivity

class PhoneNumberFragment : Fragment() {

    private lateinit var binding: FragmentPhoneNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberBinding.inflate(layoutInflater, container, false)

        binding.submit.setOnClickListener {
            val zetraId = binding.phoneNumber.editText!!.text.trim().toString()

            // Institutional Grade Validation: Check for Zetra ID format
            if (zetraId.isEmpty()) {
                showError("Please enter a valid Zetra ID!")
            } else {
                // Save the ID and move to the Security Gate
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("number", zetraId)
                    apply()
                }
                navigateToOtpVerificationFragment()
            }
        }

        return binding.root
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
