package com.example.kendaraanbp1.ui.servicehistory

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
import com.example.kendaraanbp1.ui.servicehistory.ServiceHistoryViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.data.model.ServiceTimelineItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kendaraanbp1.util.Formatters.toRupiah
import com.google.android.material.snackbar.Snackbar
import com.example.kendaraanbp1.databinding.FragmentServiceHistoryBinding
import com.example.kendaraanbp1.ui.util.BottomNavTab
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarMargin
import com.example.kendaraanbp1.ui.util.applyTopSystemBarPadding
import com.example.kendaraanbp1.ui.util.bind
import com.example.kendaraanbp1.ui.util.setActiveTab
import com.example.kendaraanbp1.ui.util.setEmptyState
import com.example.kendaraanbp1.ui.util.setupNavigation
import com.example.kendaraanbp1.ui.addservice.AddServiceEntryFragment

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.navigation.fragment.findNavController

class ServiceHistoryFragment : Fragment() {

    private var _binding: FragmentServiceHistoryBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val serviceViewModel: ServiceHistoryViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentServiceHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.header.applyTopSystemBarPadding()
        binding.bottomNavInclude.root.applyBottomSystemBarMargin()
        binding.bottomNavInclude.setActiveTab(BottomNavTab.VEHICLES)
        binding.bottomNavInclude.setupNavigation(findNavController())

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.searchButton.visibility = View.GONE // Hidden for MVP release
        binding.addServiceFab.setOnClickListener {
            AddServiceEntryFragment().show(childFragmentManager, AddServiceEntryFragment.TAG)
        }

        val adapter = ServiceTimelineAdapter(
            onEditClick = { item ->
                val log = serviceViewModel.serviceLogs.value.data?.find { it.id == item.id }
                if (log != null) {
                    val fragment = AddServiceEntryFragment.newInstance(
                        id = log.id,
                        date = log.date,
                        category = log.category,
                        workshopName = log.workshopName,
                        odometer = log.odometer,
                        totalCost = log.totalCost,
                        nextServiceDate = log.nextServiceDate,
                        nextServiceOdometer = log.nextServiceOdometer
                    )
                    fragment.show(childFragmentManager, AddServiceEntryFragment.TAG)
                }
            },
            onDeleteClick = { item ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hapus Riwayat Servis")
                    .setMessage("Apakah Anda yakin ingin menghapus catatan servis ini? Pengingat servis terkait juga akan dibatalkan.")
                    .setPositiveButton("Hapus") { _, _ ->
                        val log = serviceViewModel.serviceLogs.value.data?.find { it.id == item.id }
                        if (log != null) {
                            serviceViewModel.deleteLog(
                                log = log,
                                onSuccess = {
                                    Snackbar.make(binding.root, "Catatan servis berhasil dihapus", Snackbar.LENGTH_SHORT).show()
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
        binding.serviceTimelineList.layoutManager = LinearLayoutManager(requireContext())
        binding.serviceTimelineList.adapter = adapter
        
        setUpEmptyState()
        observeViewModels(adapter)
    }

    private fun setUpEmptyState() {
        binding.serviceEmptyState.bind(
            R.drawable.ic_inbox,
            R.string.empty_service_title,
            R.string.empty_service_subtitle,
        )
    }
    
    private fun observeViewModels(adapter: ServiceTimelineAdapter) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.selectedVehicleId.collect { id ->
                        serviceViewModel.setVehicleId(id)
                    }
                }
                
                launch {
                    serviceViewModel.upcomingService.collect { svc ->
                        if (svc != null) {
                            binding.serviceNextTitle.text = svc.category
                            binding.serviceNextDate.text =
                                svc.nextServiceDate?.let { dateFormat.format(Date(it)) }
                                    ?: getString(R.string.service_next_none_date)
                            binding.serviceRemainingValue.text =
                                svc.nextServiceOdometer?.let {
                                    com.example.kendaraanbp1.util.VehicleStats.formatThousands(it)
                                } ?: "–"
                        } else {
                            binding.serviceNextTitle.text = getString(R.string.service_next_none_title)
                            binding.serviceNextDate.text = getString(R.string.service_next_none_date)
                            binding.serviceRemainingValue.text = "–"
                        }
                    }
                }

                launch {
                    serviceViewModel.serviceLogs.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                binding.loadingIndicator.visibility = View.GONE
                                val logs = resource.data ?: emptyList()
                                val items = logs.map { log ->
                                    ServiceTimelineItem(
                                        id = log.id,
                                        title = log.category,
                                        workshopName = log.workshopName,
                                        statusLabel = getString(R.string.service_status_done),
                                        dateDistanceLabel = "${dateFormat.format(Date(log.date))} • ${log.odometer} km",
                                        totalCostLabel = log.totalCost.toRupiah(),
                                        iconRes = R.drawable.ic_wrench,
                                    )
                                }
                                adapter.submitList(items)
                                binding.serviceEmptyState.setEmptyState(items.isEmpty(), binding.serviceTimelineList)
                            }
                            is Resource.Error -> {
                                binding.loadingIndicator.visibility = View.GONE
                                binding.serviceEmptyState.setEmptyState(true, binding.serviceTimelineList)
                                Snackbar.make(binding.root, "Gagal memuat data riwayat servis", Snackbar.LENGTH_SHORT).show()
                            }
                            is Resource.Loading -> {
                                binding.loadingIndicator.visibility = View.VISIBLE
                                binding.serviceEmptyState.setEmptyState(false, binding.serviceTimelineList)
                            }
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
