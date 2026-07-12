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
import com.example.kendaraanbp1.databinding.FragmentServiceHistoryBinding
import com.example.kendaraanbp1.ui.util.BottomNavTab
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarMargin
import com.example.kendaraanbp1.ui.util.bind
import com.example.kendaraanbp1.ui.util.setActiveTab
import com.example.kendaraanbp1.ui.util.setEmptyState
import com.example.kendaraanbp1.ui.util.setupNavigation

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

        binding.bottomNavInclude.root.applyBottomSystemBarMargin()
        binding.bottomNavInclude.setActiveTab(BottomNavTab.VEHICLES)
        binding.bottomNavInclude.setupNavigation(findNavController())

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.searchButton.setOnClickListener {
            // TODO: show service search once implemented.
        }
        binding.addServiceFab.setOnClickListener {
            // TODO: show add-service entry sheet once implemented.
        }

        val adapter = ServiceTimelineAdapter()
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
                    serviceViewModel.serviceLogs.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val logs = resource.data ?: emptyList()
                                val items = logs.map { log ->
                                    ServiceTimelineItem(
                                        id = log.id,
                                        title = log.category,
                                        workshopName = log.workshopName,
                                        statusLabel = getString(R.string.service_status_done),
                                        dateDistanceLabel = "${dateFormat.format(Date(log.date))} • ${log.odometer} km",
                                        totalCostLabel = "Rp ${log.totalCost.toLong()}",
                                        iconRes = R.drawable.ic_wrench,
                                    )
                                }
                                adapter.submitList(items)
                                binding.serviceEmptyState.setEmptyState(items.isEmpty(), binding.serviceTimelineList)
                            }
                            is Resource.Error -> {
                                binding.serviceEmptyState.setEmptyState(true, binding.serviceTimelineList)
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
