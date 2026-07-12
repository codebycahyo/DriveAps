package com.example.kendaraanbp1.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.data.model.ActivityItem
import com.example.kendaraanbp1.data.model.ReminderItem
import com.example.kendaraanbp1.data.model.ReminderSeverity
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.databinding.FragmentHomeDashboardBinding
import com.example.kendaraanbp1.ui.home.HomeViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.example.kendaraanbp1.databinding.ItemQuickActionBinding
import com.example.kendaraanbp1.ui.addfuel.AddFuelEntryFragment
import com.example.kendaraanbp1.ui.util.BottomNavTab
import com.example.kendaraanbp1.ui.util.VerticalSpaceItemDecoration
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarMargin
import com.example.kendaraanbp1.ui.util.applyTopSystemBarPadding
import com.example.kendaraanbp1.ui.util.bind
import com.example.kendaraanbp1.ui.util.setActiveTab
import com.example.kendaraanbp1.ui.util.setEmptyState
import com.example.kendaraanbp1.ui.util.setupNavigation

class HomeDashboardFragment : Fragment() {

    private var _binding: FragmentHomeDashboardBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.applyTopSystemBarPadding()
        binding.bottomNavInclude.root.applyBottomSystemBarMargin()
        binding.bottomNavInclude.setActiveTab(BottomNavTab.HOME)
        binding.bottomNavInclude.setupNavigation(findNavController())

        setUpQuickActions()
        
        val activityAdapter = ActivityAdapter()
        binding.activityList.layoutManager = LinearLayoutManager(requireContext())
        binding.activityList.adapter = activityAdapter
        binding.activityList.addItemDecoration(
            VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_2))
        )

        val reminderAdapter = ReminderAdapter()
        binding.reminderList.layoutManager = LinearLayoutManager(requireContext())
        binding.reminderList.adapter = reminderAdapter
        binding.reminderList.addItemDecoration(
            VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_12))
        )
        
        setUpActivityState()
        setUpReminderState()
        observeViewModels(activityAdapter, reminderAdapter)

        binding.activeVehicleSelector.setOnClickListener {
            showVehicleSelectionDialog()
        }

        binding.quickActionFuel.root.setOnClickListener {
            AddFuelEntryFragment().show(childFragmentManager, AddFuelEntryFragment.TAG)
        }

        binding.quickActionDocument.root.setOnClickListener {
            findNavController().navigate(R.id.action_homeDashboardFragment_to_vehicleDocumentsFragment)
        }

        binding.quickActionService.root.setOnClickListener {
            findNavController().navigate(R.id.action_homeDashboardFragment_to_serviceHistoryFragment)
        }
    }

    private fun setUpQuickActions() {
        bindQuickAction(binding.quickActionFuel, R.drawable.ic_fuel, getString(R.string.quick_action_fuel))
        bindQuickAction(binding.quickActionService, R.drawable.ic_wrench, getString(R.string.quick_action_service))
        bindQuickAction(binding.quickActionScan, R.drawable.ic_camera, getString(R.string.quick_action_scan))
        bindQuickAction(binding.quickActionDocument, R.drawable.ic_file_text, getString(R.string.quick_action_document))
    }

    private fun bindQuickAction(itemBinding: ItemQuickActionBinding, iconRes: Int, label: String) {
        itemBinding.quickActionIcon.setImageResource(iconRes)
        itemBinding.quickActionLabel.text = label
    }

    private fun setUpActivityState() {
        binding.activityEmptyState.bind(
            R.drawable.ic_inbox,
            R.string.empty_activity_title,
            R.string.empty_activity_subtitle,
        )
    }

    private fun setUpReminderState() {
        binding.reminderEmptyState.bind(
            R.drawable.ic_inbox,
            R.string.empty_reminder_title,
            R.string.empty_reminder_subtitle,
        )
    }
    
    private fun observeViewModels(activityAdapter: ActivityAdapter, reminderAdapter: ReminderAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.selectedVehicleId.collect { id ->
                        homeViewModel.setVehicleId(id)
                    }
                }
                
                launch {
                    homeViewModel.recentActivities.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                // show loading state if needed
                            }
                            is Resource.Success -> {
                                val items = resource.data ?: emptyList()
                                activityAdapter.submitList(items)
                                binding.activityEmptyState.setEmptyState(items.isEmpty(), binding.activityList)
                            }
                            is Resource.Error -> {
                                // show error state if needed
                                binding.activityEmptyState.setEmptyState(true, binding.activityList)
                            }
                        }
                    }
                }
                
                launch {
                    homeViewModel.reminders.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                // show loading state if needed
                            }
                            is Resource.Success -> {
                                val items = resource.data ?: emptyList()
                                reminderAdapter.submitList(items)
                                binding.reminderEmptyState.setEmptyState(items.isEmpty(), binding.reminderList)
                            }
                            is Resource.Error -> {
                                // show error
                            }
                        }
                    }
                }
                
                launch {
                    sharedViewModel.allVehicles.collect { resource ->
                        if (resource is Resource.Success) {
                            val vehicles = resource.data ?: emptyList()
                            val selectedId = sharedViewModel.selectedVehicleId.value
                            val selectedVehicle = vehicles.find { it.id == selectedId }
                            if (selectedVehicle != null) {
                                binding.homeVehicleName.text = "${selectedVehicle.brand} ${selectedVehicle.model}"
                            }
                        }
                    }
                }
                
                launch {
                    sharedViewModel.selectedVehicleId.collect { id ->
                        val resource = sharedViewModel.allVehicles.value
                        if (resource is Resource.Success) {
                            val vehicles = resource.data ?: emptyList()
                            val selectedVehicle = vehicles.find { it.id == id }
                            if (selectedVehicle != null) {
                                binding.homeVehicleName.text = "${selectedVehicle.brand} ${selectedVehicle.model}"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showVehicleSelectionDialog() {
        val resource = sharedViewModel.allVehicles.value
        if (resource is Resource.Success) {
            val vehicles = resource.data ?: emptyList()
            if (vehicles.isEmpty()) {
                findNavController().navigate(R.id.action_homeDashboardFragment_to_addVehicleFragment)
                return
            }
            
            val vehicleNames = vehicles.map { "${it.brand} ${it.model}" }.toMutableList()
            vehicleNames.add("+ Tambah Kendaraan")
            
            AlertDialog.Builder(requireContext())
                .setTitle("Pilih Kendaraan")
                .setItems(vehicleNames.toTypedArray()) { dialog, which ->
                    if (which == vehicles.size) {
                        findNavController().navigate(R.id.action_homeDashboardFragment_to_addVehicleFragment)
                    } else {
                        val selectedVehicle = vehicles[which]
                        sharedViewModel.selectVehicle(selectedVehicle.id)
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
