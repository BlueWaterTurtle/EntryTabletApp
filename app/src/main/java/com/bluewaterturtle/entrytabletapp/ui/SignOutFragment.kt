package com.bluewaterturtle.entrytabletapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bluewaterturtle.entrytabletapp.data.GuestEntity
import com.bluewaterturtle.entrytabletapp.databinding.FragmentSignOutBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignOutFragment : Fragment() {

    private var _binding: FragmentSignOutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GuestViewModel by activityViewModels()
    private val displayFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())

    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var signedInGuests: List<GuestEntity> = emptyList()
    private var selectedGuestId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-populate sign-out time
        selectedCalendar = Calendar.getInstance()
        binding.etSignOutTime.setText(displayFormat.format(selectedCalendar.time))

        binding.etSignOutTime.setOnClickListener { showDateTimePicker() }
        binding.layoutSignOutTime.setEndIconOnClickListener { showDateTimePicker() }

        // Observe signed-in guests to populate dropdown
        viewModel.signedInGuests.observe(viewLifecycleOwner) { guests ->
            signedInGuests = guests
            if (guests.isEmpty()) {
                binding.layoutGuestSpinner.visibility = View.GONE
                binding.tvNoGuests.visibility = View.VISIBLE
                binding.btnConfirm.isEnabled = false
            } else {
                binding.layoutGuestSpinner.visibility = View.VISIBLE
                binding.tvNoGuests.visibility = View.GONE
                binding.btnConfirm.isEnabled = true

                val names = guests.map { it.name }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    names
                )
                binding.spinnerGuests.setAdapter(adapter)
                binding.spinnerGuests.setOnItemClickListener { _, _, position, _ ->
                    selectedGuestId = guests[position].id
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnConfirm.setOnClickListener {
            val guestId = selectedGuestId
            if (guestId == null) {
                binding.layoutGuestSpinner.error = "Please select a guest"
                return@setOnClickListener
            }
            binding.layoutGuestSpinner.error = null

            val guestName = signedInGuests.find { it.id == guestId }?.name ?: ""
            viewModel.signOut(guestId, selectedCalendar.timeInMillis)
            findNavController().popBackStack()
            Snackbar.make(
                requireActivity().window.decorView,
                "✅ $guestName signed out",
                Snackbar.LENGTH_SHORT
            ).show()
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
                        binding.etSignOutTime.setText(displayFormat.format(cal.time))
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
