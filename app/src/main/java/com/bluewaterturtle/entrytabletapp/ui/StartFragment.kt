package com.bluewaterturtle.entrytabletapp.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bluewaterturtle.entrytabletapp.R
import com.bluewaterturtle.entrytabletapp.databinding.FragmentStartBinding
import com.google.android.material.snackbar.Snackbar

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_start_to_signIn)
        }

        binding.btnSignOut.setOnClickListener {
            findNavController().navigate(R.id.action_start_to_signOut)
        }

        binding.btnAdmin.setOnClickListener {
            val passwordInput = EditText(requireContext()).apply {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = "Password"
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Admin Access")
                .setView(passwordInput)
                .setPositiveButton("Enter") { _, _ ->
                    if (passwordInput.text.toString() == "admin") {
                        findNavController().navigate(R.id.action_start_to_log)
                    } else {
                        Snackbar.make(binding.root, "Incorrect password", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
