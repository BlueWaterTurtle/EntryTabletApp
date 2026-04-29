package com.bluewaterturtle.entrytabletapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bluewaterturtle.entrytabletapp.databinding.FragmentSignInBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GuestViewModel by activityViewModels()
    private val displayFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())

    private var selectedCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-populate sign-in time with current date/time
        selectedCalendar = Calendar.getInstance()
        binding.etSignInTime.setText(displayFormat.format(selectedCalendar.time))

        // Open DateTimePicker when the field or end icon is tapped
        binding.etSignInTime.setOnClickListener { showDateTimePicker() }
        binding.layoutSignInTime.setEndIconOnClickListener { showDateTimePicker() }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim() ?: ""
            val personToSee = binding.etPersonToSee.text?.toString()?.trim() ?: ""

            var valid = true
            if (name.isEmpty()) {
                binding.layoutName.error = "Name is required"
                valid = false
            } else {
                binding.layoutName.error = null
            }
            if (personToSee.isEmpty()) {
                binding.layoutPersonToSee.error = "Person to see is required"
                valid = false
            } else {
                binding.layoutPersonToSee.error = null
            }

            if (valid) {
                viewModel.signIn(name, personToSee, selectedCalendar.timeInMillis)
                findNavController().popBackStack()
                Snackbar.make(
                    requireActivity().window.decorView,
                    "✅ $name signed in",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showDateTimePicker() {
        val cal = selectedCalendar
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        cal.set(year, month, day, hour, minute, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        selectedCalendar = cal
                        binding.etSignInTime.setText(displayFormat.format(cal.time))
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    false
                ).show()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
