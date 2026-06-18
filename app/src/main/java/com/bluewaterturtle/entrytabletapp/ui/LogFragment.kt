package com.bluewaterturtle.entrytabletapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluewaterturtle.entrytabletapp.R
import com.bluewaterturtle.entrytabletapp.data.AddPersonToSeeResult
import com.bluewaterturtle.entrytabletapp.data.PersonToSeeEntity
import com.bluewaterturtle.entrytabletapp.databinding.FragmentLogBinding
import com.bluewaterturtle.entrytabletapp.databinding.ItemPersonToSeeBinding
import com.bluewaterturtle.entrytabletapp.util.CsvExporter
import com.google.android.material.snackbar.Snackbar

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GuestViewModel by activityViewModels()
    private lateinit var adapter: GuestLogAdapter
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GuestLogAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.activePeopleToSee.observe(viewLifecycleOwner) { people ->
            renderPeopleToSee(people)
        }

        viewModel.allGuests.observe(viewLifecycleOwner) { guests ->
            adapter.submitList(guests)
            if (guests.isEmpty()) {
                binding.tvNoRecords.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvNoRecords.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        binding.btnAddPerson.setOnClickListener {
            binding.layoutAddPerson.error = null
            viewModel.addPersonToSee(binding.etAddPerson.text?.toString().orEmpty()) { result ->
                when (result) {
                    AddPersonToSeeResult.ADDED -> {
                        binding.etAddPerson.text?.clear()
                        Snackbar.make(binding.root, getString(R.string.person_added), Snackbar.LENGTH_SHORT).show()
                    }

                    AddPersonToSeeResult.REACTIVATED -> {
                        binding.etAddPerson.text?.clear()
                        Snackbar.make(binding.root, getString(R.string.person_reactivated), Snackbar.LENGTH_SHORT).show()
                    }

                    AddPersonToSeeResult.EMPTY_NAME -> {
                        binding.layoutAddPerson.error = getString(R.string.person_name_required)
                    }

                    AddPersonToSeeResult.DUPLICATE_ACTIVE -> {
                        binding.layoutAddPerson.error = getString(R.string.person_duplicate)
                    }
                }
            }
        }

        binding.btnExportCsv.setOnClickListener {
            val guests = viewModel.allGuests.value ?: emptyList()
            if (guests.isEmpty()) {
                Snackbar.make(binding.root, "No records to export.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CsvExporter.exportAndShare(requireContext(), guests)
        }

        binding.btnClearAll.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Clear")
                .setMessage("Are you sure you want to clear all guest records? This cannot be undone.")
                .setPositiveButton("Clear All") { _, _ ->
                    viewModel.clearAll()
                    Snackbar.make(binding.root, "All records cleared.", Snackbar.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun renderPeopleToSee(people: List<PersonToSeeEntity>) {
        binding.personListContainer.removeAllViews()
        binding.tvNoPeopleConfigured.isVisible = people.isEmpty()
        people.forEach { person ->
            val itemBinding = ItemPersonToSeeBinding.inflate(layoutInflater, binding.personListContainer, false)
            itemBinding.tvPersonName.text = person.displayName
            itemBinding.btnDeactivatePerson.setOnClickListener {
                viewModel.deactivatePersonToSee(person.id)
                Snackbar.make(binding.root, getString(R.string.person_removed), Snackbar.LENGTH_SHORT).show()
            }
            binding.personListContainer.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
