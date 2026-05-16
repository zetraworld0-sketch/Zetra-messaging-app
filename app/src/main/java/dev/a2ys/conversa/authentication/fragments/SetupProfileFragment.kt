package dev.a2ys.conversa.authentication.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import dev.a2ys.conversa.R
import dev.a2ys.conversa.databinding.FragmentSetupProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SetupProfileFragment : Fragment() {

    private var _binding: FragmentSetupProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.profileImage.setPadding(0, 0, 0, 0)
            binding.profileImage.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()

            if (name.isEmpty()) {
                showError("Please enter your name to proceed.")
                return@setOnClickListener
            }

            setLoadingState(true)
            saveUserAccountData(name)
        }

        return binding.root
    }

    private fun saveUserAccountData(username: String) {
        val uid = auth.currentUser?.uid ?: return
        
        val userMap = hashMapOf(
            "uid" to uid,
            "name" to username,
            "email" to (auth.currentUser?.email ?: ""),
            "profileImageUrl" to (selectedImageUri?.toString() ?: ""),
            "status" to "Available"
        )

        // Save cleanly to your cloud infrastructure database
        db.collection("Users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                setLoadingState(false)
                launchMainDashboard()
            }
            .addOnFailureListener { e ->
                setLoadingState(false)
                showError("Database Error: ${e.message}")
            }
    }

    private fun launchMainDashboard() {
        val intent = Intent(requireActivity(), Class.forName("dev.a2ys.conversa.main.activities.MainActivity"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSaveProfile.visibility = View.GONE
            binding.profileProgress.visibility = View.VISIBLE
        } else {
            binding.profileProgress.visibility = View.GONE
            binding.btnSaveProfile.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
