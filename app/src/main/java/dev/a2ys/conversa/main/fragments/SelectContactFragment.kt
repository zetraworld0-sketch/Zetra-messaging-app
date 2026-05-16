package dev.a2ys.conversa.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.a2ys.conversa.databinding.FragmentSelectContactBinding
import com.google.android.material.snackbar.Snackbar

class SelectContactFragment : Fragment() {

    private var _binding: FragmentSelectContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectContactBinding.inflate(inflater, container, false)

        binding.btnNewGroup.setOnClickListener {
            Snackbar.make(binding.root, "Group creation system initiated.", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnNewContact.setOnClickListener {
            Snackbar.make(binding.root, "Contact synchronization system opened.", Snackbar.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
