package com.bluewaterturtle.entrytabletapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bluewaterturtle.entrytabletapp.R
import com.bluewaterturtle.entrytabletapp.data.PersonToSeeEntity
import com.bluewaterturtle.entrytabletapp.databinding.FragmentSignInBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GuestViewModel by activityViewModels()
    private val displayFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())

    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var activePeople: List<PersonToSeeEntity> = emptyList()
    private lateinit var personToSeeAdapter: ArrayAdapter<String>

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

        personToSeeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        binding.etPersonToSee.setAdapter(personToSeeAdapter)
        binding.etPersonToSee.setOnClickListener { binding.etPersonToSee.showDropDown() }
        binding.etPersonToSee.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etPersonToSee.showDropDown()
            }
        }
        binding.etPersonToSee.setOnItemClickListener { _, _, _, _ ->
            updateCustomPersonVisibility()
            binding.layoutPersonToSee.error = null
        }
        binding.etPersonToSee.doAfterTextChanged {
            updateCustomPersonVisibility()
            binding.layoutPersonToSee.error = null
        }
        updateCustomPersonVisibility()

        viewModel.activePeopleToSee.observe(viewLifecycleOwner) { people ->
            activePeople = people
            updatePersonToSeeOptions()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim() ?: ""
            val personToSeeSelection = binding.etPersonToSee.text?.toString() ?: ""
            val customPersonToSee = binding.etCustomPersonToSee.text?.toString() ?: ""

            var valid = true
            if (name.isEmpty()) {
                binding.layoutName.error = getString(R.string.name_required)
                valid = false
            } else {
                binding.layoutName.error = null
            }

            val personToSeeResult = PersonToSeeSelectionValidator.validate(
                selectedOption = personToSeeSelection,
                customName = customPersonToSee,
                validNames = activePeople.map { it.displayName }.toSet(),
                otherLabel = getString(R.string.other_person_option)
            )

            val finalPersonToSee = when (personToSeeResult) {
                is PersonToSeeSelectionResult.Success -> {
                    binding.layoutPersonToSee.error = null
                    binding.layoutCustomPersonToSee.error = null
                    personToSeeResult.personToSee
                }

                PersonToSeeSelectionResult.MissingSelection -> {
                    binding.layoutPersonToSee.error = getString(R.string.person_to_see_required)
                    binding.layoutCustomPersonToSee.error = null
                    valid = false
                    ""
                }

                PersonToSeeSelectionResult.InvalidSelection -> {
                    binding.layoutPersonToSee.error = getString(R.string.select_person_to_see_from_list)
                    binding.layoutCustomPersonToSee.error = null
                    valid = false
                    ""
                }

                PersonToSeeSelectionResult.MissingCustomName -> {
                    binding.layoutPersonToSee.error = null
                    binding.layoutCustomPersonToSee.error = getString(R.string.custom_person_required)
                    valid = false
                    ""
                }
            }

            if (valid) {
                viewModel.signIn(name, finalPersonToSee, selectedCalendar.timeInMillis)
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

    private fun updatePersonToSeeOptions() {
        val options = activePeople.map { it.displayName } + getString(R.string.other_person_option)
        personToSeeAdapter.clear()
        personToSeeAdapter.addAll(options)
        personToSeeAdapter.notifyDataSetChanged()

        val currentSelection = binding.etPersonToSee.text?.toString()?.trim().orEmpty()
        if (currentSelection.isNotEmpty() && currentSelection !in options) {
            binding.etPersonToSee.setText("", false)
        }
        updateCustomPersonVisibility()
    }

    private fun updateCustomPersonVisibility() {
        val showCustomField =
            binding.etPersonToSee.text?.toString()?.trim() == getString(R.string.other_person_option)
        binding.layoutCustomPersonToSee.isGone = !showCustomField
        if (!showCustomField) {
            binding.etCustomPersonToSee.text?.clear()
            binding.layoutCustomPersonToSee.error = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
