package dev.a2ys.conversa.authentication.fragments.phoneNumberVerification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dev.a2ys.conversa.R
import dev.a2ys.conversa.databinding.FragmentPhoneNumberBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class PhoneNumberFragment : Fragment() {

    private var _binding: FragmentPhoneNumberBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.submit.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.length < 6) {
                showError("Provide a valid email and a 6+ digit security key.")
                return@setOnClickListener
            }

            binding.submit.visibility = View.GONE
            binding.progressCircular.visibility = View.VISIBLE

            processIdentitySign(email, password)
        }

        return binding.root
    }

    private fun processIdentitySign(email: String, javaPass: String) {
        auth.signInWithEmailAndPassword(email, javaPass)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    finalizeAuthenticationSuccess()
                } else {
                    auth.createUserWithEmailAndPassword(email, javaPass)
                        .addOnCompleteListener(requireActivity()) { createEnv ->
                            if (createEnv.isSuccessful) {
                                finalizeAuthenticationSuccess()
                            } else {
                                resetUiState()
                                showError("Authentication Network Error: ${createEnv.exception?.message}")
                            }
                        }
                }
            }
    }

    private fun finalizeAuthenticationSuccess() {
        resetUiState()
        Navigation.findNavController(requireActivity(), R.id.user_authentication_navigation_fragment)
            .navigate(R.id.action_phoneNumberFragment_to_otpVerificationFragment)
    }

    private fun resetUiState() {
        binding.progressCircular.visibility = View.GONE
        binding.submit.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(requireActivity().findViewById(R.id.container), message, Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {}
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
