package com.example.kendaraanbp1.ui.vehicledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.vehicledetail.VehicleDetailViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.ui.addfuel.AddFuelEntryFragment
import com.example.kendaraanbp1.data.model.VehicleActivityItem
import com.example.kendaraanbp1.databinding.FragmentVehicleDetailBinding
import com.example.kendaraanbp1.ui.util.BottomNavTab
import com.example.kendaraanbp1.ui.util.VerticalSpaceItemDecoration
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarMargin
import com.example.kendaraanbp1.ui.util.applyTopSystemBarPadding
import com.example.kendaraanbp1.ui.util.bind
import com.example.kendaraanbp1.ui.util.setActiveTab
import com.example.kendaraanbp1.ui.util.setEmptyState
import com.example.kendaraanbp1.ui.util.setupNavigation
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class VehicleDetailFragment : Fragment() {

    private var _binding: FragmentVehicleDetailBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val detailViewModel: VehicleDetailViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVehicleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.applyTopSystemBarPadding()
        binding.bottomNavInclude.root.applyBottomSystemBarMargin()
        binding.bottomNavInclude.setActiveTab(BottomNavTab.VEHICLES)
        binding.bottomNavInclude.setupNavigation(findNavController())

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.action_vehicleDetailFragment_to_editVehicleFragment)
        }
        binding.addActivityFab.setOnClickListener {
            AddFuelEntryFragment().show(childFragmentManager, AddFuelEntryFragment.TAG)
        }

        setUpTabs()
        
        val adapter = VehicleActivityAdapter()
        binding.activityList.layoutManager = LinearLayoutManager(requireContext())
        binding.activityList.adapter = adapter
        binding.activityList.addItemDecoration(
            VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_8))
        )
        
        setUpActivityState()
        observeViewModels(adapter)
    }

    private fun setUpTabs() {
        val tabs = binding.activityTabs
        tabs.addTab(tabs.newTab().setText(R.string.tab_all_activity))
        tabs.addTab(tabs.newTab().setText(R.string.tab_fuel))
        tabs.addTab(tabs.newTab().setText(R.string.tab_service))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    1 -> findNavController().navigate(R.id.action_vehicleDetailFragment_to_fuelHistoryFragment)
                    2 -> findNavController().navigate(R.id.action_vehicleDetailFragment_to_serviceHistoryFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun setUpActivityState() {
        binding.activityEmptyState.bind(
            R.drawable.ic_inbox,
            R.string.empty_vehicle_activity_title,
            R.string.empty_vehicle_activity_subtitle,
        )
    }
    
    private fun observeViewModels(adapter: VehicleActivityAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.selectedVehicleId.collect { id ->
                        detailViewModel.setVehicleId(id)
                    }
                }
                
                launch {
                    detailViewModel.vehicleDetails.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                resource.data?.let { vehicle ->
                                    binding.topAppBar.findViewById<com.example.kendaraanbp1.ui.widget.DTextView>(R.id.vehicleName)?.text = "${vehicle.brand} ${vehicle.model}"
                                    binding.root.findViewById<com.example.kendaraanbp1.ui.widget.DTextView>(R.id.vehiclePlate)?.text = vehicle.plateNumber
                                    // other details can be populated here if there are UI elements
                                }
                            }
                            else -> {}
                        }
                    }
                }
                
                launch {
                    detailViewModel.vehicleStats.collect { stats ->
                        binding.statFuelValue.text = stats.totalFuelLabel
                        binding.statServiceValue.text = stats.serviceCountLabel
                        binding.statOdometerValue.text = stats.odometerLabel
                    }
                }

                launch {
                    detailViewModel.vehicleActivities.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val items = resource.data ?: emptyList()
                                adapter.submitList(items)
                                binding.activityEmptyState.setEmptyState(items.isEmpty(), binding.activityList)
                            }
                            is Resource.Error -> {
                                binding.activityEmptyState.setEmptyState(true, binding.activityList)
                                Snackbar.make(binding.root, "Gagal memuat aktivitas kendaraan", Snackbar.LENGTH_SHORT).show()
                            }
                            is Resource.Loading -> {
                                binding.activityEmptyState.setEmptyState(false, binding.activityList)
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
