package com.bluewaterturtle.entrytabletapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluewaterturtle.entrytabletapp.databinding.FragmentLogBinding
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
