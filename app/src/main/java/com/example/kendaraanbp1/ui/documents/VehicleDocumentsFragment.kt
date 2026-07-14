package com.example.kendaraanbp1.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kendaraanbp1.R
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.ui.documents.DocumentViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.data.model.DocumentItem
import com.example.kendaraanbp1.data.model.DocumentStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kendaraanbp1.databinding.FragmentVehicleDocumentsBinding
import com.example.kendaraanbp1.ui.util.VerticalSpaceItemDecoration
import com.example.kendaraanbp1.ui.util.bind
import com.example.kendaraanbp1.ui.util.setEmptyState
import com.example.kendaraanbp1.ui.util.setupNavigation
import com.example.kendaraanbp1.ui.adddocument.AddDocumentFragment

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.findNavController

class VehicleDocumentsFragment : Fragment() {

    private var _binding: FragmentVehicleDocumentsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val documentViewModel: DocumentViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVehicleDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.searchButton.visibility = View.GONE // Hidden for MVP release
        binding.addDocumentFab.setOnClickListener {
            AddDocumentFragment().show(childFragmentManager, AddDocumentFragment.TAG)
        }

        val adapter = DocumentAdapter(
            onEditClick = { item ->
                val doc = documentViewModel.documents.value.data?.find { it.id == item.id }
                if (doc != null) {
                    val issued = doc.issuedDate ?: System.currentTimeMillis()
                    val daysValid = if (doc.expiryDate != null) {
                        ((doc.expiryDate - issued) / (1000L * 60 * 60 * 24)).toInt()
                    } else 0
                    val fragment = AddDocumentFragment.newInstance(
                        id = doc.id,
                        type = doc.documentType,
                        number = doc.documentNumber ?: "",
                        validDays = daysValid,
                        issuedDate = issued
                    )
                    fragment.show(childFragmentManager, AddDocumentFragment.TAG)
                }
            },
            onDeleteClick = { item ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hapus Dokumen")
                    .setMessage("Apakah Anda yakin ingin menghapus dokumen ini? Pengingat dokumen terkait juga akan dibatalkan.")
                    .setPositiveButton("Hapus") { _, _ ->
                        val doc = documentViewModel.documents.value.data?.find { it.id == item.id }
                        if (doc != null) {
                            documentViewModel.deleteDocument(
                                doc = doc,
                                onSuccess = {
                                    Snackbar.make(binding.root, "Dokumen berhasil dihapus", Snackbar.LENGTH_SHORT).show()
                                },
                                onError = { msg ->
                                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )
        binding.documentList.layoutManager = LinearLayoutManager(requireContext())
        binding.documentList.adapter = adapter
        binding.documentList.addItemDecoration(
            VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_12))
        )
        
        setUpEmptyState()
        observeViewModels(adapter)
    }

    private fun setUpEmptyState() {
        binding.documentEmptyState.bind(
            R.drawable.ic_inbox,
            R.string.empty_document_title,
            R.string.empty_document_subtitle,
        )
    }
    
    private fun observeViewModels(adapter: DocumentAdapter) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.selectedVehicleId.collect { id ->
                        documentViewModel.setVehicleId(id)
                    }
                }
                
                launch {
                    documentViewModel.documents.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val docs = resource.data ?: emptyList()
                                val currentTime = System.currentTimeMillis()
                                val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
                                
                                val items = docs.map { doc ->
                                    val status: DocumentStatus
                                    val statusText: String
                                    val isExpiringSoon = doc.expiryDate != null && (doc.expiryDate - currentTime) <= thirtyDaysMs
                                    val isExpired = doc.expiryDate != null && (doc.expiryDate < currentTime)
                                    
                                    if (isExpired) {
                                        status = DocumentStatus.URGENT
                                        statusText = "Kadaluarsa"
                                    } else if (isExpiringSoon) {
                                        status = DocumentStatus.EXPIRING
                                        statusText = "Segera Habis"
                                    } else {
                                        status = DocumentStatus.ACTIVE
                                        statusText = "Aktif"
                                    }
                                    
                                    DocumentItem(
                                        id = doc.id,
                                        title = doc.documentType,
                                        subtitle = doc.expiryDate?.let { "Berlaku s/d: ${dateFormat.format(Date(it))}" } ?: "Tidak ada masa berlaku",
                                        statusLabel = statusText,
                                        status = status,
                                        iconRes = R.drawable.ic_file_text
                                    )
                                }
                                adapter.submitList(items)
                                binding.documentEmptyState.setEmptyState(items.isEmpty(), binding.documentList)
                            }
                            is Resource.Error -> {
                                binding.documentEmptyState.setEmptyState(true, binding.documentList)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
